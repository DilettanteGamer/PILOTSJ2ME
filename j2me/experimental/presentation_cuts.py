"""Authorized presentation cuts retain original frame clocks and world coordinates."""
import copy,struct
TAIL='scenes/02-73.anm';REST='scenes/02-74.anm';TAIL_START=51

def graphics_source(blob,metadata,rows):
 metadata=copy.deepcopy(metadata);byname={c['name']:c for c in metadata['clips']};clip=byname[TAIL];rest=byname[REST]
 assert clip['count']==60 and rest['count']==1 and clip['palette']==rest['palette'] and clip['transparent']==rest['transparent']
 old={f[2] for f in clip['frames'][TAIL_START:]};clip['frames'][TAIL_START:]=[rest['frames'][0]]*(clip['count']-TAIL_START)
 used={f[2] for c in metadata['clips'] for f in c['frames']};orphan=old-used
 # Preserve global IDs; discarded pixels no longer referenced by any shipped animation.
 # Zeroing those records lets bank generation remove their otherwise dead pixel payload.
 result=bytearray(blob)
 for image in orphan:
  w,h,off,length=rows[image];result[off+4:off+length]=bytes(w*h)
 c=clip;anm=b'ANM6'+struct.pack('>HHBB',c['count'],c['duration'],c['transparent'],c['palette'])
 for x,y,image in c['frames']:
  w,h,_,_=rows[image];anm+=struct.pack('>hhHHI',x,y,w,h,image)
 return (bytes(result),metadata,rows),{TAIL:anm},dict(hippo_tail_start_frame=TAIL_START,replaced_tail_frames=clip['count']-TAIL_START,original_frame_count_preserved=True,unreferenced_images_cleared=len(orphan),unreferenced_indexed_bytes=sum(rows[i][0]*rows[i][1] for i in orphan))

def reference(name,raw,rest_raw):
 if name!=TAIL:return raw
 assert raw[:4]==b'ANM1' and rest_raw[:4]==b'ANM1';pos=777;frames=[]
 for _ in range(60):
  w,h=struct.unpack_from('>HH',raw,pos+4);end=pos+8+w*h;frames.append(raw[pos:end]);pos=end
 return raw[:777]+b''.join(frames[:TAIL_START])+rest_raw[777:]*(60-TAIL_START)

def patch(name,text):
 if name=='Viewport.java':
  text=text.replace('public boolean zoom;','public boolean zoom;\n    public int fixedFocusX=-1;')
  text=text.replace('''    public void update() {
        this.cursorX''','''    public void update() {
        if(fixedFocusX>=0)zoom=false;
        this.cursorX''')
  text=text.replace('this.cursorX * this.drawWidth / this.worldWidth - this.width / 2','(fixedFocusX>=0?fixedFocusX:this.cursorX) * this.drawWidth / this.worldWidth - this.width / 2')
 if name=='PilotsMidlet.java':
  old='this.view.zoom = this.state.zoom;';assert text.count(old)==1
  text=text.replace(old,old+'\n            this.view.fixedFocusX=n==8?260:-1;')
 return text
