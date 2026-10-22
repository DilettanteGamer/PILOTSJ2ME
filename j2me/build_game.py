#!/usr/bin/env python3
"""Compile and CLDC-preverify the playable MIDlet, then package selected profiles."""
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
from animation_codec import encode_animation, decode_animation
import build as resources
from extract_window_gameplay import extract
from extract_barrel_gameplay import extract as extract_barrel
from extract_first_location import extract as extract_first_location
from extract_subtitles import extract as extract_subtitles
from extract_courtyard import extract as extract_courtyard
from extract_audio import extract as extract_audio
from extract_animals import extract as extract_animals

ROOT = Path(__file__).resolve().parent
VERSION = '0.9.4'
PROFILES = json.loads((ROOT / 'profiles.json').read_text())


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


def compile_game(java, javac, tools, work, evidence, barrel, full_binary, full_report, subtitle_binary):
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
         ROOT / 'src/pilots/SoundSink.java',ROOT / 'src/pilots/AudioCues.java',ROOT / 'src/pilots/ZooSubtitles.java', ROOT / 'src/pilots/GameState.java', ROOT / 'src/pilots/ZooLogic.java', ROOT / 'src/pilots/OriginalZooRules.java', ROOT / 'src/pilots/ZooWalk.java', ROOT / 'tests/ZooLogicTest.java', ROOT / 'tests/BarrelLogicTest.java', ROOT / 'tests/OriginalRoutingTest.java',ROOT / 'tests/WalkingRulesTest.java'])
    (test_classes / 'scenes').mkdir(parents=True, exist_ok=True)
    (test_classes / 'scenes/02.rules').write_bytes(full_binary)
    (test_classes / 'scenes/02.subtitles').write_bytes(subtitle_binary)
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
    walking_fixture=work / 'source-walking-cases.csv'
    walking_fixture.write_text('\n'.join(','.join(map(str,row)) for row in full_report['walking_cases'])+'\n')
    run([java,'-cp',test_classes,'pilots.WalkingRulesTest',walking_fixture])
    with zipfile.ZipFile(preverified) as archive:
        return {n: archive.read(n) for n in archive.namelist() if n.endswith('.class')}


