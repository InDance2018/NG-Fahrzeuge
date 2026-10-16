package de.nevergate.fahrzeuge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.VehicleSpec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public final class NgCityRenderer extends EntityRenderer<NgCityEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/block/white_concrete.png");
    private final NgCityModel model;
    private final SpecialVehicleModel special;private final VehicleDetailsModel details;

    public NgCityRenderer(EntityRendererProvider.Context context, VehicleSpec spec) {
        super(context);
        model = spec.ordinal()<4?new NgCityModel(context.bakeLayer(NgCityModel.layer(spec)), spec):null;special=spec.ordinal()>=4?new SpecialVehicleModel(spec):null;details=new VehicleDetailsModel(spec);
        shadowRadius = spec.length / 2.5F;
        VehicleTextures.initialize();
    }

    @Override
    public void render(NgCityEntity vehicle, float yaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.translate(0, 1.5, 0);
        pose.mulPose(Axis.YP.rotationDegrees(180 - yaw));
        pose.scale(-1, -1, 1);
        if(special!=null)special.render(vehicle,pose,buffers,light,OverlayTexture.NO_OVERLAY,partialTick);
        else {model.render(vehicle, pose, buffers, light, OverlayTexture.NO_OVERLAY, partialTick);model.renderGlass(vehicle,pose,buffers,light,OverlayTexture.NO_OVERLAY,partialTick);}
        details.render(vehicle,pose,buffers,light,OverlayTexture.NO_OVERLAY,partialTick);
        if(vehicle.spec()==VehicleSpec.BUS)busSigns(vehicle,pose,buffers,light);
        String plate=vehicle.systems.comfort.plate();
        if(!plate.isEmpty())for(boolean front:new boolean[]{true,false}){
            if(front&&vehicle.spec()==VehicleSpec.MOTORCYCLE)continue;
            float length=vehicle.spec().ordinal()>=4?vehicle.spec().length*8:vehicle.spec()==VehicleSpec.TOW_TRUCK?46:vehicle.spec()==VehicleSpec.TRANSPORTER?32.8F:vehicle.spec()==VehicleSpec.FAMILY?28.4F:20.4F;
            pose.pushPose();pose.translate(0,vehicle.spec()==VehicleSpec.TOW_TRUCK?1.12:1.01,(front?-1:1)*(length+2.8F)/16F);
            if(!front)pose.mulPose(Axis.YP.rotationDegrees(180));float plateScale=vehicle.spec()==VehicleSpec.MOTORCYCLE?.008F:.014F;pose.scale(plateScale,plateScale,plateScale);
            var font=net.minecraft.client.Minecraft.getInstance().font;font.drawInBatch(plate,-font.width(plate)/2F,0,0xff101514,false,pose.last().pose(),buffers,net.minecraft.client.gui.Font.DisplayMode.NORMAL,0xffe9eeeb,light);pose.popPose();
        }
        pose.popPose();
        var parentId=vehicle.systems.comfort.parent();
        if(parentId!=null&&(vehicle.systemTag().getInt("RecoveryMode")==1||vehicle.systemTag().getInt("RecoveryMode")==4)){
            for(var entity:net.minecraft.client.Minecraft.getInstance().level.entitiesForRendering())if(entity.getUUID().equals(parentId)&&entity instanceof NgCityEntity parent){
                boolean winch=vehicle.systemTag().getInt("RecoveryMode")==4;
                double pa=Math.toRadians(parent.getYRot()),ca=Math.toRadians(vehicle.getYRot());
                double hitch=winch?.7:parent.spec().length/2+.05;
                var end=parent.position().add(Math.sin(pa)*hitch,winch?1.05:.45,-Math.cos(pa)*hitch).subtract(vehicle.position());
                var start=new net.minecraft.world.phys.Vec3(-Math.sin(ca)*vehicle.spec().length/2,.45,Math.cos(ca)*vehicle.spec().length/2);
                var delta=end.subtract(start);if(delta.lengthSqr()<.001)break;
                var line=buffers.getBuffer(RenderType.lines());var matrix=pose.last();
                var normal=new org.joml.Vector3f((float)delta.x,(float)delta.y,(float)delta.z).normalize();
                line.vertex(matrix.pose(),(float)start.x,(float)start.y,(float)start.z).color(90,66,36,255).normal(matrix.normal(),normal.x,normal.y,normal.z).endVertex();
                line.vertex(matrix.pose(),(float)end.x,(float)end.y,(float)end.z).color(90,66,36,255).normal(matrix.normal(),normal.x,normal.y,normal.z).endVertex();break;
            }
        }
        super.render(vehicle, yaw, partialTick, pose, buffers, light);
    }

    private void busSigns(NgCityEntity car,PoseStack p,MultiBufferSource b,int light){String text=car.systems.expansion.route()+" "+car.systems.expansion.destination();if(text.isBlank())text="NG BUS";var font=net.minecraft.client.Minecraft.getInstance().font;
        for(int face=0;face<3;face++){p.pushPose();if(face==0)p.translate(0,-1.28,-4.025);else if(face==1){p.translate(0,-1.28,4.03);p.mulPose(Axis.YP.rotationDegrees(180));}else{p.translate(1.24,-1.24,-1.25);p.mulPose(Axis.YP.rotationDegrees(-90));}
            float scale=Math.min(.018F,(face==2?1.65F:1.95F)/Math.max(1,font.width(text)));p.scale(scale,scale,scale);font.drawInBatch(text,-font.width(text)/2F,0,0xffffb737,false,p.last().pose(),b,net.minecraft.client.gui.Font.DisplayMode.NORMAL,0,net.minecraft.client.renderer.LightTexture.FULL_BRIGHT);p.popPose();}}
    @Override public ResourceLocation getTextureLocation(NgCityEntity vehicle) { return TEXTURE; }
}
