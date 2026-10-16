package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.NgCityEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

/** Local camera entity only; driving and server authority remain with the real player. */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid=de.nevergate.fahrzeuge.NgFahrzeuge.MOD_ID,value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class VehicleCamera {
    @net.minecraftforge.eventbus.api.SubscribeEvent public static void hand(net.minecraftforge.client.event.RenderHandEvent event){if(following)event.setCanceled(true);}
    @net.minecraftforge.eventbus.api.SubscribeEvent public static void crosshair(net.minecraftforge.client.event.RenderGuiOverlayEvent.Pre event){if(following&&event.getOverlay().id().equals(net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.CROSSHAIR.id()))event.setCanceled(true);}
    private static ArmorStand camera;private static CameraType previous;private static boolean following;
    public static void toggle(Minecraft mc){
        if(mc.player==null||!(mc.player.getVehicle() instanceof NgCityEntity))return;
        if(following){restore(mc);return;}previous=mc.options.getCameraType();mc.options.setCameraType(CameraType.FIRST_PERSON);following=true;
        camera=new ArmorStand(mc.level,mc.player.getX(),mc.player.getY(),mc.player.getZ());camera.setInvisible(true);mc.setCameraEntity(camera);tick(mc);
    }
    public static void restore(Minecraft mc){if(following){mc.setCameraEntity(mc.player);if(previous!=null)mc.options.setCameraType(previous);}following=false;camera=null;}
    public static boolean following(){return following;}
    public static void tick(Minecraft mc){
        if(!following)return;
        if(mc.player==null||mc.level==null||camera==null||camera.level()!=mc.level||!(mc.player.getVehicle() instanceof NgCityEntity car)){restore(mc);return;}
        if(mc.getCameraEntity()!=camera){restore(mc);return;}
        double a=Math.toRadians(car.getYRot());Vec3 target=car.position().add(0,1.2,0),desired=target.add(Math.sin(a)*6,2.4,-Math.cos(a)*6);
        var hit=mc.level.clip(new ClipContext(target,desired,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,car));
        if(hit.getType()!=net.minecraft.world.phys.HitResult.Type.MISS)desired=hit.getLocation().add(target.subtract(hit.getLocation()).normalize().scale(.3));
        Vec3 look=target.subtract(desired);float yaw=(float)Math.toDegrees(Math.atan2(-look.x,look.z)),pitch=(float)-Math.toDegrees(Math.atan2(look.y,look.horizontalDistance()));
        camera.setOldPosAndRot();camera.setPos(desired.x,desired.y-camera.getEyeHeight(),desired.z);camera.setYRot(yaw);camera.setXRot(pitch);mc.options.setCameraType(CameraType.FIRST_PERSON);
    }
}
