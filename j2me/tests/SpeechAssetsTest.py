#!/usr/bin/env python3
"""Every original WAV classified; speech banks independently matched to reviewed text."""
from pathlib import Path
import json,hashlib,struct,zipfile,sys
ROOT=Path(__file__).resolve().parents[1];catalog=json.loads((ROOT/'speech-catalog.json').read_text());samples=catalog['samples'];expected={a:s for s in samples for a in s['aliases']};report=[]
for profile in ['128x128','128x160','240x320']:
 with zipfile.ZipFile((Path(sys.argv[1]) if len(sys.argv)>1 else ROOT/'dist')/('Pilots-'+profile+'.jar')) as jar:
  names={n for n in jar.namelist() if n.endswith('.wav')};assert names==set(expected),'Unclassified sound files'
  for name in names:assert hashlib.sha256(jar.read(name)).hexdigest()==expected[name]['wav_sha256']
  bindings=0
  for scene in range(2,18):
   rows={int(n[-6:-4]):s for n,s in expected.items() if n.startswith('audio/%02d-'%scene) and s['kind']=='speech'}
   data=jar.read('scenes/%02d.speech'%scene);assert data[:4]==b'SPH1';assert struct.unpack_from('>H',data,4)[0]==len(rows);p=6;seen=set()
   for i in rows:
    cue,ms,size=struct.unpack_from('>BHH',data,p);p+=5;text=data[p:p+size].decode('utf-8');p+=size;sample=rows[cue];assert text==sample['text'] and ms==int(sample['duration']*1000+.5);assert cue not in seen;seen.add(cue);bindings+=1
   assert p==len(data) and seen==set(rows) and 6 in rows and 7 in rows
  report.append(dict(profile=profile,original_wavs=len(names),unique_samples=len(samples),speech_samples=sum(s['kind']=='speech' for s in samples),speech_bindings=bindings))
print('SpeechAssets: every original WAV classified, immutable audio hashes and all scene speech banks matched to reviewed Russian text')
(ROOT/'analysis/speech-assets-audit.json').write_text(json.dumps(report,indent=2)+'\n')
