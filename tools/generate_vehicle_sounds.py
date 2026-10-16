"""Generate original synthetic vehicle sounds and reviewable base64 source assets.
Requires Python 3 and ffmpeg (libvorbis). Gradle only decodes the checked-in assets.
"""
from pathlib import Path
import base64, math, random, struct, subprocess, tempfile, wave
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'src/main/sound-assets'; OUT.mkdir(parents=True,exist_ok=True)
RATE=22050
for name,seconds in [('engine_city',1),('engine_family',1),('engine_transporter',1),('road',1),('engine_start',.7),('horn',.55),('indicator',.09),('lock',.18)]:
    rng=random.Random(714); samples=[]
    for i in range(round(RATE*seconds)):
        t=i/RATE
        if name.startswith('engine_') and name!='engine_start':
            f={'engine_city':48,'engine_family':42,'engine_transporter':34}[name]
            v=.23*(math.sin(2*math.pi*f*t)+.5*math.sin(2*math.pi*f*2*t)+.3*math.sin(2*math.pi*f*4*t))*(.8+.2*math.sin(2*math.pi*8*t))
        elif name=='engine_start':
            v=(.22*math.sin(2*math.pi*(28*t+24*t*t))+.08*(rng.random()*2-1))*(.5+.5*math.sin(2*math.pi*13*t))
        elif name=='road':v=(rng.random()*2-1)*.16
        elif name=='horn':v=.25*math.sin(2*math.pi*330*t)+.15*math.sin(2*math.pi*440*t)
        elif name=='indicator':v=.4*math.exp(-t*85)*math.sin(2*math.pi*950*t)
        else:v=.3*math.sin(2*math.pi*850*t)*math.exp(-t*32)+.13*(rng.random()*2-1)*math.exp(-t*22)
        if not name.startswith('engine_') or name=='engine_start':v*=min(1,t/.008,(seconds-t)/.025)
        samples.append(struct.pack('<h',round(max(-1,min(1,v))*32767)))
    with tempfile.TemporaryDirectory() as d:
        wav=Path(d)/'sound.wav'; ogg=Path(d)/'sound.ogg'
        with wave.open(str(wav),'wb') as w:w.setnchannels(1);w.setsampwidth(2);w.setframerate(RATE);w.writeframes(b''.join(samples))
        subprocess.run(['ffmpeg','-v','error','-y','-i',str(wav),'-c:a','libvorbis','-q:a','3',str(ogg)],check=True)
        (OUT/(name+'.ogg.b64')).write_text(base64.b64encode(ogg.read_bytes()).decode()+'\n')
        print(name,len(ogg.read_bytes()))
