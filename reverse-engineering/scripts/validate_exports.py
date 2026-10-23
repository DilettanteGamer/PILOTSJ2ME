#!/usr/bin/env python3
"""Check exported analysis consistency, not behavioral equivalence to the game."""
import csv
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
GENERATED = ROOT / 'reverse-engineering/generated'
EXPECTED_INPUTS = {
    'PILOTS.EXE': 'c980fb1ad09c3543d89dd0be26c062b8847fab73e86b92012c4fe0507ba34e1f',
    'AUTORUN.EXE': '3232897c1d974e8e8ec48fdec6290c13995c86a3e616c349045f01f0f8ba2b0b',
    'GMW.DLL': '0e7a808f5ea230ec9bbd30ce37c8ebfd3ba5e849908c1bab1574356e89cdb550',
    'PILOTS0.230': 'c859600dbe0455fb189dfe52889b8a60b37933dc1a5f8e586aea36bec1951494',
}


def require(condition, message):
    if not condition:
        raise ValueError(message)


metadata = json.loads((GENERATED / 'inputs.json').read_text())
require({d['file'] for d in metadata} == set(EXPECTED_INPUTS), 'Input list mismatch')
for item in metadata:
    digest = hashlib.sha256((ROOT / item['file']).read_bytes()).hexdigest()
    require(digest == EXPECTED_INPUTS[item['file']] == item['sha256'], 'Input SHA-256 mismatch: ' + item['file'])

for binary in ['PILOTS.EXE', 'AUTORUN.EXE', 'GMW.DLL']:
    folder = GENERATED / binary
    report = json.loads((folder / 'analysis.json').read_text())
    with (folder / 'functions.tsv').open() as f:
        rows = list(csv.DictReader(f, delimiter='\t'))
    require(len(rows) == report['functions'] > 0, 'Missing functions: ' + binary)
    require(len({r['address'] for r in rows}) == len(rows), 'Duplicate function address')
    code = (folder / 'decompiled.c').read_text()
    for status, key in [('decompiled', 'decompiled'), ('thunk', 'thunks'), ('failed', 'failed')]:
        require(sum(row['status'] == status for row in rows) == report[key], 'Wrong count: ' + status)
    for row in rows:
        require(row['status'] in ['decompiled', 'thunk', 'failed'], 'Unexpected status')
        if row['status'] == 'decompiled':
            require('/* Address: ' + row['address'] + ' */' in code, 'Missing pseudocode')
    expected_errors = {'1000:07c7', '1000:07cd'} if binary == 'GMW.DLL' else set()
    require({r['address'] for r in rows if r['status'] == 'failed'} == expected_errors, 'Unexpected decompiler failure set')
    require({r['address'] for r in report['failures']} == expected_errors, 'Failure report mismatch')
    for filename in ['disassembly.tsv', 'symbols.tsv', 'strings.tsv']:
        require((folder / filename).stat().st_size > 0, 'Missing export: ' + filename)
    print(binary + ': export consistent; ' + str(report['decompiled']) + ' functions decompiled, ' + str(report['failed']) + ' known failures')

print('Input checksums and export consistency verified. No game behavior or recompilation tested.')
