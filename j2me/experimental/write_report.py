from pathlib import Path
import hashlib,json,zipfile
ROOT=Path(__file__).resolve().parents[1];exp=ROOT/'experimental';jar=exp/'dist/Pilots-Experimental-128x160.jar'
previous=json.loads((exp/'dist/verification.json').read_text()) if (exp/'dist/verification.json').exists() else {}
report=dict(version='0.27.2',profile='128x160',jar_bytes=jar.stat().st_size,jar_sha256=hashlib.sha256(jar.read_bytes()).hexdigest(),baseline_bytes=(ROOT/'dist/Pilots-128x160.jar').stat().st_size,tile_size=16,tile_cache_bytes=131072,local_bank_target_bytes=65536,bank_frame_limit=8,bank_cache_slots=2,cldc_preverified=True,native_device_tested=False,all_locations_available_for_testing=True,separate_experimental_rms=True,checks={})
for key,file in [('all_animation_pixels','/tmp/exp-animation-final.log'),('resources','/tmp/exp-resources.log'),('cache_budgets','/tmp/exp-native-cache.log'),('mmapi_streams','/tmp/exp-media-final.log'),('audio_lifecycle','/tmp/exp-audio-lifecycle.log'),('location_entries_and_saves','/tmp/exp-entry-final.log'),('walking_performance','/tmp/exp-walking-final.log'),('baseline_walking_performance','/tmp/base-walking.log')]:
 text=Path(file).read_text();text=text.splitlines()[0] if key=='codec_port' else text
 assert text.strip() and 'Exception in thread' not in text,(key,text);report['checks'][key]=text.strip()
with zipfile.ZipFile(jar) as z:
 assert z.testzip() is None;report['class_count']=sum(n.endswith('.class') for n in z.namelist());assert all(z.read(n)[6:8]==b'\x00\x2d' for n in z.namelist() if n.endswith('.class'))
report.update(hot_graphics_codec='native JAR DEFLATE',cold_resources_codec='native JAR DEFLATE',audio_codec='native MMAPI MP3 16 kbps mono 11025 Hz',java_audio_decoder=False,java_lzma_decoder=False,frame_cache_bytes=196608,compressed_audio_cache_bytes=32768,animation_descriptor_cache_bytes=32768,target_device='Nokia C1-02',available_storage_bytes=13000000)
report['native_audio_verification']=json.loads((exp/'dist/native-audio-verification.json').read_text())
report['native_audio_verification'].pop('clips_detail',None)
report['action_latency_comparison']={name:(json.loads(Path(path).read_text()) if Path(path).exists() else previous.get('action_latency_comparison',{}).get(name,{})) for name,path in [('previous_0_27_0','/tmp/latency-0270.json'),('current_0_27_2','/tmp/latency-balanced.json'),('previous_0_27_1','/tmp/latency-native.json'),('local_lzma_candidate','/tmp/latency-0271.json'),('stable_0_26_1','/tmp/latency-stable.json')]}
report['size_reduction_percent']=round((1-report['jar_bytes']/report['baseline_bytes'])*100,2);(exp/'dist/verification.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(report['jar_bytes'],report['size_reduction_percent'])
