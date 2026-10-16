package de.nevergate.fahrzeuge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import de.nevergate.fahrzeuge.VehicleSpec;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import java.util.Map;
import java.util.WeakHashMap;

/** Original voxel meshes reconstructed from the three approved project reference images. */
public final class NgCityModel {
    private final ModelPart root;
    private final VehicleSpec spec;
    private final Map<NgCityEntity, float[]> animations = new WeakHashMap<>();
    private float animation(NgCityEntity car,boolean hatch,float partial){
        float[] a=animations.computeIfAbsent(car,k->new float[]{0,0,0,0,-1,0,0});
        if(a[4]!=car.tickCount){a[0]=a[1];a[2]=a[3];a[1]=net.minecraft.util.Mth.approach(a[1],(car.systems.doors()&1)!=0?1:0,.14F);a[3]=net.minecraft.util.Mth.approach(a[3],(car.systems.doors()&2)!=0?1:0,.14F);a[5]=a[6];a[6]=net.minecraft.util.Mth.approach(a[6],car.systems.comfort.windows()?1:0,.12F);a[4]=car.tickCount;}
        return net.minecraft.util.Mth.lerp(partial,a[hatch?2:0],a[hatch?3:1])*1.22F;
    }
    private static void hinge(PoseStack pose,float x,float y,float z,float angle,boolean hatch){
        pose.translate(x/16F,y/16F,z/16F);pose.mulPose(hatch?com.mojang.math.Axis.XP.rotation(angle):com.mojang.math.Axis.YP.rotation(angle));pose.translate(-x/16F,-y/16F,-z/16F);
    }
    private final Map<NgCityEntity, WheelState> wheels = new WeakHashMap<>();
    private static final class WheelState { double x,z; float spin; boolean initialized; }
    public NgCityModel(ModelPart root, VehicleSpec spec) { this.root=root; this.spec=spec; }
    public static ModelLayerLocation layer(VehicleSpec spec) {
        return new ModelLayerLocation(new ResourceLocation(NgFahrzeuge.MOD_ID,spec.id),"main");
    }
    private static final class Parts {
        final MeshDefinition mesh=new MeshDefinition();
        final CubeListBuilder body=CubeListBuilder.create(), dark=CubeListBuilder.create(), frame=CubeListBuilder.create();
        final CubeListBuilder chrome=CubeListBuilder.create(), interior=CubeListBuilder.create(), glass=CubeListBuilder.create();
        final CubeListBuilder stripe=CubeListBuilder.create(), seams=CubeListBuilder.create(), indicatorLeft=CubeListBuilder.create(), indicatorRight=CubeListBuilder.create();
        final CubeListBuilder thirdBrake=CubeListBuilder.create();
        final CubeListBuilder front=CubeListBuilder.create(), rear=CubeListBuilder.create(), reverse=CubeListBuilder.create();
        void box(CubeListBuilder b,float x,float y,float z,float w,float h,float d){b.texOffs(0,0).addBox(x,y,z,w,h,d);}
        void child(String name,CubeListBuilder b,PartPose pose){mesh.getRoot().addOrReplaceChild(name,b,pose);}
        void beam(String name,float x,float y,float z,float w,float height,float depth,float angle){
            child(name,CubeListBuilder.create().addBox(-w/2,-height,-depth/2,w,height,depth),PartPose.offsetAndRotation(x,y,z,angle,0,0));
        }
        void windowFrame(String name,float y,float z,float width,float height,float angle){
            CubeListBuilder b=CubeListBuilder.create()
                .addBox(-width/2,-height,-.25F,width,.75F,.5F)
                .addBox(-width/2,-.7F,-.25F,width,.7F,.5F)
                .addBox(-width/2,-height,.0F,.7F,height,.45F)
                .addBox(width/2-.7F,-height,.0F,.7F,height,.45F);
            child(name,b,PartPose.offsetAndRotation(0,y,z,angle,0,0));
        }
        LayerDefinition finish(){
            child("body",body,PartPose.ZERO);child("dark",dark,PartPose.ZERO);child("frame",frame,PartPose.ZERO);
            child("chrome",chrome,PartPose.ZERO);child("interior",interior,PartPose.ZERO);child("glass",glass,PartPose.ZERO);
            child("stripe",stripe,PartPose.ZERO);child("seams",seams,PartPose.ZERO);child("indicator_left",indicatorLeft,PartPose.ZERO);child("indicator_right",indicatorRight,PartPose.ZERO);child("third_brake",thirdBrake,PartPose.ZERO);
            child("front_lights",front,PartPose.ZERO);child("rear_lights",rear,PartPose.ZERO);child("reverse_lights",reverse,PartPose.ZERO);
            return LayerDefinition.create(mesh,128,128);
        }
    }
    public static LayerDefinition createLayer(VehicleSpec spec) {
        if(spec==VehicleSpec.TOW_TRUCK)return truckLayer();
        Parts p=new Parts();boolean van=spec==VehicleSpec.TRANSPORTER, family=spec==VehicleSpec.FAMILY;
        float w=van?15.4F:family?13F:11.8F, length=van?32.8F:family?28.4F:20.4F;
        float frontAxle=van?-23F:family?-19F:-12.6F, rearAxle=van?23F:family?18.5F:12.2F;
        // Low chassis and open wheel housings, rather than a solid rectangular body over the tyres.
        p.box(p.dark,-w+1,19.8F,frontAxle+4.5F,2*w-2,1.2F,rearAxle-frontAxle-9);
        p.box(p.body,-w,12.8F,-length,2*w,.7F,2*length);
        float[] edges={-length,frontAxle-5,frontAxle+5,rearAxle-5,rearAxle+5,length};
        for(int j=0;j<6;j+=2)if(j!=2)for(int side:new int[]{-1,1})
            p.box(p.body,side<0?-w:w-2,13.5F,edges[j],2,6.4F,edges[j+1]-edges[j]);
        // Outer sill strips and door seams continue only between the wheel arches.
        for(int side:new int[]{-1,1}) {
            p.box(p.dark,side<0?-w-.10F:w-.35F,19.4F,frontAxle+5,0.45F,1.2F,rearAxle-frontAxle-10);

            for(float axle:new float[]{frontAxle,rearAxle}) {
                CubeListBuilder arch=van?p.dark:p.body;
                float x=side<0?-w-.3F:w-.7F;
                p.box(arch,x,12.6F,axle-3.4F,1,1,6.8F);
                p.box(arch,x,13.5F,axle-4.7F,1,1.5F,1.8F);p.box(arch,x,13.5F,axle+2.9F,1,1.5F,1.8F);
                p.box(arch,x,14.9F,axle-5.5F,1,2.7F,1.1F);p.box(arch,x,14.9F,axle+4.4F,1,2.7F,1.1F);
            }
        }
        if(van) van(p,w,length); else car(p,w,length,family);
        float split=van?-11.5F:family?2.6F:9.1F;
        for(int side:new int[]{-1,1}){
            float start=frontAxle+5,end=rearAxle-5;
            p.child("door_"+side,CubeListBuilder.create().addBox(side<0?-w:w-1.1F,van?7.8F:11.5F,start,1.1F,van?11.7F:8,Math.min(split,end)-start),PartPose.ZERO);
            float top=van?-5.7F:family?2.6F:3.7F,bottom=van?7.7F:11.2F;
            float ft=van?-20.6F:family?-8.8F:-.8F,fb=van?-28.5F:family?-16F:-6.9F;
            float gx=side*(van?14.95F:family?12.02F:10.82F);
            CubeListBuilder frame=CubeListBuilder.create().addBox(gx-.2F,top-.15F,ft,.4F,.4F,split-ft)
                .addBox(gx-.2F,bottom-.2F,fb,.4F,.4F,split-fb)
                .addBox(gx-.2F,top,split-.2F,.4F,bottom-top,.4F)
                .addBox(side<0?-w-.16F:w-.03F,van?9.4F:12.2F,split-3,.2F,.8F,2.4F);
            if(family||van)frame.addBox(side<0?-w-.12F:w-.18F,16.8F,start,.3F,1.2F,Math.min(split,end)-start);
            p.child("door_frame_"+side,frame,PartPose.ZERO);
            p.beam("door_beam_"+side,gx,bottom,fb,.4F,(float)Math.hypot(ft-fb,bottom-top),.4F,-(float)Math.atan2(ft-fb,bottom-top));
            CubeListBuilder back=CubeListBuilder.create();
            if(family)back.addBox(gx-.2F,top,split,.4F,.4F,16.8F-split).addBox(gx-.2F,bottom,split,.4F,.4F,16.8F-split)
                .addBox(gx-.2F,top,16.6F,.4F,bottom-top,.4F)
                .addBox(side<0?-w-.16F:w-.03F,12.2F,14,.2F,.8F,2.4F)
                .addBox(side<0?-w-.12F:w-.18F,16.8F,split,.3F,1.2F,Math.max(.01F,end-split));
            p.child("back_door_frame_"+side,back,PartPose.ZERO);
            p.child("door_stripe_"+side,van?CubeListBuilder.create().addBox(side<0?-w-.12F:w-.07F,11.1F,start,.18F,1.5F,split-start):CubeListBuilder.create(),PartPose.ZERO);
            p.child("back_door_"+side,CubeListBuilder.create().addBox(side<0?-w:w-1.1F,11.5F,Math.min(split,end),1.1F,8,Math.max(.01F,end-Math.min(split,end))),PartPose.ZERO);
        }
        bumpers(p,w,length,van);
        for(int side:new int[]{-1,1}) {
            float x=side*(w-.1F);
            float mirrorZ=van?-24.1F:family?-12.6F:-4.2F;
            float mirrorY=van?2:8.6F;
            p.box(p.frame,side<0?-w-1.5F:w-.1F,mirrorY+.6F,mirrorZ+1,1.6F,.7F,.65F);
            p.box(p.frame,side<0?-w-2.9F:w+.5F,mirrorY,mirrorZ,2.4F,van?4.2F:2.4F,2.6F);
            p.box(p.chrome,side<0?-w-2.86F:w+.55F,mirrorY+.35F,mirrorZ+2.55F,2.1F,van?3.5F:1.7F,.1F);
            for(float axle:new float[]{frontAxle,rearAxle}) wheel(p,side*(w-.35F),18.8F,axle,side,axle==frontAxle);
        }
        comfortParts(p,w,van);
        return p.finish();
    }
    private static void car(Parts p,float w,float l,boolean family){
        float roof=family?1.5F:2.6F, cowl=11.3F;
        float windBottom=family?-16.7F:-7.6F, windTop=family?-9.5F:-1.5F;
        float rearTop=family?22.2F:13.3F, rearBottom=family?27.6F:19.6F;
        float cab=w-1;
        p.box(p.body,-cab,roof-.55F,windTop-.7F,2*cab,1.1F,rearTop-windTop+1.1F);
        float hoodLength=(float)Math.hypot(windBottom+l,1.6F);
        p.child("hood",CubeListBuilder.create().addBox(-w,0,0,2*w,.9F,hoodLength),
            PartPose.offsetAndRotation(0,12.6F,-l,(float)Math.atan2(1.6F,windBottom+l),0,0));
        for(int i=0;i<10;i++)for(int side:new int[]{-1,1}){
            float z=-l+(windBottom+l)*i/10;
            p.box(p.body,side<0?-w:w-.8F,12.6F-1.6F*i/10,z,.8F,2.1F,(windBottom+l)/10+.03F);
        }
        // Thin full-width angled windscreen with a dark surround and mint A-pillars.
        float angle=-(float)Math.atan2(windTop-windBottom,cowl-roof-1);
        float height=(float)Math.hypot(windTop-windBottom,cowl-roof-1);
        p.windowFrame("wind_frame",cowl,windBottom,2*cab-.4F,height,angle);
        p.beam("wind_glass",0,cowl-.35F,windBottom-.30F,2*cab-2.7F,height-1.0F,.15F,angle);
        for(int side:new int[]{-1,1})p.beam("a_pillar_"+side,side*(cab-.3F),cowl,windBottom,1.2F,height,.9F,angle);
        // Stepped side glazing matches the sloped front and rear pillars in the concepts.
        int rows=(int)Math.ceil(cowl-roof-1);
        for(int row=0;row<rows;row++){
            float y=roof+1+row, t=(row+.5F)/(cowl-roof-1);
            float front=windTop+(windBottom-windTop)*t;
            float rear=rearTop+(rearBottom-rearTop)*t;
            for(int side:new int[]{-1,1}){
                float x=side<0?-cab:cab-.12F;

                p.box(p.body,side<0?-cab-.12F:cab-.75F,y,front-.15F,.9F,1.05F,1.2F);
                p.box(p.body,side<0?-cab-.12F:cab-.75F,y,rear-.9F,.9F,1.05F,1.2F);
            }
        }
        float backAngle=(float)Math.atan2(rearBottom-rearTop,cowl-roof-1);
        p.windowFrame("rear_frame",cowl,rearBottom,2*cab-1,height,backAngle);
        p.beam("rear_glass",0,cowl-.3F,rearBottom+.24F,2*cab-3,height-1,.12F,backAngle);
        for(int side:new int[]{-1,1}){
            float x=side<0?-w-.04F:w-.1F;
            float split=family?2.6F:9.1F;
            p.box(p.frame,side<0?-cab-.04F:cab-.5F,roof+.6F,split,.65F,cowl-roof,.9F);
            if(family)p.box(p.body,side<0?-cab-.09F:cab-.7F,roof+.6F,16.8F,.8F,cowl-roof,1.1F);
            p.box(p.seams,x,11.1F,split,.12F,8.2F,.16F);
            p.box(p.seams,x,12.2F,windBottom+.3F,.12F,7.2F,.16F);
            p.box(p.seams,x,19.15F,windBottom+.3F,.12F,.12F,split-windBottom);

            if(family){
                p.box(p.seams,x,11.1F,16.8F,.12F,8.1F,.16F);

                p.box(p.dark,side*(cab-1.2F)-.35F,roof-2.2F,windTop+1,.7F,.8F,rearTop-windTop-1);
                for(float z:new float[]{windTop+2,7,rearTop-2})p.box(p.dark,side*(cab-1.2F)-.4F,roof-1.5F,z,.8F,1.0F,1.5F);
            }
        }
        p.box(p.frame,-cab+1,cowl-1,windBottom+1.3F,2*cab-2,1.4F,3);
        p.box(p.interior,-cab+2,15.3F,windBottom+3,2*cab-4,3,rearBottom-windBottom-5);
        seats(p,4.9F,13.9F,family?-1.8F:5.2F,family);
        controls(p,4.9F,cowl-1,windBottom+2.2F);
        // Additional high-mounted stop lamp and an interior rear-view mirror.
        p.box(p.thirdBrake,-2.2F,roof+1.0F,rearTop+.25F,4.4F,.7F,.35F);
        p.box(p.frame,-.25F,roof+1F,windTop+1,.5F,1.5F,.4F);
        p.box(p.frame,-2,roof+2F,windTop+1,4,1.3F,.6F);
        p.box(p.chrome,-1.8F,roof+2.15F,windTop+1.62F,3.6F,1F,.08F);
        p.child("hatch_body",CubeListBuilder.create().addBox(-w+1.7F,12.9F,l+.05F,2*w-3.4F,5.4F,.45F),PartPose.ZERO);
        // Rear hatch seams, upright taillamps and small plate panel.
        p.box(p.seams,-w+1.2F,12.7F,l+.08F,2*w-2.4F,.14F,.10F);
        p.box(p.frame,-3,16,l+.12F,6,1.8F,.12F);
    }
    private static void van(Parts p,float w,float l){
        // Tall closed cargo body, stepped raised roof, forward sloped cab.
        p.box(p.body,-w,5,-10.5F,1.2F,7.8F,l+10.5F);
        p.box(p.body,w-1.2F,5,-10.5F,1.2F,7.8F,l+10.5F);
        p.child("cargo_panels",CubeListBuilder.create().addBox(-w,-13.8F,-9,1.2F,18.8F,l+8).addBox(w-1.2F,-13.8F,-9,1.2F,18.8F,l+8),PartPose.ZERO);
        p.child("cargo_glass",CubeListBuilder.create().addBox(-w+.2F,-12.8F,-8,.15F,16.8F,l+6).addBox(w-.35F,-12.8F,-8,.15F,16.8F,l+6),PartPose.ZERO);
        for(float z:new float[]{-9,6,20,l-2}){p.box(p.body,-w,-13.8F,z,1.2F,18.8F,1);p.box(p.body,w-1.2F,-13.8F,z,1.2F,18.8F,1);}
        p.box(p.body,-w,-13.8F,-10.5F,2*w,26.6F,1.2F);
        p.box(p.interior,-w+1,12.1F,-9,2*w-2,.7F,l+8);
        p.box(p.body,-w+.7F,-15.5F,-10.8F,2*w-1.4F,1.7F,l+10.7F);
        p.box(p.body,-w+.3F,-14.5F,-17.5F,2*w-.6F,1.3F,7.5F);
        for(int i=0;i<6;i++)p.box(p.body,-w+.5F,-13.7F+i*1.1F,-18-i*1.1F,2*w-1,1.4F,2.2F+i*1.1F);
        float bottom=7.8F, top=-5.9F, zBottom=-29.3F,zTop=-21.4F;
        float angle=-(float)Math.atan2(zTop-zBottom,bottom-top),h=(float)Math.hypot(zTop-zBottom,bottom-top);
        p.windowFrame("wind_frame",bottom,zBottom,2*w-1.8F,h,angle);
        p.beam("wind_glass",0,bottom-.6F,zBottom-.36F,2*w-4.2F,h-1.3F,.18F,angle);
        for(int side:new int[]{-1,1}){
            p.beam("a_pillar_"+side,side*(w-1.1F),bottom,zBottom,1.4F,h,1.1F,angle);
            for(int row=0;row<14;row++){
                float t=(row+.5F)/14, front=zTop+(zBottom-zTop)*t;

                p.box(p.body,side<0?-w+.2F:w-1.3F,top+row,front-.3F,1.1F,1.03F,1.2F);
            }
            p.box(p.body,side<0?-w:w-1.4F,-6.4F,-11.4F,1.4F,19.4F,1.4F);
            p.box(p.seams,side<0?-w-.04F:w-.08F,-12.8F,-9.9F,.12F,32.3F,.14F);
            p.box(p.seams,side<0?-w-.04F:w-.08F,8.4F,-29.3F,.12F,11,.14F);

            p.box(p.frame,side<0?-w-.16F:w-.05F,9.4F,-7.8F,.22F,.9F,2.7F);
            p.box(p.chrome,side<0?-w-.12F:w-.05F,4.2F,-6,.18F,.55F,31);
            p.box(p.frame,side<0?-w-.15F:w-.06F,4.9F,-5.8F,.22F,.45F,30.5F);
            // Cargo pressings on the closed upper side panel.
            float x=side<0?-w-.06F:w-.06F;
            p.box(p.seams,x,-9.5F,-6.7F,.12F,.12F,36);
            p.box(p.seams,x,-9.5F,29.3F,.12F,16.5F,.12F);
            p.box(p.chrome,x,7.0F,-6.7F,.12F,.15F,36);
            p.box(p.stripe,side<0?-w-.11F:w-.07F,11.1F,-10.5F,.18F,1.5F,l+9.9F);
        }
        p.child("hood",CubeListBuilder.create().addBox(-w,0,0,2*w,1.0F,5.2F),
            PartPose.offsetAndRotation(0,12.2F,-l,.37F,0,0));
        p.box(p.frame,-w+2,7.2F,-26.8F,2*w-4,1.5F,4);
        p.box(p.interior,-w+2,15.2F,-26.2F,2*w-4,3,15);
        seats(p,6.1F,12.8F,-14.5F,false);controls(p,6.1F,6.4F,-26.1F);
        for(int side:new int[]{-1,1}){
            float x=side<0?-w:0;
            p.child("hatch_"+side,CubeListBuilder.create().addBox(x,-13.4F,l-.6F,w,32.5F,.8F),PartPose.ZERO);
            p.child("hatch_dark_"+side,CubeListBuilder.create().addBox(side<0?-.18F:0,-13.4F,l+.25F,.18F,32.5F,.1F)
                .addBox(side<0?-1.7F:.7F,7.7F,l+.3F,1,.65F,.2F),PartPose.ZERO);
            p.child("hatch_stripe_"+side,CubeListBuilder.create().addBox(x,11.1F,l+.3F,w,1.5F,.1F),PartPose.ZERO);
        }
    }
    private static void seats(Parts p,float spacing,float y,float z,boolean rear){
        for(int side:new int[]{-1,1}){
            float x=side*spacing;
            p.box(p.interior,x-3.6F,y,z-2.7F,7.2F,1.8F,6.8F);
            p.box(p.interior,x-3.5F,y-5.6F,z+3,7,6.7F,1.8F);
            p.box(p.interior,x-2.4F,y-8.1F,z+3.1F,4.8F,2.5F,1.6F);
            p.box(p.frame,x-3.9F,y-4.9F,z+3.3F,.45F,4.7F,1.6F);
        }
        if(rear){
            p.box(p.interior,-8.8F,y+.1F,z+11,17.6F,1.8F,6);
            p.box(p.interior,-8.8F,y-5,z+15.5F,17.6F,5.4F,1.7F);
            for(float x:new float[]{-5,5})p.box(p.interior,x-2.3F,y-7.1F,z+15.5F,4.6F,2.2F,1.7F);
        }
    }
    private static void controls(Parts p,float x,float y,float z){
        p.box(p.frame,x-.4F,y-.8F,z-.4F,.8F,3.5F,2.1F);
        p.box(p.frame,x-2.1F,y-1.7F,z+1,4.2F,.6F,.5F);
        p.box(p.frame,x-2.1F,y+1.1F,z+1,4.2F,.6F,.5F);
        p.box(p.frame,x-2.1F,y-1.7F,z+1,.6F,3.4F,.5F);
        p.box(p.frame,x+1.5F,y-1.7F,z+1,.6F,3.4F,.5F);
        p.box(p.chrome,x-.6F,y-.5F,z+1,.9F,.9F,.6F);
    }
    private static void bumpers(Parts p,float w,float l,boolean van){
        for(float z:new float[]{-l-1,l-1}){
            p.box(p.dark,-w-.35F,18.6F,z,2*w+.7F,2.2F,2);
            p.box(p.dark,-w-.2F,22F,z+.3F,2*w+.4F,1.0F,1.4F);
            for(float x:new float[]{-w+.2F,w-2.2F})p.box(p.dark,x,20.6F,z+.2F,2,1.6F,1.6F);
        }
        p.box(p.frame,-7.5F,14.0F,-l-.23F,15,4.2F,.5F);
        for(int j=0;j<3;j++)p.box(p.dark,-7.3F,14.4F+j*1.2F,-l-.54F,14.6F,.48F,.2F);
        p.box(p.frame,-6.5F,20.65F,-l-.8F,13,1.2F,.3F);
        for(int side:new int[]{-1,1}){
            float x=side<0?-w+.4F:7.7F;
            p.box(p.frame,x-.22F,13.85F,-l-.25F,w-7.4F,4.2F,.55F);
            p.box(p.front,x,14.1F,-l-.56F,w-8.1F,3.5F,.25F);
            p.box(side>0?p.indicatorLeft:p.indicatorRight,side<0?-w+.15F:w-1.05F,14.1F,-l-.57F,.9F,3.5F,.27F);
            p.box(side>0?p.indicatorLeft:p.indicatorRight,side<0?-w-.08F:w-.08F,12.5F,-l+7,.16F,.8F,1.2F);
            p.box(p.rear,side<0?-w+.35F:w-1.5F,van?5.3F:10.9F,l+.15F,1.15F,van?10.2F:5.9F,.3F);
            p.box(side>0?p.indicatorLeft:p.indicatorRight,side<0?-w+.35F:w-1.5F,van?9.5F:12.8F,l+.48F,1.15F,1.25F,.12F);
            p.box(p.reverse,side<0?-w+.35F:w-1.5F,van?11.1F:14.2F,l+.49F,1.15F,.9F,.12F);
        }
    }
    private static void wheel(Parts p,float x,float y,float z,int side,boolean front){
        String name=(front?"front_":"rear_")+(side<0?"left":"right");
        var wheel=p.mesh.getRoot().addOrReplaceChild(name,CubeListBuilder.create(),PartPose.offset(x,y,z));
        CubeListBuilder tyre=CubeListBuilder.create().addBox(-1.75F,-5.2F,-2.8F,3.5F,10.4F,5.6F)
            .addBox(-1.75F,-4.2F,-4.2F,3.5F,8.4F,8.4F).addBox(-1.75F,-2.8F,-5.2F,3.5F,5.6F,10.4F);
        wheel.addOrReplaceChild("tyre",tyre,PartPose.ZERO);
        float out=side<0?-1.86F:1.76F;
        CubeListBuilder rim=CubeListBuilder.create().addBox(out,-3.4F,-2F,.1F,6.8F,4F)
            .addBox(out,-2.7F,-2.7F,.1F,5.4F,5.4F).addBox(out,-2F,-3.4F,.1F,4F,6.8F);
        wheel.addOrReplaceChild("rim",rim,PartPose.ZERO);
        CubeListBuilder hubs=CubeListBuilder.create().addBox(side<0?out-.1F:out+.1F,-1.2F,-1.2F,.12F,2.4F,2.4F);
        for(float a:new float[]{-1.9F,1.9F})hubs.addBox(side<0?out-.06F:out+.06F,a,-.24F,.12F,.5F,.5F)
            .addBox(side<0?out-.06F:out+.06F,-.24F,a,.12F,.5F,.5F);
        wheel.addOrReplaceChild("hub",hubs,PartPose.ZERO);
    }
    public void render(NgCityEntity vehicle,PoseStack pose,MultiBufferSource buffers,int light,int overlay,float partialTick){
        if(spec==VehicleSpec.TOW_TRUCK){renderTruck(vehicle,pose,buffers,light,overlay,partialTick);return;}
        float[] color=VehicleTextures.paint(vehicle.color());
        renderComfort(vehicle,pose,buffers,light,overlay,partialTick);
        if(spec==VehicleSpec.TRANSPORTER&&vehicle.systems.comfort.variant()==0)renderPart("cargo_panels",pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);
        if(spec==VehicleSpec.FAMILY&&vehicle.systems.comfort.variant()==1)renderPart("roof_box",pose,buffers,VehicleTextures.RUBBER,light,overlay,.16F,.18F,.19F);
        renderPart("body",pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);
        renderPart("hood",pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);
        renderPart("dark",pose,buffers,VehicleTextures.RUBBER,light,overlay,.15F,.16F,.18F);
        renderPart("frame",pose,buffers,VehicleTextures.RUBBER,light,overlay,.08F,.09F,.10F);
        renderPart("seams",pose,buffers,VehicleTextures.METAL,light,overlay,color[0]*.57F,color[1]*.57F,color[2]*.57F);
        renderPart("chrome",pose,buffers,VehicleTextures.METAL,light,overlay,.60F,.64F,.67F);
        renderPart("interior",pose,buffers,VehicleTextures.FABRIC,light,overlay,.15F,.17F,.19F);
        renderPart("stripe",pose,buffers,VehicleTextures.PAINT,light,overlay,.26F,.73F,.68F);
        for (boolean left : new boolean[]{true,false}) {
            boolean flash=vehicle.indicatorLit(left);
            renderPart(left?"indicator_left":"indicator_right",pose,buffers,VehicleTextures.LENS,flash?LightTexture.FULL_BRIGHT:light,overlay,flash?1:.65F,flash?.48F:.25F,.025F);
        }
        pose.pushPose();if(spec!=VehicleSpec.TRANSPORTER)hinge(pose,0,spec==VehicleSpec.FAMILY?1.5F:2.6F,spec==VehicleSpec.FAMILY?22.2F:13.3F,animation(vehicle,true,partialTick),true);
        renderPart("third_brake",pose,buffers,VehicleTextures.LENS,vehicle.isBraking()?LightTexture.FULL_BRIGHT:light,overlay,vehicle.isBraking()?1:.42F,.025F,.02F);
        pose.popPose();
        boolean van=spec==VehicleSpec.TRANSPORTER;
        float head=vehicle.lightsOn()?1:.82F;
        renderPart("front_lights",pose,buffers,VehicleTextures.LENS,vehicle.lightsOn()?LightTexture.FULL_BRIGHT:light,overlay,head,head*.98F,head*.91F);
        boolean rear=vehicle.lightsOn()||vehicle.isBraking();
        renderPart("rear_lights",pose,buffers,VehicleTextures.LENS,rear?LightTexture.FULL_BRIGHT:light,overlay,vehicle.isBraking()?1:rear?.75F:.49F,.025F,.02F);
        float reverse=vehicle.getDriveSpeed()<-.001F?1:.78F;
        renderPart("reverse_lights",pose,buffers,VehicleTextures.LENS,reverse==1?LightTexture.FULL_BRIGHT:light,overlay,reverse,reverse,reverse);
        float[] angles=wheelAngles(vehicle);
        for(String name:new String[]{"front_left","front_right","rear_left","rear_right"}){
            ModelPart wheel=root.getChild(name);wheel.xRot=angles[0];wheel.yRot=name.startsWith("front")?angles[1]:0;
            renderWheel(wheel,pose,buffers,light,overlay);
        }
        for(String name:new String[]{"a_pillar_-1","a_pillar_1"})renderPart(name,pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);
        renderPart("wind_frame",pose,buffers,VehicleTextures.RUBBER,light,overlay,.065F,.075F,.085F);
        float w=van?15.4F:spec==VehicleSpec.FAMILY?13:11.8F;
        float doorZ=van?-29.3F:spec==VehicleSpec.FAMILY?-16.7F:-7.6F;
        float split=van?-11.5F:spec==VehicleSpec.FAMILY?2.6F:9.1F;
        for(int side:new int[]{-1,1}){
            pose.pushPose();hinge(pose,side*w,0,doorZ,side*animation(vehicle,false,partialTick),false);
            renderPart("door_"+side,pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);
            renderPart("door_frame_"+side,pose,buffers,VehicleTextures.RUBBER,light,overlay,.08F,.09F,.1F);
            renderPart("door_beam_"+side,pose,buffers,VehicleTextures.RUBBER,light,overlay,.08F,.09F,.1F);
            renderPart("door_stripe_"+side,pose,buffers,VehicleTextures.PAINT,light,overlay,.26F,.73F,.68F);pose.popPose();
            pose.pushPose();if(spec==VehicleSpec.FAMILY)hinge(pose,side*w,0,split,side*animation(vehicle,false,partialTick),false);
            renderPart("back_door_"+side,pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);
            renderPart("back_door_frame_"+side,pose,buffers,VehicleTextures.RUBBER,light,overlay,.08F,.09F,.1F);pose.popPose();
        }
        if(!van){
            pose.pushPose();hinge(pose,0,spec==VehicleSpec.FAMILY?1.5F:2.6F,spec==VehicleSpec.FAMILY?22.2F:13.3F,animation(vehicle,true,partialTick),true);
            renderPart("rear_frame",pose,buffers,VehicleTextures.RUBBER,light,overlay,.065F,.075F,.085F);
            renderPart("hatch_body",pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);pose.popPose();
        }else for(int side:new int[]{-1,1}){
            pose.pushPose();hinge(pose,side*w,0,32.8F,side*animation(vehicle,true,partialTick),false);
            renderPart("hatch_"+side,pose,buffers,VehicleTextures.PAINT,light,overlay,color[0],color[1],color[2]);
            renderPart("hatch_dark_"+side,pose,buffers,VehicleTextures.RUBBER,light,overlay,.08F,.09F,.1F);
            renderPart("hatch_stripe_"+side,pose,buffers,VehicleTextures.PAINT,light,overlay,.26F,.73F,.68F);pose.popPose();
        }
        badge(pose,buffers,light,overlay,van?32.8F:spec==VehicleSpec.FAMILY?28.4F:20.4F,van);
    }
    private static void comfortParts(Parts p,float w,boolean van){
        p.child("cabin_light",CubeListBuilder.create().addBox(-1.5F,van?-5F:4F,van?-15:4,3,.4F,2),PartPose.ZERO);
        for(int side:new int[]{-1,1})p.child("wiper_"+side,CubeListBuilder.create().addBox(-.2F,-6,0,.4F,6,.3F),PartPose.offset(side*w*.45F,van?7.5F:11.1F,van?-29.6F:w==13?-17F:-8F));
        p.child("roof_box",CubeListBuilder.create().addBox(-8,-4,-5,16,3,20).addBox(-7,-5,-4,14,1,18),PartPose.ZERO);
    }
    private void renderComfort(NgCityEntity car,PoseStack pose,MultiBufferSource buffers,int light,int overlay,float partial){
        boolean lamp=car.systems.comfort.cabin()&&car.systems.comfort.battery()>0;
        renderPart("cabin_light",pose,buffers,VehicleTextures.LENS,lamp?LightTexture.FULL_BRIGHT:light,overlay,lamp?1:.5F,lamp?.94F:.5F,lamp?.7F:.5F);
        for(int side:new int[]{-1,1}){var part=root.getChild("wiper_"+side);part.zRot=car.systems.comfort.wipers()?(float)Math.sin((car.tickCount+partial)*.18F)*.65F:.85F;
            renderPart("wiper_"+side,pose,buffers,VehicleTextures.RUBBER,light,overlay,.07F,.08F,.09F);}
    }
    private static LayerDefinition truckLayer(){
        Parts p=new Parts();
        p.box(p.dark,-18,19,-46,36,3,92);p.box(p.body,-17,4,-44,34,15,28);
        p.box(p.body,-17,-8,-31,34,1.5F,16);p.box(p.body,-17,-6,-17,34,18,1.5F);
        for(int side:new int[]{-1,1}){p.box(p.body,side<0?-17:15,-6,-31,2,14,2);p.box(p.body,side<0?-17:15,-6,-17,2,14,2);}
        p.child("truck_glass",CubeListBuilder.create().addBox(-15,-6,-31.3F,30,12,.15F),PartPose.ZERO);
        p.child("truck_side",CubeListBuilder.create().addBox(-16.5F,0,-29,.15F,12,12).addBox(16.35F,0,-29,.15F,12,12),PartPose.offset(0,-6,0));
        p.child("flatbed",CubeListBuilder.create().addBox(-18,8,-12,36,2,57).addBox(-18,6,-12,1,2,57).addBox(17,6,-12,1,2,57),PartPose.ZERO);
        p.child("ramp",CubeListBuilder.create().addBox(-18,0,0,36,1,16),PartPose.offset(0,9,45));
        p.box(p.frame,-18,17,-47,36,3,2);p.box(p.front,-15,9,-44.4F,6,3,.4F);p.box(p.front,9,9,-44.4F,6,3,.4F);
        p.box(p.rear,-15,16,46,5,2,.4F);p.box(p.rear,10,16,46,5,2,.4F);
        for(int side:new int[]{-1,1}){wheel(p,side*17,18.8F,-32,side,true);wheel(p,side*17,18.8F,28,side,false);}
        seats(p,6,13,-23,false);controls(p,6,7,-30);comfortParts(p,17,true);return p.finish();
    }
    private void renderTruck(NgCityEntity car,PoseStack pose,MultiBufferSource b,int light,int overlay,float partial){
        animation(car,false,partial);
        float[] c=VehicleTextures.paint(car.color());renderPart("body",pose,b,VehicleTextures.PAINT,light,overlay,c[0],c[1],c[2]);
        for(String n:new String[]{"dark","frame","interior"})renderPart(n,pose,b,VehicleTextures.RUBBER,light,overlay,.12F,.14F,.15F);
        renderPart("flatbed",pose,b,VehicleTextures.METAL,light,overlay,.52F,.58F,.6F);
        root.getChild("ramp").xRot=car.systems.comfort.ramp()?-.65F:(float)Math.PI/2;renderPart("ramp",pose,b,VehicleTextures.METAL,light,overlay,.52F,.58F,.6F);
        renderPart("front_lights",pose,b,VehicleTextures.LENS,car.lightsOn()?LightTexture.FULL_BRIGHT:light,overlay,1,1,.9F);
        renderPart("rear_lights",pose,b,VehicleTextures.LENS,car.isBraking()||car.lightsOn()?LightTexture.FULL_BRIGHT:light,overlay,1,.02F,.02F);
        float[] a=wheelAngles(car);for(String n:new String[]{"front_left","front_right","rear_left","rear_right"}){var wheel=root.getChild(n);wheel.xRot=a[0];wheel.yRot=n.startsWith("front")?a[1]:0;renderWheel(wheel,pose,b,light,overlay);}
        renderComfort(car,pose,b,light,overlay,partial);
    }
    private float[] wheelAngles(NgCityEntity car){
        WheelState state=wheels.computeIfAbsent(car,k->new WheelState());
        if(state.initialized){double distance=Math.hypot(car.getX()-state.x,car.getZ()-state.z);if(distance<2)state.spin+=distance*16/5.2F*Math.signum(car.getDriveSpeed());}
        state.x=car.getX();state.z=car.getZ();state.initialized=true;
        float steer=Math.max(-.38F,Math.min(.38F,(car.getYRot()-car.yRotO)*.14F*Math.signum(car.getDriveSpeed())));
        return new float[]{state.spin,steer};
    }
    private void renderWheel(ModelPart wheel,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();wheel.translateAndRotate(pose);
        wheel.getChild("tyre").render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.RUBBER)),light,overlay,.08F,.085F,.09F,1);
        wheel.getChild("rim").render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.METAL)),light,overlay,.65F,.68F,.70F,1);
        wheel.getChild("hub").render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.METAL)),light,overlay,.40F,.43F,.46F,1);
        pose.popPose();
    }
    private void renderPart(String name,PoseStack pose,MultiBufferSource buffers,ResourceLocation texture,int light,int overlay,float r,float g,float b){
        root.getChild(name).render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(texture)),light,overlay,r,g,b,1);
    }
    public void renderGlass(NgCityEntity vehicle,PoseStack pose,MultiBufferSource buffers,int light,int overlay,float partial){
        VertexConsumer buffer=buffers.getBuffer(RenderType.entityTranslucent(VehicleTextures.PAINT));
        if(spec==VehicleSpec.TOW_TRUCK){
            root.getChild("truck_glass").render(pose,buffer,light,overlay,.2F,.3F,.35F,.32F);
            var side=root.getChild("truck_side");float[] anim=animations.get(vehicle);float amount=anim==null?0:net.minecraft.util.Mth.lerp(partial,anim[5],anim[6]);side.y=-6+11*amount;side.yScale=1-amount*.92F;side.render(pose,buffer,light,overlay,.2F,.3F,.35F,.32F);return;
        }
        if(spec==VehicleSpec.TRANSPORTER&&vehicle.systems.comfort.variant()==1)root.getChild("cargo_glass").render(pose,buffer,light,overlay,.2F,.3F,.35F,.32F);
        sideGlass(vehicle,pose,buffer,light,overlay,partial);
        root.getChild("wind_glass").render(pose,buffer,light,overlay,.20F,.29F,.34F,.30F);
        if(spec!=VehicleSpec.TRANSPORTER){
            pose.pushPose();hinge(pose,0,spec==VehicleSpec.FAMILY?1.5F:2.6F,spec==VehicleSpec.FAMILY?22.2F:13.3F,animation(vehicle,true,partial),true);
            root.getChild("rear_glass").render(pose,buffer,light,overlay,.20F,.29F,.34F,.33F);pose.popPose();
        }
    }
    private void sideGlass(NgCityEntity vehicle,PoseStack pose,VertexConsumer buffer,int light,int overlay,float partial){
        boolean van=spec==VehicleSpec.TRANSPORTER, family=spec==VehicleSpec.FAMILY;
        float x=van?14.95F:family?12.02F:10.82F;
        float top=van?-5.7F:family?2.6F:3.7F, bottom=van?7.7F:11.2F;
        float[] a=animations.get(vehicle);float opened=a==null?0:net.minecraft.util.Mth.lerp(partial,a[5],a[6]);top+=(bottom-top-1)*opened;
        float frontTop=van?-20.6F:family?-8.8F:-.8F;
        float frontBottom=van?-28.5F:family?-16F:-6.9F;
        float rearTop=van?-11.5F:family?21.6F:12.7F;
        float rearBottom=van?-11.5F:family?27F:19F;
        float split=van?-11.5F:family?2.6F:9.1F;
        float quarter=family?16.8F:rearBottom;
        for(int side:new int[]{-1,1}){
            // Separate front-door glass, optional rear-door glass, and fixed rear quarter.
            for(int zone=0;zone<(family?3:2);zone++){
                float ft=zone==0?frontTop:zone==1?split:quarter;
                float fb=zone==0?frontBottom:zone==1?split:quarter;
                float rt=zone==0?Math.min(split,rearTop):zone==1?(family?quarter:rearTop):rearTop;
                float rb=zone==0?Math.min(split,rearBottom):zone==1?(family?quarter:rearBottom):rearBottom;
                if(rt<=ft&&rb<=fb)continue;
                pose.pushPose();
                if(zone==0||family&&zone==1)hinge(pose,side*(van?15.4F:family?13:11.8F),0,zone==0?(van?-29.3F:family?-16.7F:-7.6F):split,side*animation(vehicle,false,partial),false);
                pose.scale(1/16F,1/16F,1/16F);
                float[][] points=side>0?new float[][]{{top,ft,0,0},{bottom,fb,0,1},{bottom,rb,1,1},{top,rt,1,0}}:
                    new float[][]{{top,rt,1,0},{bottom,rb,1,1},{bottom,fb,0,1},{top,ft,0,0}};
                for(float[] point:points)buffer.vertex(pose.last().pose(),side*x,point[0],point[1])
                    .color(.20F,.29F,.34F,.32F).uv(point[2],point[3]).overlayCoords(overlay).uv2(light)
                    .normal(pose.last().normal(),side,0,0).endVertex();
                pose.popPose();
            }
        }
    }

    private void badge(PoseStack pose,MultiBufferSource buffers,int light,int overlay,float l,boolean van){
        pose.pushPose();pose.scale(1/16F,1/16F,1/16F);
        VertexConsumer v=buffers.getBuffer(RenderType.entityCutoutNoCull(van?VehicleTextures.BADGE_MINT:VehicleTextures.BADGE));
        Matrix4f m=pose.last().pose();float z=-l-.78F,y=15.1F,w=van?4:3.2F,h=van?2:1.6F;
        vertex(v,pose,m,-w/2,y,z,0,0,light,overlay);vertex(v,pose,m,-w/2,y+h,z,0,1,light,overlay);
        vertex(v,pose,m,w/2,y+h,z,1,1,light,overlay);vertex(v,pose,m,w/2,y,z,1,0,light,overlay);pose.popPose();
    }
    private void vertex(VertexConsumer v,PoseStack pose,Matrix4f m,float x,float y,float z,float u,float t,int light,int overlay){
        v.vertex(m,x,y,z).color(255,255,255,255).uv(u,t).overlayCoords(overlay).uv2(light).normal(pose.last().normal(),0,0,-1).endVertex();
    }
}
