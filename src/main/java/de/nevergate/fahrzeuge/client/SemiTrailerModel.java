package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
public final class SemiTrailerModel {
 private final ModelPart root;
 private static CubeListBuilder box(float x,float y,float z,float w,float h,float d){return CubeListBuilder.create().addBox(x,y,z,w,h,d);}
 public SemiTrailerModel(){var m=new MeshDefinition();var p=m.getRoot();p.addOrReplaceChild("body",box(-19,-28,-64,38,2,128).addBox(-19,-26,-64,1.2F,34,128).addBox(17.8F,-26,-64,1.2F,34,128).addBox(-19,-26,-64,38,34,1.2F).addBox(-19,-26,62.8F,38,34,1.2F).addBox(-19,8,-64,38,1.5F,128),PartPose.ZERO);
  p.addOrReplaceChild("kingpin",box(-1,8,-50.6F,2,4.5F,2),PartPose.ZERO);
  p.addOrReplaceChild("frame",box(-14,11,-50,2,7,110).addBox(12,11,-50,2,7,110).addBox(-18,19,63,36,2,2),PartPose.ZERO);
  p.addOrReplaceChild("doors",box(-.4F,-26,64,.8F,34,.25F).addBox(-13,-20,64.1F,.7F,27,.7F).addBox(12,-20,64.1F,.7F,27,.7F),PartPose.ZERO);
  p.addOrReplaceChild("leg",box(-.7F,10,-.7F,1.4F,13,1.4F).addBox(-3,23,-3,6,1,6),PartPose.ZERO);
  for(int side:new int[]{-1,1}){p.addOrReplaceChild("tyres"+side,box(side*18-2,14,30,4,10,9).addBox(side*18-2,14,43,4,10,9).addBox(side*18-2,14,55,4,10,9),PartPose.ZERO);p.addOrReplaceChild("rims"+side,box(side*18-2.1F,16,32,4.2F,6,5).addBox(side*18-2.1F,16,45,4.2F,6,5).addBox(side*18-2.1F,16,57,4.2F,6,5),PartPose.ZERO);p.addOrReplaceChild("red"+side,box(side<0?-17:12,17,64.3F,5,2,.4F),PartPose.ZERO);p.addOrReplaceChild("amber"+side,box(side<0?-18:16,17,64.4F,2,2,.4F),PartPose.ZERO);p.addOrReplaceChild("white"+side,box(side<0?-8:5,17,64.3F,3,2,.4F),PartPose.ZERO);}
  root=LayerDefinition.create(m,128,128).bakeRoot();
 }
 private void part(String n,PoseStack p,MultiBufferSource b,ResourceLocation t,int l,float r,float g,float blue){root.getChild(n).render(p,b.getBuffer(RenderType.entityCutoutNoCull(t)),l,OverlayTexture.NO_OVERLAY,r,g,blue,1);}
 public void render(TrailerEntity t,PoseStack p,MultiBufferSource b,int light){part("body",p,b,VehicleTextures.PAINT,light,.78F,.83F,.84F);part("frame",p,b,VehicleTextures.METAL,light,.21F,.25F,.28F);part("kingpin",p,b,VehicleTextures.METAL,light,.4F,.43F,.45F);part("doors",p,b,VehicleTextures.METAL,light,.38F,.42F,.44F);
  for(int side:new int[]{-1,1}){p.pushPose();p.translate(side*15/16F,t.supports()?0:-.65,-25/16F);part("leg",p,b,VehicleTextures.METAL,light,.3F,.35F,.37F);p.popPose();part("tyres"+side,p,b,VehicleTextures.RUBBER,light,.065F,.075F,.08F);part("rims"+side,p,b,VehicleTextures.METAL,light,.63F,.67F,.69F);part("red"+side,p,b,VehicleTextures.LENS,t.braking()||t.lights()?LightTexture.FULL_BRIGHT:light,t.braking()?1:.55F,.02F,.02F);part("white"+side,p,b,VehicleTextures.LENS,t.reversing()?LightTexture.FULL_BRIGHT:light,.9F,.9F,.9F);boolean flash=(t.signal()==3||t.signal()==(side<0?1:2))&&t.level().getGameTime()%20<10;part("amber"+side,p,b,VehicleTextures.LENS,flash?LightTexture.FULL_BRIGHT:light,flash?1:.55F,.4F,.02F);}
 }
}
