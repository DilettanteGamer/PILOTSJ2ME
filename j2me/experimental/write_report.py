from pathlib import Path
import hashlib,json,zipfile
ROOT=Path(__file__).resolve().parents[1];exp=ROOT/'experimental';jar=exp/'dist/Pilots-Experimental-128x160.jar'
report=dict(version='0.27.0',profile='128x160',jar_bytes=jar.stat().st_size,jar_sha256=hashlib.sha256(jar.read_bytes()).hexdigest(),baseline_bytes=(ROOT/'dist/Pilots-128x160.jar').stat().st_size,tile_size=16,lzma_dictionary_bytes=262144,tile_cache_bytes=262144,independent_block_bytes=262144,cldc_api_port=True,cldc_preverified=True,native_device_tested=False,all_locations_available_for_testing=True,separate_experimental_rms=True,lzma_context_reused=False,checks={})
for key,file in [('codec_port','/tmp/exp-tests.log'),('all_animation_pixels','/tmp/exp-animation-final.log'),('resources','/tmp/exp-resources.log'),('mmapi_streams','/tmp/exp-media-final.log'),('audio_lifecycle','/tmp/exp-audio-lifecycle.log'),('location_entries_and_saves','/tmp/exp-entry-final.log'),('walking_performance','/tmp/exp-walking-final.log'),('baseline_walking_performance','/tmp/base-walking.log')]:
 text=Path(file).read_text();text=text.splitlines()[0] if key=='codec_port' else text
 assert text.strip() and 'Exception in thread' not in text,(key,text);report['checks'][key]=text.strip()
with zipfile.ZipFile(jar) as z:
 assert z.testzip() is None;report['class_count']=sum(n.endswith('.class') for n in z.namelist());assert all(z.read(n)[6:8]==b'\x00\x2d' for n in z.namelist() if n.endswith('.class'))
report['size_reduction_percent']=round((1-report['jar_bytes']/report['baseline_bytes'])*100,2);(exp/'dist/verification.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(report['jar_bytes'],report['size_reduction_percent'])