def package(profile, classes, sprites, output):
    config = PROFILES[profile]
    baseline_profile = config['baseline']
    source = ROOT / 'input' / ('Pilots-' + baseline_profile + '.jar')
    locked = json.loads((ROOT / 'baselines.json').read_text())
    resources.require(hashlib.sha256(source.read_bytes()).hexdigest() == locked[baseline_profile], 'Baseline hash mismatch')
    with zipfile.ZipFile(source) as archive:
        baseline = {n: archive.read(n) for n in archive.namelist()}
    entries, changes = resources.adjust_animations(baseline, json.loads((ROOT / 'animation-adjustments.json').read_text()))
    entries = {n: d for n, d in entries.items() if not n.endswith('.class')}
    entries.update(classes)
    entries.update(sprites)
    # Only these three locations are playable; keep future assets in input/EXE.
    removed_future_resources=[]
    for name in list(entries):
        if name.startswith('scenes/') and name[7:9].isdigit() and int(name[7:9]) not in (2,3,4):
            removed_future_resources.append(name)
            del entries[name]
    entries['catalog.bin']=b'PLT2\x00\x03\x02\x03\x04'
    packed_clips=[]
    inflated_bytes_saved=0
    for name,data in list(entries.items()):
        if name.endswith('.anm'):
            stored=encode_animation(data)
            if stored!=data:
                packed_clips.append(name)
                inflated_bytes_saved+=len(data)-len(stored)
                entries[name]=stored
    entries.pop('scenes/02.animations',None)
    entries.pop('scenes/03.animations',None)
    changes['pending_placement_menu_numbers']=[]
    changes['pending_character_relative_menu_numbers']=[]
    manifest = resources.manifest(entries['META-INF/MANIFEST.MF'])
    manifest['MIDlet-Version'] = VERSION
    manifest['MIDlet-Name'] = config['midlet_name']
    manifest['MIDlet-1'] = config['midlet_name'] + ',,pilots.PilotsMidlet'
    manifest['Pilots-Screen'] = profile
    manifest['Pilots-Profile'] = profile
    manifest['Pilots-Raster-Cache-Pixels'] = str(config['raster_cache_pixels'])
    entries['META-INF/MANIFEST.MF'] = ('\r\n'.join(k + ': ' + v for k, v in manifest.items()) + '\r\n\r\n').encode()
    report = resources.validate(entries, profile)
    # Decoded animation content and retained profile rasters must remain byte-identical.
    resources.require(all((decode_animation(d) if n.endswith('.anm') else d) == baseline[n] for n, d in entries.items() if n in baseline and n.endswith(('.anm', '.p8', '.bar'))), 'Existing visual content changed')
    output.mkdir(parents=True, exist_ok=True)
    target = output / ('Pilots-' + profile + '.jar')
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
    report.update(audio_wav_count=125,audio_midi_count=3,audio_event_tables='Original bytecode predicates and animal yard VM; audio-original-cues.json',audio_device_tested=False,playable_scene_ids=[2,3,4],active_preview_scene=4,version=VERSION, jar_size=target.stat().st_size,
                  jar_sha256=hashlib.sha256(target.read_bytes()).hexdigest(), bytecode_preserved=False,
                  compilation='ECJ 4.6.1: source 1.3 / target 1.1; CLDC 1.1 + MIDP 2.0 APIs',
                  preverification='ProGuard 6.2.2 -microedition; no shrinking, optimization or obfuscation',
                  original_window_rule_oracle_cases=8, existing_visual_payloads_preserved=True, original_barrel_rule_oracle_cases=26, original_cat_rule_oracle_cases=7, original_full_location_route_cases=1024, subtitle_cues=[6,7,34,35,36], original_walking_sprite_ids=list(range(20,34)), added_original_sprite_ids=list(range(38))+[44], code_defined_hotspots={'scene_02_window': [150, 224, 68, 66], 'scene_02_kettle': [80, 284, 38, 36], 'scene_02_barrel': [128, 282, 64, 68], 'scene_02_pickaxe': [80, 248, 38, 36], 'scene_02_coin': [299, 305, 28, 23], 'scene_02_bell': [440, 205, 40, 65], 'scene_02_gate': [248, 155, 187, 125], 'scene_02_wall': [470, 180, 170, 110]}, original_animal_section=6,original_animal_local_clips=93,animal_preview_count=46,save_format='PSV1 for scenes 02/03; PSV2 scene 04 AWS2 snapshot, AWS1 backward compatible reader',validation='Three-location gameplay and exit tests, source audio/PCM/MIDI conversion, MMAPI lifecycle spy, production panel/scene rendering and static packaging checks; no emulator or device test',
                  **changes)
    report['rgb_compatibility'] = {'model': 'full-stride drawRGB bounds check; desktop spy, not a device test',
                                 'animation_clips': 305, 'animation_frames': 3819,
                                 'scales': ['normal', 'zoomed'], 'edge_clipping': True,
                                 'screen_sizes': [list(map(int, profile.split('x'))), [128, 160]]}
    report['removed_future_resources']=sorted(removed_future_resources)
    report['lossless_frame_storage']={'packed_clips':packed_clips,'inflated_bytes_saved':inflated_bytes_saved,'decoded_visual_payloads_preserved':True}
    report['existing_visual_payloads_preserved']=False
    report['render_cache']={'small_profile_pixels':65536,'large_profile_pixels':131072,'walking_byte_cache_max':65536,'low_memory_fallback':True,'device_fps_measured':False}
    report['build_profile'] = dict(config, id=profile, original_input=source.name)
    target.with_suffix('.report.json').write_text(json.dumps(report, indent=2) + '\n')
    print(profile + ': built ' + str(target) + ' (' + str(report['classes']) + ' preverified classes)')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--profile', choices=['all'] + list(PROFILES), default='all')
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
    run([sys.executable,ROOT / 'tests/AudioExtractionTest.py'])
    with zipfile.ZipFile(ROOT / 'input/Pilots-240x320.jar') as archive:
        palette = archive.read('scenes/02-64.anm')[9:777]
    sprites, evidence = extract((ROOT.parent / 'PILOTS.EXE').read_bytes(), palette)
    (ROOT / 'analysis/window-original-rules.json').write_text(json.dumps(evidence, indent=2) + '\n')
    barrel = extract_barrel((ROOT.parent / 'PILOTS.EXE').read_bytes())
    (ROOT / 'analysis/barrel-original-rules.json').write_text(json.dumps(barrel, indent=2) + '\n')
    full_binary, full_report = extract_first_location((ROOT.parent / 'PILOTS.EXE').read_bytes())
    subtitle_binary,subtitle_report=extract_subtitles((ROOT.parent / 'PILOTS.EXE').read_bytes())
    sprites['scenes/02.subtitles']=subtitle_binary
    (ROOT / 'analysis/subtitle-original-cues.json').write_text(json.dumps(subtitle_report,ensure_ascii=False,indent=2)+'\n')
    courtyard_sprites,courtyard_report=extract_courtyard((ROOT.parent / 'PILOTS.EXE').read_bytes())
    sprites.update(courtyard_sprites)
    animal_resources,animal_report=extract_animals((ROOT.parent / 'PILOTS.EXE').read_bytes())
    sprites.update(animal_resources)
    (ROOT / 'analysis/animals-original-rules.json').write_text(json.dumps(animal_report,ensure_ascii=False,indent=2)+'\n')
    audio_resources,audio_report=extract_audio((ROOT.parent / 'PILOTS.EXE').read_bytes())
    sprites.update(audio_resources)
    (ROOT / 'analysis/audio-original-cues.json').write_text(json.dumps(audio_report,ensure_ascii=False,indent=2)+'\n')
    (ROOT / 'analysis/courtyard-original-rules.json').write_text(json.dumps(courtyard_report,ensure_ascii=False,indent=2)+'\n')
    sprites['scenes/02.rules'] = full_binary
    (ROOT / 'analysis/first-location-original-rules.json').write_text(json.dumps(full_report, indent=2) + '\n')
    with tempfile.TemporaryDirectory(prefix='pilots-build-') as temporary:
        classes = compile_game(java, javac, tools, Path(temporary), evidence, barrel, full_binary, full_report, subtitle_binary)
        for profile in PROFILES if args.profile == 'all' else [args.profile]:
            package(profile, classes, sprites, output)
            test_classes = Path(temporary) / 'adapter-tests'
            target = output / ('Pilots-' + profile + '.jar')
            classpath = os.pathsep.join(map(str, [target, tools['midpapi20-2.0.4.jar']]))
            run([javac, '-encoding', 'UTF-8', '-cp', classpath, '-d', test_classes,
                 ROOT / 'tests/ZooSceneTest.java', ROOT / 'tests/BarrelSceneTest.java', ROOT / 'tests/FirstLocationSceneTest.java',ROOT / 'tests/ZooPresentationTest.java',ROOT / 'tests/CourtyardSceneTest.java',ROOT / 'tests/CourtyardRulesTest.java',ROOT / 'tests/AnimalWorldTest.java',ROOT / 'tests/AnimalVisibilityTest.java'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.ZooSceneTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.BarrelSceneTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.FirstLocationSceneTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.ZooPresentationTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.CourtyardSceneTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.AnimalWorldTest'])
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.AnimalVisibilityTest', ROOT / 'tests/fixtures/animal-0.9.3-entry.psv', ROOT / 'tests/fixtures/animal-0.9.3-pickup.psv'])
            courtyard_fixture=Path(temporary) / 'courtyard-source-actions.csv'
            courtyard_fixture.write_text('\n'.join(','.join(map(str,row)) for row in courtyard_report['actions'])+'\n')
            run([java, '-cp', str(test_classes) + os.pathsep + classpath, 'pilots.CourtyardRulesTest',courtyard_fixture])
            audio_classes=Path(temporary) / 'audio-tests'
            run([javac,'-encoding','UTF-8','-cp',classpath,'-d',audio_classes,
                 ROOT / 'tests/stubs/javax/microedition/media/MediaException.java',ROOT / 'tests/stubs/javax/microedition/media/Manager.java',ROOT / 'tests/AudioTest.java'])
            run([java,'-cp',str(audio_classes)+os.pathsep+classpath,'pilots.AudioTest']+[audio_report['scenes'][str(n)]['music']['event_sha256'] for n in [2,3,4]])
            panel_classes=Path(temporary) / 'panel-tests'
            run([javac,'-encoding','UTF-8','-cp',classpath,'-d',panel_classes,
                 ROOT / 'tests/stubs/javax/microedition/lcdui/Graphics.java',ROOT / 'tests/stubs/javax/microedition/lcdui/Image.java',ROOT / 'tests/PanelRenderTest.java',ROOT / 'tests/ZooGeometryTest.java',ROOT / 'tests/CourtyardRenderTest.java',ROOT / 'tests/AnimalRenderTest.java',ROOT / 'tests/RGBCompatibilityTest.java',ROOT / 'tests/RenderPerformanceTest.java',ROOT / 'tests/AnimationDecodeTest.java',ROOT / 'tests/ScrollRenderTest.java'])
            frame_hashes=Path(temporary) / ('frame-hashes-'+profile+'.properties')
            with zipfile.ZipFile(target) as archive:
                frame_hashes.write_text(''.join(name+'='+hashlib.sha256(decode_animation(archive.read(name))[777:]).hexdigest()+'\n' for name in archive.namelist() if name.endswith('.anm')))
                header=decode_animation(archive.read('scenes/02-20000.anm'))[:777]
            import struct
            for name,payload in [('overflow',b'\x81\x01'),('literal',b'\x02\x01'),('incomplete',b'\x00\x01'),('size',b'\x00\x01\x00')]:
                fixture=b'ANM3'+struct.pack('>H',1)+header[6:777]+struct.pack('>hhHHI',0,0,3,1,len(payload))+payload
                (panel_classes / ('bad-rle-'+name+'.anm')).write_bytes(fixture)
            run([java,'-cp',str(panel_classes)+os.pathsep+classpath,'pilots.AnimationDecodeTest',frame_hashes])
            width,height=profile.split('x')
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.ScrollRenderTest',width,height])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.RGBCompatibilityTest',target,width,height])
            # Also check Nokia C1-02 dimensions for the other install profiles.
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.RGBCompatibilityTest',target,'128','160'])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.RenderPerformanceTest',width,height])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.RenderPerformanceTest','128','160'])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.PanelRenderTest','128','160',Path(temporary) / ('nokia-panel-'+profile+'.png')])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.PanelRenderTest',width,height,ROOT / ('analysis/panel-'+profile+'-'+VERSION+'.png')])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.ZooGeometryTest',width,height,ROOT / ('analysis/geometry-'+profile+'-'+VERSION+'.png')])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.CourtyardRenderTest',width,height,ROOT / ('analysis/courtyard-'+profile+'-'+VERSION)])
            run([java,'-Djava.awt.headless=true','-cp',str(panel_classes)+os.pathsep+classpath,
                 'pilots.AnimalRenderTest',width,height,ROOT / ('analysis/animals-'+profile+'-'+VERSION)])
        if args.profile == 'all':
            run([sys.executable, ROOT / 'tests/BuildProfilesTest.py', output])


if __name__ == '__main__':
    main()
