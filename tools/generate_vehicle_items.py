"""Reproducible inventory silhouettes for the three Dev6 voxel vehicles."""
import json
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/resources/assets/ng_fahrzeuge/models/item'
for name,length,van,family in [('ng_city',20.4,False,False),('ng_family',28.4,False,True),('ng_transporter',32.8,True,False)]:
    w=15.4 if van else 13 if family else 11.8
    scale=12/(length*2)
    elements=[]
    def box(x,y,z,dx,dy,dz,material='body',rotation=None):
        lo=[8+x*scale,(24-y-dy)*scale+1,8+z*scale]
        hi=[8+(x+dx)*scale,(24-y)*scale+1,8+(z+dz)*scale]
        faces={side:{'texture':'#'+material,'uv':[0,0,16,16]} for side in ['north','south','east','west','up','down']}
        if material=='body':
            for face in faces.values():face['tintindex']=0
        el={'from':[round(v,4) for v in lo],'to':[round(v,4) for v in hi],'faces':faces}
        if rotation:el['rotation']=rotation
        elements.append(el)
    box(-w,13,-length,2*w,6,2*length)
    cab=w-1
    if van:
        box(-w,-14,-10,2*w,27,length+10)
        box(-cab,-15,-15,2*cab,1.2,length+15)
        box(-cab,-6,-24,2*cab,14,14,'glass')
        for side in [-1,1]:
            x=-w-.12 if side<0 else w-.1
            box(x,11.1,-length,.25,1.5,2*length,'stripe')
            box(-cab if side<0 else cab-1,-6,-25,1,14,1)
            box(-cab if side<0 else cab-1,-6,-12,1,14,1)
            box(x,9.5,-15,.3,1,3,'wheel')
        axle=[-23,23]
    else:
        y=1.5 if family else 2.6;front=-16 if family else -7;back=24 if family else 17
        box(-cab,y+1,front+2,2*cab,10-y,back-front-3,'glass')
        box(-cab,y,front+3,2*cab,1,back-front-6)
        for side in [-1,1]:
            x=-cab if side<0 else cab-1
            box(x,y+1,front+2,1,10-y,1)
            box(x,y+1,back-2,1,10-y,1)
            box(x,y+1,4 if family else 9,.7,10-y,1,'wheel')
            box(-w-.12 if side<0 else w-.1,12,0,.25,1,2.5,'wheel')
            if family:
                box(x,y-2,front+4,.8,.8,back-front-7,'wheel')
                box(x,y+1,17,1,10-y,1)
                box(-w-.12 if side<0 else w-.1,12,14,.25,1,2.5,'wheel')
        axle=[-19,18.5] if family else [-12.6,12.2]
    for side in [-1,1]:
        for z in axle:
            x=side*(w-.4)
            box(x-1.8,13.8,z-2.8,3.6,10.4,5.6,'wheel')
            box(x-1.8,15,z-4.1,3.6,8,8.2,'wheel')
            box(x+(-1.9 if side<0 else 1.8),15.4,z-2.8,.12,6.8,5.6,'rim')
        box(-w-2.2 if side<0 else w,6 if van else 8,-24 if van else -5,2.2,3,2.4,'wheel')
    box(-w-.3,18.6,-length-1,2*w+.6,3,1.8,'wheel')
    box(-w-.3,18.6,length-.8,2*w+.6,3,1.8,'wheel')
    box(-7.5,14,-length-.3,15,4,.3,'wheel')
    for side in [-1,1]:
        x=-w+.5 if side<0 else 7.8
        box(x,14,-length-.6,w-8.3,3.4,.25,'light')
        box(-w+.2 if side<0 else w-1,14,-length-.65,.8,3.4,.3,'amber')
    model={'textures':{'body':'minecraft:block/white_concrete','glass':'minecraft:block/black_stained_glass','wheel':'minecraft:block/black_concrete','rim':'minecraft:block/iron_block','stripe':'minecraft:block/cyan_concrete','light':'minecraft:block/white_concrete','amber':'minecraft:block/orange_concrete','particle':'minecraft:block/white_concrete'},'elements':elements,'display':{'gui':{'rotation':[20,225,0],'translation':[0,0,0],'scale':[.95,.95,.95]},'ground':{'rotation':[0,0,0],'translation':[0,2,0],'scale':[.5,.5,.5]},'fixed':{'rotation':[0,180,0],'translation':[0,0,0],'scale':[1,1,1]},'thirdperson_righthand':{'rotation':[75,45,0],'translation':[0,2.5,0],'scale':[.4,.4,.4]},'firstperson_righthand':{'rotation':[0,45,0],'translation':[0,2,0],'scale':[.6,.6,.6]}}}
    # One element per line keeps model geometry reviewable without enormous diffs.
    text=json.dumps(model,ensure_ascii=False,separators=(',',':'))+'\n'
    (root/(name+'.json')).write_text(text)
    print(name,len(elements),'elements')
