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

    public NgCityRenderer(EntityRendererProvider.Context context, VehicleSpec spec) {
        super(context);
        model = new NgCityModel(context.bakeLayer(NgCityModel.layer(spec)), spec);
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
        model.render(vehicle, pose, buffers, light, OverlayTexture.NO_OVERLAY, partialTick);
        model.renderGlass(vehicle,pose, buffers,
                light, OverlayTexture.NO_OVERLAY,partialTick);
        String plate=vehicle.systems.comfort.plate();
        if(!plate.isEmpty())for(boolean front:new boolean[]{true,false}){
            float length=vehicle.spec()==VehicleSpec.TOW_TRUCK?46:vehicle.spec()==VehicleSpec.TRANSPORTER?32.8F:vehicle.spec()==VehicleSpec.FAMILY?28.4F:20.4F;
            pose.pushPose();pose.translate(0,vehicle.spec()==VehicleSpec.TOW_TRUCK?1.12:1.01,(front?-1:1)*(length+2.8F)/16F);
            if(!front)pose.mulPose(Axis.YP.rotationDegrees(180));pose.scale(.014F,.014F,.014F);
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

    @Override public ResourceLocation getTextureLocation(NgCityEntity vehicle) { return TEXTURE; }
}
