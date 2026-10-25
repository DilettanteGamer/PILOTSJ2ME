#!/usr/bin/env python3
"""Bind reviewed Russian speech to original PCM hashes across the whole campaign."""
import json,hashlib,struct
from pathlib import Path
from build import require
ROOT=Path(__file__).resolve().parent

def extract(resources):
 settings=json.loads((ROOT/'speech-catalog.json').read_text());mapping={};audit=[]
 for sample in settings['samples']:
  require(sample['kind'] in ['speech','nonverbal_or_effect','unused_variant'],'Invalid sound classification')
  for name in sample['aliases']:
   require(name not in mapping,'Duplicate sound classification')
   require(name in resources and hashlib.sha256(resources[name]).hexdigest()==sample['wav_sha256'],'Speech source changed: '+name)
   mapping[name]=sample
 names={n for n in resources if n.endswith('.wav')};require(set(mapping)==names,'Unclassified original sounds')
 out={}
 for scene in range(2,18):
  rows=[]
  for name in sorted(names):
   sample=mapping[name]
   if name.startswith('audio/%02d-'%scene) and sample['kind']=='speech':
    cue=int(name[-6:-4]);text=sample['text'];ms=int(sample['duration']*1000+.5);rows.append((cue,ms,text));audit.append(dict(scene=scene,cue=cue,text=text,duration_ms=ms,wav_sha256=sample['wav_sha256']))
  data=bytearray(b'SPH1'+struct.pack('>H',len(rows)))
  for cue,ms,text in rows:
   b=text.encode('utf-8');data.extend(struct.pack('>BHH',cue,ms,len(b))+b)
  out['scenes/%02d.speech'%scene]=bytes(data)
 return out,dict(language='ru',original_wav_count=len(names),unique_pcm_count=len(settings['samples']),speech_samples=sum(s['kind']=='speech' for s in settings['samples']),bindings=audit)
