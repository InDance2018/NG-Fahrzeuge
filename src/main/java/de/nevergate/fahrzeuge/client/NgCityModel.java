package de.nevergate.fahrzeuge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

/** Original block-style prototype geometry. Coordinates use 16 pixels per block. */
public final class NgCityModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(NgFahrzeuge.MOD_ID, "ng_city"), "main");
    private final ModelPart root;

    public NgCityModel(ModelPart root) { this.root = root; }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        var parts = mesh.getRoot();
        parts.addOrReplaceChild("body", CubeListBuilder.create()
                .addBox(-12, 13, -20, 24, 6, 40)
                .addBox(-12, 11, -20, 24, 2, 12)
                .addBox(-11, 3, -6, 22, 2, 19)
                .addBox(-11, 5, -6, 2, 8, 2)
                .addBox(9, 5, -6, 2, 8, 2)
                .addBox(-11, 5, 11, 2, 8, 2)
                .addBox(9, 5, 11, 2, 8, 2), PartPose.ZERO);
        parts.addOrReplaceChild("glass", CubeListBuilder.create()
                .addBox(-9, 5, -5.8F, 18, 8, 1)
                .addBox(-9, 5, 11.2F, 18, 8, 1)
                .addBox(-10.8F, 5, -4, 1, 8, 15)
                .addBox(9.8F, 5, -4, 1, 8, 15), PartPose.ZERO);
        parts.addOrReplaceChild("rubber", CubeListBuilder.create()
                .addBox(-14, 18, -15, 4, 6, 6)
                .addBox(10, 18, -15, 4, 6, 6)
                .addBox(-14, 18, 10, 4, 6, 6)
                .addBox(10, 18, 10, 4, 6, 6)
                .addBox(-12, 18, -20.5F, 24, 2, 1)
                .addBox(-12, 18, 19.5F, 24, 2, 1), PartPose.ZERO);
        parts.addOrReplaceChild("trim", CubeListBuilder.create()
                .addBox(-14.1F, 20, -13, 0.2F, 2, 2)
                .addBox(13.9F, 20, -13, 0.2F, 2, 2)
                .addBox(-14.1F, 20, 12, 0.2F, 2, 2)
                .addBox(13.9F, 20, 12, 0.2F, 2, 2)
                .addBox(-12.1F, 13.5F, 0, 0.2F, 1, 3)
                .addBox(11.9F, 13.5F, 0, 0.2F, 1, 3), PartPose.ZERO);
        parts.addOrReplaceChild("front_lights", CubeListBuilder.create()
                .addBox(-10, 14, -20.3F, 5, 2, 0.5F)
                .addBox(5, 14, -20.3F, 5, 2, 0.5F), PartPose.ZERO);
        parts.addOrReplaceChild("rear_lights", CubeListBuilder.create()
                .addBox(-10, 14, 19.8F, 4, 2, 0.5F)
                .addBox(6, 14, 19.8F, 4, 2, 0.5F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    public void render(PoseStack pose, VertexConsumer buffer, int light, int overlay) {
        part("body", pose, buffer, light, overlay, 0.08F, 0.68F, 0.70F);
        part("glass", pose, buffer, light, overlay, 0.14F, 0.23F, 0.28F);
        part("rubber", pose, buffer, light, overlay, 0.06F, 0.07F, 0.08F);
        part("trim", pose, buffer, light, overlay, 0.65F, 0.69F, 0.71F);
        part("front_lights", pose, buffer, light, overlay, 0.98F, 0.91F, 0.62F);
        part("rear_lights", pose, buffer, light, overlay, 0.78F, 0.08F, 0.06F);
    }

    private void part(String name, PoseStack pose, VertexConsumer buffer, int light,
                      int overlay, float red, float green, float blue) {
        root.getChild(name).render(pose, buffer, light, overlay, red, green, blue, 1);
    }
}
