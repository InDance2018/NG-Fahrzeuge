package de.nevergate.fahrzeuge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import de.nevergate.fahrzeuge.VehicleSpec;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

/** Original code-native meshes for all three vehicle silhouettes. */
public final class NgCityModel {
    private final ModelPart root;
    public NgCityModel(ModelPart root) { this.root = root; }
    public static ModelLayerLocation layer(VehicleSpec spec) {
        return new ModelLayerLocation(new ResourceLocation(NgFahrzeuge.MOD_ID, spec.id), "main");
    }
    public static LayerDefinition createLayer(VehicleSpec spec) {
        MeshDefinition mesh = new MeshDefinition();
        var parts = mesh.getRoot();
        float w = spec == VehicleSpec.CITY ? 12 : spec == VehicleSpec.FAMILY ? 13 : 15;
        float length = spec == VehicleSpec.CITY ? 20 : spec == VehicleSpec.FAMILY ? 28 : 32;
        boolean van = spec == VehicleSpec.TRANSPORTER;
        CubeListBuilder body = CubeListBuilder.create().addBox(-w, 13, -length, 2*w, 6, 2*length);
        CubeListBuilder glass = CubeListBuilder.create();
        CubeListBuilder stripe = CubeListBuilder.create();
        CubeListBuilder trim = CubeListBuilder.create();
        if (van) {
            body.addBox(-w, -14, -8, 2*w, 27, length+8)
                .addBox(-w, -16, -31, 2*w, 2, length+31)
                .addBox(-w, -14, -32, 2, 20, 2)
                .addBox(w-2, -14, -32, 2, 20, 2)
                .addBox(-w, -14, -10, 2, 27, 3)
                .addBox(w-2, -14, -10, 2, 27, 3)
                .addBox(-w, 5, -32, 2*w, 8, 24);
            glass.addBox(-w+2, -12, -31.8F, 2*w-4, 17, 1)
                .addBox(-w+0.2F, -12, -30, 1, 17, 20)
                .addBox(w-1.2F, -12, -30, 1, 17, 20);
            stripe.addBox(-w-0.1F, 9, -32, 0.2F, 3, 64)
                  .addBox(w-0.1F, 9, -32, 0.2F, 3, 64)
                  .addBox(-w, 9, length-0.1F, 2*w, 3, 0.2F);
            trim.addBox(-0.25F, -13, length, 0.5F, 24, 0.2F);
        } else {
            boolean family = spec == VehicleSpec.FAMILY;
            float front = family ? -16 : -6, back = family ? 22 : 13;
            float roof = family ? 1 : 3;
            body.addBox(-w, 11, -length, 2*w, 2, family ? 13 : 12)
                .addBox(-w+1, roof, front, 2*w-2, 2, back-front)
                .addBox(-w+1, roof+2, front, 2, 11-roof, 2)
                .addBox(w-3, roof+2, front, 2, 11-roof, 2)
                .addBox(-w+1, roof+2, back-2, 2, 11-roof, 2)
                .addBox(w-3, roof+2, back-2, 2, 11-roof, 2);
            if (family) {
                body.addBox(-w+1, roof+2, 3, 2, 11-roof, 2)
                    .addBox(w-3, roof+2, 3, 2, 11-roof, 2);
            }
            glass.addBox(-w+3, roof+2, front+0.2F, 2*w-6, 11-roof, 1)
                .addBox(-w+3, roof+2, back-1.8F, 2*w-6, 11-roof, 1)
                .addBox(-w+1.2F, roof+2, front+2, 1, 11-roof, back-front-4)
                .addBox(w-2.2F, roof+2, front+2, 1, 11-roof, back-front-4);
        }
        float axle = length - 7;
        CubeListBuilder rubber = CubeListBuilder.create();
        for (float x : new float[]{-w-2, w-2}) for (float z : new float[]{-axle, axle-4}) {
            rubber.addBox(x, 18, z, 4, 6, 6);
        }
        rubber.addBox(-w, 18, -length-0.5F, 2*w, 2, 1)
              .addBox(-w, 18, length-0.5F, 2*w, 2, 1);
        for (float x : new float[]{-w-2.1F,w+1.9F}) for (float z : new float[]{-axle+2,axle-2}) {
            trim.addBox(x, 20, z, 0.2F, 2, 2);
        }
        trim.addBox(-w-0.1F, van ? 6 : 13.5F, 0, 0.2F, 1, 3)
            .addBox(w-0.1F, van ? 6 : 13.5F, 0, 0.2F, 1, 3);
        parts.addOrReplaceChild("body", body, PartPose.ZERO);
        parts.addOrReplaceChild("glass", glass, PartPose.ZERO);
        parts.addOrReplaceChild("stripe", stripe, PartPose.ZERO);
        parts.addOrReplaceChild("rubber", rubber, PartPose.ZERO);
        parts.addOrReplaceChild("trim", trim, PartPose.ZERO);
        parts.addOrReplaceChild("front_lights", CubeListBuilder.create()
            .addBox(-w+2, 14, -length-0.3F, 5, 2, 0.5F)
            .addBox(w-7, 14, -length-0.3F, 5, 2, 0.5F), PartPose.ZERO);
        parts.addOrReplaceChild("rear_lights", CubeListBuilder.create()
            .addBox(-w+2, 14, length-0.2F, 4, 2, 0.5F)
            .addBox(w-6, 14, length-0.2F, 4, 2, 0.5F), PartPose.ZERO);
        parts.addOrReplaceChild("reverse_lights", CubeListBuilder.create()
            .addBox(-w+6, 15, length-0.2F, 2, 1, 0.5F)
            .addBox(w-8, 15, length-0.2F, 2, 1, 0.5F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    public void render(NgCityEntity vehicle, PoseStack pose, VertexConsumer buffer, int light, int overlay) {
        float[] color = DyeColor.byId(vehicle.color()).getTextureDiffuseColors();
        part("body", pose, buffer, light, overlay, color[0], color[1], color[2]);
        part("stripe", pose, buffer, light, overlay, 0.08F, 0.68F, 0.70F);
        part("rubber", pose, buffer, light, overlay, 0.06F, 0.07F, 0.08F);
        part("trim", pose, buffer, light, overlay, 0.65F, 0.69F, 0.71F);
        int front = vehicle.lightsOn() ? LightTexture.FULL_BRIGHT : light;
        float head = vehicle.lightsOn() ? 1F : 0.25F;
        part("front_lights", pose, buffer, front, overlay, head, head*0.95F, head*0.75F);
        boolean rear = vehicle.lightsOn() || vehicle.isBraking();
        part("rear_lights", pose, buffer, rear ? LightTexture.FULL_BRIGHT : light, overlay,
                vehicle.isBraking() ? 1F : rear ? 0.65F : 0.25F, 0.03F, 0.02F);
        boolean reverse = vehicle.getDriveSpeed() < -0.001F;
        float r = reverse ? 1F : 0.2F;
        part("reverse_lights", pose, buffer, reverse ? LightTexture.FULL_BRIGHT : light, overlay, r, r, r);
    }
    public void renderGlass(PoseStack pose, VertexConsumer buffer, int light, int overlay) {
        root.getChild("glass").render(pose, buffer, light, overlay, 0.28F, 0.45F, 0.52F, 0.25F);
    }
    private void part(String name, PoseStack pose, VertexConsumer buffer, int light,
                      int overlay, float red, float green, float blue) {
        root.getChild(name).render(pose, buffer, light, overlay, red, green, blue, 1);
    }
}
