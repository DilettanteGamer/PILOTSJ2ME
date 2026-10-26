"""Small independent tile banks for nearby unique frames, optimized for actions."""
import struct
import numpy as np

def group_images(blob,rows,target=65536,max_images=8):
 banks=[];image_maps=[];tiles={};bank=bytearray();images=0
 def flush():
  nonlocal tiles,bank,images
  if images:banks.append(bytes(bank))
  tiles={};bank=bytearray();images=0
 for w,h,offset,length in rows:
  raw=np.frombuffer(blob,dtype=np.uint8,count=w*h,offset=offset+4).reshape(h,w)
  cols,lines=(w+15)//16,(h+15)//16
  padded=np.pad(raw,((0,lines*16-h),(0,cols*16-w)),constant_values=0)
  pieces=[t.tobytes() for t in padded.reshape(lines,16,cols,16).transpose(0,2,1,3).reshape(-1,256)]
  additional=len(set(pieces)-tiles.keys())*256
  if images and (len(bank)+additional>target or images>=max_images):flush()
  refs=[]
  for tile in pieces:
   ident=tiles.get(tile)
   if ident is None:ident=len(tiles);tiles[tile]=ident;bank.extend(tile)
   refs.append(ident)
  assert len(bank)<=524288
  image_maps.append(struct.pack('>I',len(banks))+b''.join(i.to_bytes(3,'big') for i in refs));images+=1
  rebuilt=np.array([np.frombuffer(bytes(bank[i*256:i*256+256]),dtype=np.uint8) for i in refs],dtype=np.uint8).reshape(lines,cols,16,16).transpose(0,2,1,3).reshape(lines*16,cols*16)[:h,:w]
  assert rebuilt.tobytes()==raw.tobytes()
 flush()
 return banks,image_maps
