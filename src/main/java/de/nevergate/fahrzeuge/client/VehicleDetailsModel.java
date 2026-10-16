package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Shared visible load, separate lamps, stalk, buckled belts and progressive damage. */
public final class VehicleDetailsModel {
    private final ModelPart root;private final VehicleSpec spec;private final float length,width;
    public VehicleDetailsModel(VehicleSpec s){spec=s;length=s==VehicleSpec.TOW_TRUCK?46:s==VehicleSpec.TRANSPORTER?32.8F:s==VehicleSpec.FAMILY?28.4F:s==VehicleSpec.CITY?20.4F:s.length*8;width=s==VehicleSpec.MOTORCYCLE?3:s.width*7;
        var mesh=new MeshDefinition();var p=mesh.getRoot();
        p.addOrReplaceChild("crate",CubeListBuilder.create().addBox(-4,-6,-4,8,6,8),PartPose.ZERO);
        p.addOrReplaceChild("straps",CubeListBuilder.create().addBox(-4.1F,-6.1F,-.5F,8.2F,6.2F,1).addBox(-.5F,-6.1F,-4.1F,1,6.2F,8.2F),PartPose.ZERO);
        p.addOrReplaceChild("rack",CubeListBuilder.create().addBox(-12,-5,-24,24,1,24).addBox(-12,-7,-24,1,2,24).addBox(11,-7,-24,1,2,24).addBox(-12,-7,-24,24,2,1).addBox(-12,-7,-1,24,2,1),PartPose.ZERO);
        p.addOrReplaceChild("lamp",CubeListBuilder.create().addBox(-1,-.7F,-.3F,2,1.4F,.6F),PartPose.ZERO);
        p.addOrReplaceChild("belt",CubeListBuilder.create().addBox(-.35F,-6,0,.7F,12,.35F),PartPose.ZERO);
        p.addOrReplaceChild("stalk",CubeListBuilder.create().addBox(0,-.3F,-.3F,3,.6F,.6F).addBox(2.6F,-.6F,-.5F,1,1,1),PartPose.ZERO);
        p.addOrReplaceChild("dent",CubeListBuilder.create().addBox(-4,-2,-.3F,8,4,.6F).addBox(-3.5F,1.5F,-1,7,1,1.5F),PartPose.ZERO);
        p.addOrReplaceChild("scratch",CubeListBuilder.create().addBox(-4,0,0,8,.3F,.1F).addBox(-3,1,0,5,.25F,.1F),PartPose.ZERO);
        root=LayerDefinition.create(mesh,64,64).bakeRoot();
    }
    private void part(String n,PoseStack p,MultiBufferSource b,ResourceLocation t,int l,int o,float r,float g,float blue){root.getChild(n).render(p,b.getBuffer(RenderType.entityCutoutNoCull(t)),l,o,r,g,blue,1);}
    public void render(NgCityEntity c,PoseStack p,MultiBufferSource b,int light,int overlay,float partial){
        var e=c.systems.expansion;float ly=spec==VehicleSpec.BUS?11:spec==VehicleSpec.MOTORCYCLE?7:16;
        for(int side:new int[]{-1,1}){
            p.pushPose();p.translate(side*width*.7/16,ly/16,-(length+.8F)/16);boolean park=e.lightMode()>0&&c.systems.comfort.battery()>0;part("lamp",p,b,VehicleTextures.LENS,park?LightTexture.FULL_BRIGHT:light,overlay,park?1:.3F,park?.9F:.3F,.4F);p.popPose();
            p.pushPose();p.translate(side*width*.48/16,(ly+2)/16,-(length+.9F)/16);boolean fog=e.fog()&&c.systems.comfort.battery()>0;part("lamp",p,b,VehicleTextures.LENS,fog?LightTexture.FULL_BRIGHT:light,overlay,fog?1:.3F,fog?.86F:.3F,.35F);p.popPose();
            p.pushPose();p.translate(side*width*.23/16,(ly-3)/16,-(length+.9F)/16);boolean high=e.lightMode()==3&&c.systems.comfort.battery()>0;part("lamp",p,b,VehicleTextures.LENS,high?LightTexture.FULL_BRIGHT:light,overlay,high?.82F:.3F,high?.92F:.3F,high?1:.3F);p.popPose();
            p.pushPose();p.translate(side*width*.97/16,(ly-1)/16,-(length+1)/16);boolean flash=c.indicatorLit(side<0);part("lamp",p,b,VehicleTextures.LENS,flash?LightTexture.FULL_BRIGHT:light,overlay,flash?1:.5F,flash?.5F:.22F,.025F);p.popPose();
            if(spec.ordinal()>=4){p.pushPose();p.translate(side*width*.97/16,(ly-1)/16,(length+.4F)/16);part("lamp",p,b,VehicleTextures.LENS,flash?LightTexture.FULL_BRIGHT:light,overlay,flash?1:.5F,flash?.5F:.22F,.025F);p.popPose();}
        }
        // The stalk changes angle with the synchronized indicator, including hazards.
        p.pushPose();float driverZ=spec==VehicleSpec.BUS?-51:spec==VehicleSpec.TOW_TRUCK?-30:spec==VehicleSpec.TRANSPORTER?-24:spec==VehicleSpec.PICKUP?-21:spec==VehicleSpec.MOTORCYCLE?-12:spec==VehicleSpec.FAMILY?-12:-4;
        p.translate(spec==VehicleSpec.BUS?.52:.26,spec==VehicleSpec.BUS?0:spec==VehicleSpec.MOTORCYCLE?.06:.65,driverZ/16);p.mulPose(Axis.ZP.rotation(c.signal()==1?-.45F:c.signal()==2?.45F:0));part("stalk",p,b,VehicleTextures.RUBBER,light,overlay,.07F,.08F,.09F);p.popPose();
        if(c.systems.upgrades.rack()&&spec==VehicleSpec.PICKUP)part("rack",p,b,VehicleTextures.METAL,light,overlay,.3F,.35F,.37F);
        if(c.systems.upgrades.rack())for(int i=0;i<e.roofCrates();i++)crate(p,b,light,overlay,(i%2==0?-5:5),spec==VehicleSpec.TRANSPORTER?-20:spec==VehicleSpec.BUS?-25:spec==VehicleSpec.PICKUP?-6:-3,(i/2)*9+(spec==VehicleSpec.PICKUP?-17:-1));
        if(spec==VehicleSpec.PICKUP)for(int i=0;i<e.cargoCrates();i++)crate(p,b,light,overlay,(i%2==0?-6:6),12,(i/2)*9+6);
        float damage=1-c.systems.body()/100;
        if(damage>.05F){p.pushPose();p.translate(-width*.65/16,(ly+1)/16,-(length+1+damage*2)/16);p.mulPose(Axis.YP.rotation(-damage*.5F));p.mulPose(Axis.ZP.rotation(damage*.2F));part("dent",p,b,VehicleTextures.METAL,light,overlay,.24F,.25F,.26F);p.popPose();
            p.pushPose();p.translate(-width*.65/16,(ly-1)/16,-(length+1.5)/16);p.mulPose(Axis.ZP.rotation(.3F));part("scratch",p,b,VehicleTextures.METAL,light,overlay,.7F,.72F,.73F);p.popPose();
            p.pushPose();p.translate((width+1)/16,(ly-1)/16,length*.2/16);p.mulPose(Axis.YP.rotationDegrees(90));part("dent",p,b,VehicleTextures.RUBBER,light,overlay,.18F,.19F,.2F);p.popPose();}
        if(spec!=VehicleSpec.MOTORCYCLE)for(var passenger:c.getPassengers())if(passenger instanceof net.minecraft.world.entity.player.Player player&&e.belted(player)){
            var pos=c.getPassengerRidingPosition(player).subtract(c.position());double yaw=Math.toRadians(c.getYRot()),x=pos.x*Math.cos(yaw)+pos.z*Math.sin(yaw),z=-pos.x*Math.sin(yaw)+pos.z*Math.cos(yaw);
            p.pushPose();p.translate(-x,1.5-pos.y-.92,-z);p.mulPose(Axis.ZP.rotation(-.48F));part("belt",p,b,VehicleTextures.RUBBER,light,overlay,.06F,.07F,.075F);p.popPose();}
    }
    private void crate(PoseStack p,MultiBufferSource b,int l,int o,float x,float y,float z){p.pushPose();p.translate(x/16,y/16,z/16);part("crate",p,b,VehicleTextures.PAINT,l,o,.54F,.34F,.14F);part("straps",p,b,VehicleTextures.METAL,l,o,.23F,.2F,.15F);p.popPose();}
}
