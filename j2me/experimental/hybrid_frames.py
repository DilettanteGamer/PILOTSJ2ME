"""Native linear indexed frames for hot hero/intro cels, 16x16 tile banks elsewhere."""
from pathlib import Path
import struct
from local_tiles import group_images
ANIMAL_ARRIVAL_CLIPS={49,51,52,53,54,57,58,59,71,72,73,74,75,76,77,78,103,104,105,106,107,108,109,111,114,115,118,126,127,128,129,130,131,132,133,134,137}

def hot_images(metadata):
 return {frame[2] for clip in metadata['clips']
         if 20000<=int(Path(clip['name']).stem.split('-')[1])<=20037
         or clip['name'] in ('scenes/02-64.anm','scenes/02-66.anm','scenes/02-69.anm')
         or clip['name'].startswith('scenes/04-') and int(Path(clip['name']).stem.split('-')[1]) in ANIMAL_ARRIVAL_CLIPS
         for frame in clip['frames']}

def graphics(blob,metadata,rows):
 hot=hot_images(metadata);banks,maps=group_images(blob,[row for i,row in enumerate(rows) if i not in hot]);entries={};full=[];at=0
 # Linear banks contain up to four complete cels, at most 32 KiB; a cel is copied in one operation.
 linear=[];refs={};chunk=bytearray();count=0
 for i in sorted(hot):
  w,h,offset,length=rows[i];raw=blob[offset+4:offset+length];assert len(raw)==w*h
  if chunk and (len(chunk)+len(raw)>32768 or count==4):linear.append(bytes(chunk));chunk=bytearray();count=0
  refs[i]=(len(banks)+len(linear),len(chunk));chunk.extend(raw);count+=1
 if chunk:linear.append(bytes(chunk))
 for i,row in enumerate(rows):
  if i in hot:
   bank,offset=refs[i];full.append(struct.pack('>iI',-bank-1,offset))
  else:full.append(maps[at]);at+=1
 for i,bank in enumerate(banks+linear):entries['tiles/b'+str(i)]=struct.pack('>i',-len(bank))+bank
 for group in range((len(rows)+31)//32):
  data=full[group*32:group*32+32];entries['tiles/m'+str(group)]=bytes([len(data)])+b''.join(struct.pack('>H',len(x)) for x in data)+b''.join(data)
 stats=dict(direct_images=len(hot),direct_banks=len(linear),direct_bank_target_bytes=32768,direct_bank_frame_limit=4,direct_raw_bytes=sum(map(len,linear)),largest_direct_frame_bytes=max(rows[i][0]*rows[i][1] for i in hot),largest_direct_bank_bytes=max(map(len,linear)),tile_banks=len(banks),tile_bank_raw_bytes=sum(map(len,banks)),cache_budgets_unchanged=True,animal_arrival_clips=sorted(ANIMAL_ARRIVAL_CLIPS))
 return entries,stats
