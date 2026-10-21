#!/usr/bin/env python3
"""Compile and CLDC-preverify the playable MIDlet, then package both profiles."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import urllib.request
import zipfile

sys.dont_write_bytecode = True
import build as resources
from extract_window_gameplay import extract
from extract_barrel_gameplay import extract as extract_barrel
from extract_first_location import extract as extract_first_location

ROOT = Path(__file__).resolve().parent
VERSION = '0.5.0'


def run(command):
    subprocess.run([str(x) for x in command], check=True)


def toolchain(cache):
    cache.mkdir(parents=True, exist_ok=True)
    result = {}
    for item in json.loads((ROOT / 'toolchain.json').read_text()):
        path = cache / item['filename']
        if not path.exists():
            data = urllib.request.urlopen(item['url'], timeout=60).read()
            resources.require(hashlib.sha256(data).hexdigest() == item['sha256'], 'Tool download hash mismatch')
            path.write_bytes(data)
        resources.require(hashlib.sha256(path.read_bytes()).hexdigest() == item['sha256'], 'Cached tool hash mismatch: ' + str(path))
        result[item['filename']] = path
    return result


def compile_game(java, javac, tools, work, evidence, barrel, full_binary, full_report):
    api = [tools['cldcapi11-2.0.4.jar'], tools['midpapi20-2.0.4.jar']]
    raw = work / 'raw'
    run([java, '-jar', tools['ecj-4.6.1.jar'], '-proc:none', '-encoding', 'UTF-8',
         '-1.3', '-target', '1.1', '-bootclasspath', os.pathsep.join(map(str, api)),
         '-d', raw] + sorted((ROOT / 'src/pilots').glob('*.java')))
    preverified = work / 'preverified.jar'
    run([java, '-jar', tools['proguard-base-6.2.2.jar'], '-injars', raw, '-outjars', preverified,
         '-libraryjars', api[0], '-libraryjars', api[1], '-microedition', '-target', '1.1',
         '-dontshrink', '-dontoptimize', '-dontobfuscate', '-keep', 'class pilots.** { *; }'])
    # Pure gameplay tests run on a desktop JVM; no device/playback claim.
    test_classes = work / 'tests'
    run([javac, '-encoding', 'UTF-8', '-d', test_classes,
         ROOT / 'src/pilots/GameState.java', ROOT / 'src/pilots/ZooLogic.java', ROOT / 'src/pilots/OriginalZooRules.java', ROOT / 'src/pilots/ZooWalk.java', ROOT / 'tests/ZooLogicTest.java', ROOT / 'tests/BarrelLogicTest.java', ROOT / 'tests/OriginalRoutingTest.java'])
    (test_classes / 'scenes').mkdir(parents=True, exist_ok=True)
    (test_classes / 'scenes/02.rules').write_bytes(full_binary)
    fixture = work / 'source-window-cases.csv'
    fixture.write_text('\n'.join(','.join(map(str, row)) for row in evidence['fixtures']) + '\n')
    run([java, '-cp', test_classes, 'pilots.ZooLogicTest', fixture])
    barrel_fixture = work / 'barrel-cases.csv'
    cat_fixture = work / 'cat-cases.csv'
    for path, rows in [(barrel_fixture, barrel['fixtures']), (cat_fixture, barrel['cat_cases'])]:
        path.write_text('\n'.join(','.join(map(str,row)) for row in rows) + '\n')
    run([java, '-cp', test_classes, 'pilots.BarrelLogicTest', barrel_fixture, cat_fixture])
    routes_fixture = work / 'full-location-cases.csv'
    routes_fixture.write_text('\n'.join(','.join(map(str,row)) for row in full_report['fixtures']) + '\n')
    run([java, '-cp', test_classes, 'pilots.OriginalRoutingTest', routes_fixture])
    with zipfile.ZipFile(preverified) as archive:
        return {n: archive.read(n) for n in archive.namelist() if n.endswith('.class')}


def package(profile, classes, sprites, output):
    source = ROOT / 'input' / ('Pilots-' + profile + '.jar')
    locked = json.loads((ROOT / 'baselines.json').read_text())
    resources.require(hashlib.sha256(source.read_bytes()).hexdigest() == locked[profile], 'Baseline hash mismatch')
    with zipfile.ZipFile(source) as archive:
        baseline = {n: archive.read(n) for n in archive.namelist()}
    entries, changes = resources.adjust_animations(baseline, json.loads((ROOT / 'animation-adjustments.json').read_text()))
    entries = {n: d for n, d in entries.items() if not n.endswith('.class')}
    entries.update(classes)
    entries.update(sprites)
    manifest = resources.manifest(entries['META-INF/MANIFEST.MF'])
    manifest['MIDlet-Version'] = VERSION
    entries['META-INF/MANIFEST.MF'] = ('\r\n'.join(k + ': ' + v for k, v in manifest.items()) + '\r\n\r\n').encode()
    report = resources.validate(entries, profile)
    # Existing animation payloads and source profile rasters must remain byte-identical.
    resources.require(all(d == baseline[n] for n, d in entries.items() if n in baseline and n.endswith(('.anm', '.p8', '.bar'))), 'Existing visual resource changed')
    output.mkdir(parents=True, exist_ok=True)
    target = output / source.name
    with zipfile.ZipFile(target, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
        for name in sorted(entries, key=lambda n: (n != 'META-INF/MANIFEST.MF', n)):
            info = zipfile.ZipInfo(name, date_time=(2000, 1, 1, 0, 0, 0))
            info.create_system = 3
            info.external_attr = 0o100644 << 16
            archive.writestr(info, entries[name], compress_type=zipfile.ZIP_DEFLATED, compresslevel=9)
    with zipfile.ZipFile(target) as archive:
        resources.require(archive.testzip() is None, 'Output CRC mismatch')
        resources.require({n: archive.read(n) for n in archive.namelist()} == entries, 'Output payload mismatch')
    jad = {k: v for k, v in manifest.items() if k != 'Manifest-Version'}
    jad.update({'MIDlet-Jar-URL': target.name, 'MIDlet-Jar-Size': str(target.stat().st_size)})
    target.with_suffix('.jad').write_bytes(('\r\n'.join(k + ': ' + v for k, v in jad.items()) + '\r\n').encode())
    report.update(version=VERSION, jar_size=target.stat().st_size,
                  jar_sha256=hashlib.sha256(target.read_bytes()).hexdigest(), bytecode_preserved=False,
                  compilation='ECJ 4.6.1: source 1.3 / target 1.1; CLDC 1.1 + MIDP 2.0 APIs',
                  preverification='ProGuard 6.2.2 -microedition; no shrinking, optimization or obfuscation',
                  original_window_rule_oracle_cases=8, existing_visual_payloads_preserved=True, original_barrel_rule_oracle_cases=26, original_cat_rule_oracle_cases=7, original_full_location_route_cases=1024, added_original_sprite_ids=list(range(38))+[44], code_defined_hotspots={'scene_02_window': [150, 224, 68, 66], 'scene_02_kettle': [80, 284, 38, 36], 'scene_02_barrel': [128, 282, 64, 68], 'scene_02_pickaxe': [80, 248, 38, 36], 'scene_02_coin': [250, 265, 70, 45], 'scene_02_bell': [440, 205, 40, 65], 'scene_02_gate': [248, 155, 187, 125], 'scene_02_wall': [470, 180, 170, 110]}, validation='Gameplay state tests, headless resource wiring tests and static packaging checks; no emulator or device test',
                  **changes)
    target.with_suffix('.report.json').write_text(json.dumps(report, indent=2) + '\n')
    print(profile + ': built ' + str(target) + ' (' + str(report['classes']) + ' preverified classes)')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--profile', choices=['all', '128x128', '240x320'], default='all')
    parser.add_argument('--out', type=Path, default=ROOT / 'dist')
    parser.add_argument('--tool-cache', type=Path, default=Path(os.environ.get('PILOTS_TOOL_CACHE', '/tmp/pilots-j2me-tools')))
    args = parser.parse_args()
    java_home = os.environ.get('JAVA_HOME')
    java = str(Path(java_home) / 'bin/java') if java_home else shutil.which('java')
    javac = str(Path(java_home) / 'bin/javac') if java_home else shutil.which('javac')
    resources.require(bool(java and javac), 'A full JDK is required; set JAVA_HOME')
    output = args.out.resolve()
    resources.require(output != ROOT / 'input' and ROOT / 'input' not in output.parents, 'Do not overwrite input/')
    tools = toolchain(args.tool_cache.resolve())
    with zipfile.ZipFile(ROOT / 'input/Pilots-240x320.jar') as archive:
        palette = archive.read('scenes/02-64.anm')[9:777]
    sprites, evidence = extract((ROOT.parent / 'PILOTS.EXE').read_bytes(), palette)
    (ROOT / 'analysis/window-original-rules.json').write_text(json.dumps(evidence, indent=2) + '\n')
    barrel = extract_barrel((ROOT.parent / 'PILOTS.EXE').read_bytes())
    (ROOT / 'analysis/barrel-original-rules.json').write_text(json.dumps(barrel, indent=2) + '\n')
    full_binary, full_report = extract_first_location((ROOT.parent / 'PILOTS.EXE').read_bytes())
    sprites['scenes/02.rules'] = full_binary
    (ROOT / 'analysis/first-location-original-rules.json').write_text(json.dumps(full_report, indent=2) + '\n')
    with tempfile.TemporaryDirectory(prefix='pilots-build-') as temporary:
        classes = compile_game(java, javac, tools, Path(temporary), evidence, barrel, full_binary, full_report)
        for profile in ['128x128', '240x320'] if args.profile == 'all' else [args.profile]:
            package(profile, classes, sprites, output)
            test_classes = Path(temporary) / 'adapter-tests'
            target = output / ('Pilots-' + profile + '.jar')
            classpath = os.pathsep.join(map(str, [target, tools['midpapi20-2.0.4.jar']]))
            run([javac, '-encoding', 'UTF-8', '-cp', classpath, '-d', test_classes,
                 ROOT / 'tests/ZooSceneTest.java', ROOT / 'tests/BarrelSceneTest.java', ROOT / 'tests/FirstLocationSceneTest.java'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.ZooSceneTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.BarrelSceneTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.FirstLocationSceneTest'])
            panel_classes=Path(temporary) / 'panel-tests'
            run([javac,'-encoding','UTF-8','-cp',classpath,'-d',panel_classes,
                 ROOT / 'tests/stubs/javax/microedition/lcdui/Graphics.java',ROOT / 'tests/PanelRenderTest.java'])
            width,height=profile.split('x')
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.PanelRenderTest',width,height,ROOT / ('analysis/panel-'+profile+'-'+VERSION+'.png')])


if __name__ == '__main__':
    main()
