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
        pose.popPose();
        super.render(vehicle, yaw, partialTick, pose, buffers, light);
    }

    @Override public ResourceLocation getTextureLocation(NgCityEntity vehicle) { return TEXTURE; }
}
