package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Independent, original meshes for the low-floor bus, road motorcycle and open-bed pickup. */
public final class SpecialVehicleModel {
    private final java.util.Map<NgCityEntity,float[]> pickupDoors=new java.util.WeakHashMap<>();
    private final VehicleSpec spec;private final ModelPart root;
    private final java.util.Map<NgCityEntity,float[]> state=new java.util.WeakHashMap<>();
    private static CubeListBuilder cube(float x,float y,float z,float w,float h,float d){return CubeListBuilder.create().texOffs(0,0).addBox(x,y,z,w,h,d);}
    private static void add(PartDefinition p,String n,CubeListBuilder b){p.addOrReplaceChild(n,b,PartPose.ZERO);}
    private static void wheel(PartDefinition p,String n,float x,float z,boolean bike){
        var w=p.addOrReplaceChild(n,CubeListBuilder.create(),PartPose.offset(x,18.5F,z));float thick=bike?3:3.5F;
        for(int i=0;i<8;i++)w.addOrReplaceChild("tyre"+i,cube(-thick/2,-5.4F,-2.2F,thick,2,4.4F),PartPose.rotation(i*(float)Math.PI/4,0,0));
        w.addOrReplaceChild("rim",cube(-thick/2-.05F,-3.2F,-3.2F,thick+.1F,6.4F,6.4F),PartPose.ZERO);
        w.addOrReplaceChild("hub",cube(-thick/2-.2F,-1,-1,thick+.4F,2,2),PartPose.ZERO);
    }
    public SpecialVehicleModel(VehicleSpec s){spec=s;var mesh=new MeshDefinition();var p=mesh.getRoot();
        if(s==VehicleSpec.BUS)bus(p);else if(s==VehicleSpec.MOTORCYCLE)bike(p);else pickup(p);
        root=LayerDefinition.create(mesh,128,128).bakeRoot();
    }
    private static void bus(PartDefinition p){
        add(p,"body",cube(-19,18,-63,38,1,126).addBox(-19,5,-63,38,13,2).addBox(-19,5,61,38,13,2).addBox(-19,-23,-63,38,2,126).addBox(-19,-21,60,38,30,3));
        add(p,"dark",cube(-18,19,-62,36,2,124).addBox(-19,16,-64,38,3,2).addBox(-19,16,63,38,3,2).addBox(-18,7,-61,36,2,121));
        // Left wall glazing; curbside wall has real door openings, no hidden solid backing.
        var wall=cube(-19,-21,-62,1.4F,30,2).addBox(-19,-21,58,1.4F,30,2).addBox(17.6F,-21,-62,1.4F,30,2);
        for(int z=-46;z<=50;z+=16)wall.addBox(-19,-21,z,1.4F,30,1.4F);
        for(int[] range:new int[][]{{-39,-2},{19,60}}){wall.addBox(17.6F,4,range[0],1.4F,5,range[1]-range[0]);for(int z=range[0];z<range[1];z+=16)wall.addBox(17.6F,-21,z,1.4F,25,1.4F);}
        add(p,"frame",wall);
        add(p,"glass",cube(-17,-19,-63.15F,34,20,.18F).addBox(-19.1F,-19,-59,.18F,22,117).addBox(18.92F,-19,-38,.18F,22,35).addBox(18.92F,-19,20,.18F,22,38).addBox(-16,-18,63.04F,32,18,.18F));
        for(int i=0;i<2;i++){float z=i==0?-60:-2;add(p,"door"+i,cube(18,5,z,1,14,21).addBox(18,-21,z,1,26,1).addBox(18,-21,z+20,1,26,1).addBox(17.8F,-21,z,1.5F,2,21).addBox(17.8F,-21,z,1.5F,40,1));add(p,"doorGlass"+i,cube(19.05F,-18,z+1,.15F,24,19));}
        var seats=cube(6,7,-51,10,3,10).addBox(6,-3,-44,10,10,2);
        for(int i=0;i<6;i++)for(int side:new int[]{-1,1}){float x=side<0?-15:5,z=-29+i*14.4F;seats.addBox(x,5,z,10,3,10).addBox(x,-5,z+8,10,10,2);}
        add(p,"seats",seats);add(p,"rails",cube(-1,-17,-43,.8F,24,.8F).addBox(-1,-17,12,.8F,24,.8F).addBox(-1,-17,-43,.8F,.8F,98));
        add(p,"dashboard",cube(-17,1,-60,34,5,8).addBox(6,0,-53,7,1,4));
        add(p,"stripe",cube(-19.15F,8,-62,.18F,3,124).addBox(19,8,-38,.18F,3,36).addBox(19,8,13,.18F,3,47));
        add(p,"head",cube(-15,10,-63.3F,6,3,.4F).addBox(9,10,-63.3F,6,3,.4F));add(p,"tail",cube(-15,10,63.1F,4,6,.4F).addBox(11,10,63.1F,4,6,.4F));
        add(p,"sign",cube(-17,-21,-63.35F,34,6,.5F).addBox(19.05F,-20,-36,.4F,5,30).addBox(-14,-21,63.12F,28,6,.5F));
        for(int side:new int[]{-1,1}){wheel(p,"front"+side,side*18,-43,false);wheel(p,"rear"+side,side*18,42,false);}
    }
    private static void pickup(PartDefinition p){
        add(p,"body",cube(-15,12,-36,30,6,8).addBox(-15,10,-28,2,9,4).addBox(13,10,-28,2,9,4).addBox(-15,-3,-25,30,1.5F,25).addBox(-15,-2,-25,2,12,2).addBox(13,-2,-25,2,12,2).addBox(-15,-2,-1,30,12,2).addBox(-15,13,1,30,1.5F,34).addBox(-15,6,1,1.5F,7,34).addBox(13.5F,6,1,1.5F,7,34));
        add(p,"dark",cube(-14,19,-34,28,2,68).addBox(-16,17,-37,32,3,2).addBox(-16,17,35,32,3,2).addBox(-13.5F,12.7F,2,27,.3F,32));
        add(p,"frame",cube(-15,-2,-24,30,.7F,1).addBox(-15,9,-24,30,.7F,1));add(p,"glass",cube(-13,-1.5F,-24.2F,26,10,.15F).addBox(-12,-1,1.04F,24,8,.15F));
        add(p,"seats",cube(-11,10,-17,8,3,10).addBox(3,10,-17,8,3,10).addBox(-11,0,-9,8,10,2).addBox(3,0,-9,8,10,2));add(p,"dashboard",cube(-13,7,-24,26,4,4));
        for(int i=0;i<2;i++){add(p,"pickupDoor"+i,cube(i==0?13:-15,10,-24,2,9,23).addBox(i==0?13:-15,-2,-24,2,1,23).addBox(i==0?13:-15,-2,-2,2,12,1));add(p,"pickupGlass"+i,cube(i==0?14.95F:-15.1F,-1,-22,.15F,10,20));}
        add(p,"tailgate",cube(-15,-7,0,30,7,1.5F));rootDummy(p,"stripe");rootDummy(p,"rails");rootDummy(p,"sign");
        add(p,"head",cube(-13,12,-36.25F,7,3,.4F).addBox(6,12,-36.25F,7,3,.4F));add(p,"tail",cube(-15.15F,8,34,2,7,.3F).addBox(13.15F,8,34,2,7,.3F));
        for(int side:new int[]{-1,1}){wheel(p,"front"+side,side*15,-25,false);wheel(p,"rear"+side,side*15,24,false);}
    }
    private static void rootDummy(PartDefinition p,String n){add(p,n,CubeListBuilder.create());}
    private static void bike(PartDefinition p){
        add(p,"body",cube(-3,3,-10,6,6,11).addBox(-3,9,-17,6,2,7).addBox(-3,9,11,6,2,6));
        add(p,"dark",cube(-2,10,-10,4,5,20).addBox(-3.5F,6,0,7,2,12));
        add(p,"frame",cube(-1,8,-13,2,2,28).addBox(-1,2,-14,2,16,1).addBox(-6,1,-12,12,1.2F,1.2F).addBox(2,14,1,1.5F,2,13));
        add(p,"seats",cube(-3,5,0,6,1.2F,12));add(p,"head",cube(-2,2,-15,4,4,1));add(p,"tail",cube(-2,7,15,4,2,1));
        add(p,"stand",cube(-.5F,0,-.5F,1,9,1));rootDummy(p,"glass");rootDummy(p,"dashboard");rootDummy(p,"stripe");rootDummy(p,"rails");rootDummy(p,"sign");
        wheel(p,"front-1",0,-13,true);wheel(p,"rear-1",0,13,true);
    }
    private void part(String name,PoseStack p,MultiBufferSource b,ResourceLocation t,int light,int overlay,float r,float g,float blue){root.getChild(name).render(p,b.getBuffer(RenderType.entityCutoutNoCull(t)),light,overlay,r,g,blue,1);}
    public void render(NgCityEntity c,PoseStack pose,MultiBufferSource b,int light,int overlay,float partial){
        var a=state.computeIfAbsent(c,k->new float[]{0,0,0,0,-1});if(a[4]!=c.tickCount){a[0]=net.minecraft.util.Mth.approach(a[0],(c.systems.doors()&1)!=0?1:0,.1F);a[1]=net.minecraft.util.Mth.approach(a[1],(c.systems.doors()&2)!=0?1:0,.1F);a[2]=net.minecraft.util.Mth.approach(a[2],c.systems.expansion.stand()?-.20F:Math.max(-.38F,Math.min(.38F,(c.getYRot()-c.yRotO)*-.1F*Math.abs(c.getDriveSpeed())/.2F)),.035F);a[3]+=c.getDriveSpeed()*2.9F;a[4]=c.tickCount;}
        pose.pushPose();if(spec==VehicleSpec.MOTORCYCLE){pose.translate(0,1.45,0);pose.mulPose(Axis.ZP.rotation(a[2]));pose.translate(0,-1.45,0);}
        var col=VehicleTextures.paint(c.color());part("body",pose,b,VehicleTextures.PAINT,light,overlay,col[0],col[1],col[2]);part("dark",pose,b,VehicleTextures.RUBBER,light,overlay,.10F,.12F,.13F);part("frame",pose,b,VehicleTextures.METAL,light,overlay,.28F,.32F,.34F);part("seats",pose,b,VehicleTextures.FABRIC,light,overlay,.14F,.21F,.25F);part("dashboard",pose,b,VehicleTextures.RUBBER,light,overlay,.09F,.11F,.12F);part("stripe",pose,b,VehicleTextures.PAINT,light,overlay,.95F,.91F,.75F);part("rails",pose,b,VehicleTextures.METAL,light,overlay,.9F,.72F,.08F);part("sign",pose,b,VehicleTextures.RUBBER,light,overlay,.015F,.025F,.02F);
        part("head",pose,b,VehicleTextures.LENS,c.systems.expansion.lightMode()>=2?LightTexture.FULL_BRIGHT:light,overlay,.92F,.94F,.87F);part("tail",pose,b,VehicleTextures.LENS,c.lightsOn()||c.isBraking()?LightTexture.FULL_BRIGHT:light,overlay,c.isBraking()?1:.6F,.02F,.025F);
        if(spec==VehicleSpec.BUS){for(int i=0;i<2;i++){pose.pushPose();pose.translate(0,0,a[i]*1.25);part("door"+i,pose,b,VehicleTextures.PAINT,light,overlay,col[0],col[1],col[2]);root.getChild("doorGlass"+i).render(pose,b.getBuffer(RenderType.entityTranslucent(VehicleTextures.PAINT)),light,overlay,.2F,.32F,.35F,.4F);pose.popPose();}}
        if(spec==VehicleSpec.PICKUP){var d=pickupDoors.computeIfAbsent(c,k->new float[]{0,0,-1});if(d[2]!=c.tickCount){for(int i=0;i<2;i++)d[i]=net.minecraft.util.Mth.approach(d[i],c.systems.mechanics.door(i)?1:0,.12F);d[2]=c.tickCount;}for(int i=0;i<2;i++){pose.pushPose();float x=i==0?15:-15;pose.translate(x/16,0,-24/16F);pose.mulPose(Axis.YP.rotation((i==0?1:-1)*d[i]*1.1F));pose.translate(-x/16,0,24/16F);part("pickupDoor"+i,pose,b,VehicleTextures.PAINT,light,overlay,col[0],col[1],col[2]);root.getChild("pickupGlass"+i).render(pose,b.getBuffer(RenderType.entityTranslucent(VehicleTextures.PAINT)),light,overlay,.2F,.32F,.35F,.32F);pose.popPose();}
        pose.pushPose();pose.translate(0,14/16F,35/16F);pose.mulPose(Axis.XP.rotation(-a[1]*(float)Math.PI/2));part("tailgate",pose,b,VehicleTextures.PAINT,light,overlay,col[0],col[1],col[2]);pose.popPose();}
        if(spec==VehicleSpec.MOTORCYCLE&&c.systems.expansion.stand()){pose.pushPose();pose.translate(-.12,14/16F,.3);pose.mulPose(Axis.ZP.rotation(.65F));part("stand",pose,b,VehicleTextures.METAL,light,overlay,.45F,.48F,.5F);pose.popPose();}
        for(int side:spec==VehicleSpec.MOTORCYCLE?new int[]{-1}:new int[]{-1,1})for(String prefix:new String[]{"front","rear"}){var w=root.getChild(prefix+side);w.xRot=a[3];w.yRot=prefix.equals("front")?Math.max(-.4F,Math.min(.4F,(c.getYRot()-c.yRotO)*.14F)):0;w.yScale=c.systems.upgrades.weakest()<15?.7F:1;pose.pushPose();w.translateAndRotate(pose);for(int i=0;i<8;i++)w.getChild("tyre"+i).render(pose,b.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.RUBBER)),light,overlay,.06F,.07F,.075F,1);w.getChild("rim").render(pose,b.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.METAL)),light,overlay,.62F,.67F,.7F,1);w.getChild("hub").render(pose,b.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.METAL)),light,overlay,.2F,.24F,.25F,1);pose.popPose();}
        root.getChild("glass").render(pose,b.getBuffer(RenderType.entityTranslucent(VehicleTextures.PAINT)),light,overlay,.2F,.32F,.35F,.32F);pose.popPose();
    }
}
