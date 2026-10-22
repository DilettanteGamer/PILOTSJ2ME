#!/usr/bin/env python3
"""Check source PCM bytes and packed MIDI timing independently of the player."""
import io,struct,sys,wave
sys.dont_write_bytecode = True
from pathlib import Path
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
from extract_audio import extract,midi
from extract_scene02 import records
original=(Path(__file__).resolve().parents[2]/'PILOTS.EXE').read_bytes()
resources,report=extract(original)
common,_,_=records(original,1)
for scene,section in [(2,4),(3,5)]:
    local,_,_=records(original,section)
    for record in common+local:
        if record['code']!=0x51:continue
        count=struct.unpack_from('<I',record['data'])[0]
        with wave.open(io.BytesIO(resources['audio/%02d-%02d.wav'%(scene,record['id'])])) as wav:
            assert wav.readframes(count)==record['data'][4:4+count]
# Running status, second-byte delay suppression, source little-endian 15-bit delay.
stream=bytes.fromhex('b007ff 0740 ac02 c015 00 ff')
result,evidence=midi(struct.pack('<I',len(stream))+stream)
assert evidence['events']==3 and evidence['duration_ms']==3000
assert bytes.fromhex('00 b0077f 00 b00740 822c c015 00 ff2f00') in result
print('Audio extraction: lossless PCM wrapping and source MIDI running status/delta conversion passed')
