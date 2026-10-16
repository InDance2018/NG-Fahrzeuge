package de.nevergate.fahrzeuge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.nevergate.fahrzeuge.TrailerEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public final class TrailerRenderer extends EntityRenderer<TrailerEntity> {
    private final ModelPart model;
    public TrailerRenderer(EntityRendererProvider.Context context){
        super(context);VehicleTextures.initialize();shadowRadius=1;
        MeshDefinition mesh=new MeshDefinition();var root=mesh.getRoot();
        root.addOrReplaceChild("frame",CubeListBuilder.create().addBox(-12,17,-17,24,2,34)
                .addBox(-1,18,-31,2,2,15).addBox(-3,18,-32,6,2,3),PartPose.ZERO);
        root.addOrReplaceChild("bed",CubeListBuilder.create().addBox(-11,15,-16,22,2,32)
                .addBox(-12,8,-17,1,8,34).addBox(11,8,-17,1,8,34)
                .addBox(-11,8,-17,22,8,1).addBox(-11,8,16,22,8,1),PartPose.ZERO);
        root.addOrReplaceChild("box",CubeListBuilder.create().addBox(-11,-3,-15,22,1,30).addBox(-11,-2,-15,1,10,30).addBox(10,-2,-15,1,10,30).addBox(-11,-2,-15,22,10,1).addBox(-11,-2,14,22,10,1),PartPose.ZERO);
        root.addOrReplaceChild("canvas",CubeListBuilder.create().addBox(-11,-14,-15,22,1,30).addBox(-11,-13,-15,1,21,30).addBox(10,-13,-15,1,21,30).addBox(-11,-13,-15,22,21,1).addBox(-11,-13,14,22,21,1),PartPose.ZERO);
        for(int side:new int[]{-1,1}){
            root.addOrReplaceChild("tyre"+side,CubeListBuilder.create().addBox(side*12-1.5F,14,-3.6F,3,10,7.2F)
                    .addBox(side*12-1.5F,15.4F,-5,3,7.2F,10),PartPose.ZERO);
            root.addOrReplaceChild("rim"+side,CubeListBuilder.create().addBox(side<0?-13.6F:13.5F,16.4F,-2.6F,.1F,5.2F,5.2F),PartPose.ZERO);
            root.addOrReplaceChild("white"+side,CubeListBuilder.create().addBox(side<0?-5:3,13,17.2F,2,2,.3F),PartPose.ZERO);
            root.addOrReplaceChild("red"+side,CubeListBuilder.create().addBox(side<0?-10:6,13,17.1F,4,2,.3F),PartPose.ZERO);
            root.addOrReplaceChild("amber"+side,CubeListBuilder.create().addBox(side<0?-11:9,13,17.5F,2,2,.3F),PartPose.ZERO);
        }
        model=LayerDefinition.create(mesh,128,128).bakeRoot();
    }
    private void part(String name,PoseStack p,MultiBufferSource b,ResourceLocation texture,int light,float r,float g,float blue){
        model.getChild(name).render(p,b.getBuffer(RenderType.entityCutoutNoCull(texture)),light,OverlayTexture.NO_OVERLAY,r,g,blue,1);
    }
    @Override public void render(TrailerEntity t,float yaw,float partial,PoseStack p,MultiBufferSource b,int light){
        p.pushPose();p.translate(0,1.5,0);p.mulPose(Axis.YP.rotationDegrees(180-yaw));p.scale(-1,-1,1);if(t.variant()==1)p.scale(.9F,1,.83F);else if(t.variant()==2)p.scale(1,1,1.4F);
        if(t.variant()==1)part("box",p,b,VehicleTextures.PAINT,light,.65F,.75F,.72F);else if(t.variant()==2)part("canvas",p,b,VehicleTextures.FABRIC,light,.20F,.35F,.31F);
        part("frame",p,b,VehicleTextures.METAL,light,.30F,.34F,.35F);
        part("bed",p,b,VehicleTextures.PAINT,light,.49F,.72F,.67F);
        for(int side:new int[]{-1,1}){
            part("tyre"+side,p,b,VehicleTextures.RUBBER,light,.08F,.09F,.1F);
            part("rim"+side,p,b,VehicleTextures.METAL,light,.7F,.74F,.75F);
            part("red"+side,p,b,VehicleTextures.LENS,t.lights()||t.braking()?LightTexture.FULL_BRIGHT:light,t.braking()?1:.6F,.02F,.02F);
            part("white"+side,p,b,VehicleTextures.LENS,t.reversing()?LightTexture.FULL_BRIGHT:light,t.reversing()?1:.5F,t.reversing()?1:.5F,t.reversing()?1:.5F);
            boolean flashing=(t.signal()==3||t.signal()==(side>0?1:2))&&t.level().getGameTime()%20<10;
            part("amber"+side,p,b,VehicleTextures.LENS,flashing?LightTexture.FULL_BRIGHT:light,flashing?1:.6F,.38F,.02F);
        }p.popPose();super.render(t,yaw,partial,p,b,light);
    }
    @Override public ResourceLocation getTextureLocation(TrailerEntity t){return VehicleTextures.PAINT;}
}
