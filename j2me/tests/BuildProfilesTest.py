#!/usr/bin/env python3
"""Check independent install identities, JADs and profile settings in built artifacts."""
import hashlib
import json
from pathlib import Path
import sys
import zipfile

sys.dont_write_bytecode = True
ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))
from build import manifest

profiles = json.loads((ROOT / 'profiles.json').read_text())
output = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / 'dist'
identities = set()
engine = None
for profile, config in profiles.items():
    path = output / ('Pilots-' + profile + '.jar')
    with zipfile.ZipFile(path) as archive:
        mf = manifest(archive.read('META-INF/MANIFEST.MF'))
        current = {n: hashlib.sha256(archive.read(n)).hexdigest() for n in archive.namelist() if n.endswith('.class')}
    jad = manifest(path.with_suffix('.jad').read_bytes())
    assert all(jad[k] == v for k, v in mf.items() if k != 'Manifest-Version'), profile
    assert jad['MIDlet-Jar-URL'] == path.name and int(jad['MIDlet-Jar-Size']) == path.stat().st_size, profile
    assert mf['MIDlet-Name'] == config['midlet_name'], profile
    assert mf['MIDlet-1'].split(',')[0] == config['midlet_name'], profile
    assert mf['Pilots-Profile'] == mf['Pilots-Screen'] == profile, profile
    assert int(mf['Pilots-Raster-Cache-Pixels']) == config['raster_cache_pixels'], profile
    identity = (mf['MIDlet-Vendor'], mf['MIDlet-Name'])
    assert identity not in identities, 'Profiles would replace each other on installation'
    identities.add(identity)
    if engine is None:
        engine = current
    assert current == engine, 'Unexpected divergence in shared gameplay engine'
    report = json.loads(path.with_suffix('.report.json').read_text())
    assert report['build_profile']['screen'] == list(map(int, profile.split('x'))), profile
    assert report['build_profile']['baseline'] == config['baseline'], profile
print('BuildProfiles: three independent suite identities, matching JADs, settings and shared engine passed')
