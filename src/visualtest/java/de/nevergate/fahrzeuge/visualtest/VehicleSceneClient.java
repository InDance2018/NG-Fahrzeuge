package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,value=Dist.CLIENT)
public final class VehicleSceneClient {
    private static int ticks,stage;
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
        if(Boolean.getBoolean("ng_fahrzeuge.dev10Visual")||Boolean.getBoolean("ng_fahrzeuge.dev9Visual")||Boolean.getBoolean("ng_fahrzeuge.dev7Visual")||Boolean.getBoolean("ng_fahrzeuge.dev8Visual"))return;
        if(event.phase!=TickEvent.Phase.END)return;
        var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
        if(mc.player==null || !mc.player.isSpectator())return;
        mc.options.hideGui=true;mc.options.fov().set(35);mc.options.renderDistance().set(5);
        ticks++;
        if(ticks==70)camera(mc,0,false);
        if(ticks==120){shot(mc,"city-front");camera(mc,1,false);}
        if(ticks==180){shot(mc,"family-front");camera(mc,2,false);}
        if(ticks==240){shot(mc,"transporter-front");camera(mc,1,true);}
        if(ticks==300){shot(mc,"family-rear");camera(mc,0,true);}
        if(ticks==360){shot(mc,"city-rear");camera(mc,2,true);}
        if(ticks==420)shot(mc,"transporter-rear");
        if(ticks==460){System.out.println("NG_VISUAL_COMPLETE");mc.stop();}
    }
    private static void camera(Minecraft mc,int index,boolean rear){
        var server=mc.getSingleplayerServer();if(server==null)return;
        var id=mc.player.getUUID();
        server.execute(()->{
            var p=server.getPlayerList().getPlayer(id);if(p==null)return;
            double x=index*48+(index==2?4.5:3.3),z=(rear?-1:1)*(index==2?6.4:4.9),y=140.55+(index==2?.9:0);
            p.teleportTo(p.serverLevel(),x,y,z,rear?35:145,16F);
        });
    }
    private static void shot(Minecraft mc,String name){
        Screenshot.grab(mc.gameDirectory,"dev6-"+name+".png",mc.getMainRenderTarget(),m->System.out.println("NG_VISUAL_SCREENSHOT "+m.getString()));
    }
}
