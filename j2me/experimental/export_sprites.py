"""Export editable indexed PNGs and a browser animation index; never modify the JAR."""
from pathlib import Path
import argparse,hashlib,html,io,json,struct,sys,zipfile
import numpy as np
from PIL import Image
sys.dont_write_bytecode=True
EXP=Path(__file__).resolve().parent;ROOT=EXP.parent;sys.path.insert(0,str(ROOT))
from graphics_format_audit import collect_frames
from presentation_cuts import graphics_source as cuts
from halfsize import dimensions

def main():
    p=argparse.ArgumentParser();p.add_argument('--scene',help='Two-digit scene ID, e.g. 07; default: all scenes')
    p.add_argument('--include-originals',action='store_true');p.add_argument('--output',type=Path,default=EXP/'editable/Pilots-Sprites-128x160.zip');args=p.parse_args()
    (blob,metadata,rows),_,_=cuts(*collect_frames(ROOT/'dist/Pilots-128x160.jar'))
    clips=[c for c in metadata['clips'] if args.scene is None or c['name'].startswith('scenes/'+args.scene+'-')]
    if not clips:raise SystemExit('No matching scene')
    ids=sorted({i for c in clips for x,y,i in c['frames']});palettes={};uses={i:set() for i in ids}
    for c in metadata['clips']:
        for x,y,i in c['frames']:
            if i in uses:uses[i].add(c['name'].split('/')[1].split('-')[0])
    for c in clips:
        for x,y,i in c['frames']:palettes.setdefault(i,c['palette'])
    manifest=dict(profile='128x160 reduced-art 0.27.22–0.27.24',transparency_index=0,images=[],clips=clips)
    doc='''Спрайты Pilots 128×160\n\nОткрой index.html в браузере на компьютере: там список анимаций по локациям.\nВ images/ — PNG, соответствующие уменьшенным пиксельным буферам игры.\nВ originals/ (если включены) — исходные кадры для сравнения.\nPNG хранят исходную индексную палитру и прозрачный индекс 0.\n\nДля правки используй images/, сохраняя имя, размер и палитру файла.\nРаботай карандашом без сглаживания и полупрозрачности. Не увеличивай холст.\nОдно изображение может использоваться в нескольких кадрах/локациях:\nссылки и смещения есть в manifest.json; shared_with_scenes показывает локации.\nЕсли изображение используется с другими палитрами, цвета в других сценах\nмогут отличаться, но пиксельные индексы общие. Кадры не являются атласами.\n\nОтправь изменённые PNG с прежними именами — по ID можно точно вернуть их\nв сборку. Размеры, маршруты, тайминги и исходные маски взаимодействия\nпри визуальной правке сохраняются. Этот архив не устанавливается на телефон.\n'''
    page=['<!doctype html><meta charset="utf-8"><title>Pilots: спрайты</title><style>body{background:#222;color:#eee;font:16px sans-serif}figure{display:inline-block;margin:8px;vertical-align:top}img{image-rendering:pixelated;max-width:320px;max-height:240px}figcaption{font-size:12px}a{color:#f4d56d}summary{cursor:pointer;padding:8px}</style><h1>Спрайты 128×160</h1><p>Редактируй PNG в images/, сохраняя имена, размер и палитру. Оригиналы — для сравнения.</p>']
    args.output.parent.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(args.output,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for ident in ids:
            w,h,offset,length=rows[ident];sw,sh=dimensions(w,h);pal=metadata['palettes'][palettes[ident]]
            src=np.frombuffer(blob,dtype=np.uint8,count=w*h,offset=offset+4).reshape(h,w)
            reduced=src[np.arange(sh)*h//sh][:,np.arange(sw)*w//sw].tobytes()
            name=f'images/{ident:05}.png'
            def png(data,width,height):
                im=Image.frombytes('P',(width,height),data);im.putpalette(pal);out=io.BytesIO();im.save(out,format='PNG',transparency=0,compress_level=9)
                raw=out.getvalue();check=Image.open(io.BytesIO(raw));assert check.mode=='P' and check.tobytes()==data and check.info['transparency']==0
                return raw
            z.writestr(name,png(reduced,sw,sh))
            if args.include_originals:z.writestr(f'originals/{ident:05}.png',png(src.tobytes(),w,h))
            manifest['images'].append(dict(id=ident,file=name,width=sw,height=sh,original_width=w,original_height=h,palette=palettes[ident],shared_with_scenes=sorted(uses[ident]),pixel_sha256=hashlib.sha256(reduced).hexdigest()))
        for c in clips:
            page.append('<details><summary>'+html.escape(c['name'])+f' — {c["count"]} кадров, {c["duration"]} мс</summary>')
            for frame,(x,y,ident) in enumerate(c['frames']):
                page.append(f'<figure><a href="images/{ident:05}.png"><img loading="lazy" src="images/{ident:05}.png"></a><figcaption>кадр {frame}, ID {ident}<br>смещение {x}, {y}</figcaption></figure>')
            page.append('</details>')
        manifest['palettes']=[list(p) for p in metadata['palettes']]
        z.writestr('manifest.json',json.dumps(manifest,ensure_ascii=False,separators=(',',':')))
        z.writestr('index.html','\n'.join(page));z.writestr('README.txt',doc)
    print(json.dumps(dict(path=str(args.output),images=len(ids),clips=len(clips),originals=args.include_originals,bytes=args.output.stat().st_size,sha256=hashlib.sha256(args.output.read_bytes()).hexdigest()),ensure_ascii=False),flush=True)

if __name__=='__main__':main()
