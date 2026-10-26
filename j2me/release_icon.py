#!/usr/bin/env python3
"""Add the original ICO pixels to released MIDlets without rebuilding gameplay."""
from pathlib import Path
import io,json,hashlib,zipfile
from PIL import Image
ROOT=Path(__file__).resolve().parent

def icon():
 original=Image.open(ROOT.parent/'AUTORUN.ICO').convert('RGBA');out=io.BytesIO();original.save(out,format='PNG');result=out.getvalue();assert Image.open(io.BytesIO(result)).convert('RGBA').tobytes()==original.tobytes();return result

def main():
 report={}
 for profile in ['128x128','128x160','240x320']:
  path=ROOT/'dist'/('Pilots-'+profile+'.jar')
  with zipfile.ZipFile(path) as jar:entries={n:jar.read(n) for n in jar.namelist()}
  before=dict(entries);manifest=entries['META-INF/MANIFEST.MF'].decode()
  lines=[]
  for line in manifest.splitlines():
   if line.startswith('MIDlet-Version:'):line='MIDlet-Version: 0.26.1'
   if line.startswith('MIDlet-1:'):line=line.replace(',,pilots.PilotsMidlet',',/icon.png,pilots.PilotsMidlet')
   lines.append(line)
  entries['META-INF/MANIFEST.MF']=('\r\n'.join(lines).rstrip()+'\r\n\r\n').encode();entries['icon.png']=icon()
  with zipfile.ZipFile(path,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as jar:
   for name,data in entries.items():
    info=zipfile.ZipInfo(name,(2000,1,1,0,0,0));jar.writestr(info,data,compress_type=zipfile.ZIP_DEFLATED,compresslevel=9)
  jad=path.with_suffix('.jad');text=jad.read_text().replace('MIDlet-Version: 0.26.0','MIDlet-Version: 0.26.1').replace(',,pilots.PilotsMidlet',',/icon.png,pilots.PilotsMidlet');text='\n'.join('MIDlet-Jar-Size: '+str(path.stat().st_size) if row.startswith('MIDlet-Jar-Size:') else row for row in text.splitlines())+'\n';jad.write_text(text)
  with zipfile.ZipFile(path) as jar:
   assert jar.testzip() is None
   assert all(jar.read(n)==b for n,b in before.items() if n!='META-INF/MANIFEST.MF')
  report[profile]=dict(size=path.stat().st_size,sha256=hashlib.sha256(path.read_bytes()).hexdigest(),gameplay_resources_and_classes_unchanged=True)
 (ROOT/'analysis/release-icon-audit.json').write_text(json.dumps(report,indent=2)+'\n')
if __name__=='__main__':main()
