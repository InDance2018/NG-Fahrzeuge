package de.nevergate.fahrzeuge.client;

import java.util.Locale;
import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class VehicleHud {
    @SubscribeEvent public static void register(RegisterGuiOverlaysEvent event){event.registerAboveAll("vehicle",(gui,g,partial,width,height)->{
        var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.player==null||!(mc.player.getVehicle() instanceof NgCityEntity car))return;
        int w=220,x=width-w-6,y=6;
        g.fill(x,y,x+w,y+104,0xdd102b29);g.fill(x,y,x+3,y+104,0xff6cddc4);
        g.drawString(mc.font,car.getName().copy().append(" · "+Math.round(car.systems.comfort.battery())+"%"),x+10,y+7,0xffc6f4e6,false);
        g.drawString(mc.font,Math.round(Math.abs(car.getDriveSpeed())*72)+" km/h",x+10,y+21,0xffffffff,false);
        String motor=Component.translatable(car.engineRunning()?"state.ng_fahrzeuge.engine_on":"state.ng_fahrzeuge.engine_off",DrivingClient.ENGINE.getTranslatedKeyMessage()).getString();
        g.drawString(mc.font,motor+" | "+(car.isParkingBraked()?"P":"—")+" | "+(car.lightsOn()?Component.translatable("state.ng_fahrzeuge.lights_on").getString():"—"),x+10,y+34,0xffa9d9cd,false);
        g.drawString(mc.font,Component.translatable("hud.ng_fahrzeuge.distance",String.format(Locale.ROOT,"%.2f",car.systems.metres()/1000),String.format(Locale.ROOT,"%.2f",car.systems.trip()/1000)),x+10,y+47,0xffdbeae5,false);
        g.drawString(mc.font,Component.translatable("hud.ng_fahrzeuge.fuel",String.format(Locale.ROOT,"%.1f",car.systems.fuel()),Math.round(car.systems.tankCapacity())),x+10,y+60,car.systems.fuel()<5?0xffffbd6c:0xffdbeae5,false);
        g.fill(x+10,y+72,x+w-10,y+77,0xff344e49);g.fill(x+10,y+72,x+10+Math.round((w-20)*car.systems.fuel()/car.systems.tankCapacity()),y+77,0xff59d5b6);
        g.drawString(mc.font,Component.translatable("gui.ng_fahrzeuge.condition",Math.round(car.systems.body()),Math.round(car.systems.motor())),x+10,y+82,0xffdbeae5,false);
        g.drawString(mc.font,Component.translatable("state.ng_fahrzeuge.signal_"+car.signal()).append(car.systems.cruise()>0?" · TEMPOMAT":"").append(car.systems.attached()?" · +":""),x+10,y+94,0xffffc36f,false);
    });}
}
