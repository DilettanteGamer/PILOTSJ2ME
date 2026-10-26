"""Decode every native MP3 with FFmpeg; compare duration and signal against source WAV."""
import io,json,wave,hashlib,zipfile,subprocess,concurrent.futures
from pathlib import Path
import numpy as np
ROOT=Path(__file__).resolve().parents[1]
with zipfile.ZipFile(ROOT/'dist/Pilots-128x160.jar') as original,zipfile.ZipFile(ROOT/'experimental/input/native-mp3.zip') as corpus:
 rows={hashlib.sha256(original.read(n)).hexdigest():(n,original.read(n)) for n in original.namelist() if n.endswith('.wav')}
 inputs=[(sha,name,wav,corpus.read(sha+'.mp3')) for sha,(name,wav) in rows.items()]
def check(row):
 sha,name,raw,encoded=row
 with wave.open(io.BytesIO(raw)) as wav:
  assert wav.getnchannels()==1 and wav.getsampwidth()==1 and wav.getframerate()==11025
  samples=wav.getnframes();signal=(np.frombuffer(wav.readframes(samples),dtype=np.uint8).astype(np.float64)-128)*256
 decoded=subprocess.run(['ffmpeg','-v','error','-i','pipe:0','-f','s16le','-acodec','pcm_s16le','pipe:1'],input=encoded,check=True,capture_output=True).stdout
 pcm=np.frombuffer(decoded,dtype='<i2').astype(np.float64)
 # Pipe input cannot always apply the final LAME padding trim; it must retain all source samples.
 assert len(pcm)>=samples and len(pcm)-samples<=1152,(name,samples,len(pcm))
 noise=np.sum((signal-pcm[:samples])**2);power=np.sum(signal**2);snr=10*np.log10(power/noise) if power and noise else None
 return dict(name=name,source_frames=samples,decoded_frames=len(pcm),snr_db=None if snr is None else round(float(snr),2),mp3_bytes=len(encoded))
with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:results=list(pool.map(check,inputs))
report=dict(codec='MP3 mono 16 kbps 11025 Hz, original WAV input',clips=len(results),device_mmapi_tested=False,all_decoded=True,max_tail_padding_frames=max(r['decoded_frames']-r['source_frames'] for r in results),median_snr_db=float(np.median([r['snr_db'] for r in results if r['snr_db'] is not None])),clips_detail=results)
(ROOT/'experimental/dist/native-audio-verification.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print('Native MP3 FFmpeg: all',len(results),'decoded; max tail padding',report['max_tail_padding_frames'],'frames; median SNR',report['median_snr_db'])
