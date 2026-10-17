package de.nevergate.fahrzeuge.client;

import java.util.Locale;
import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import de.nevergate.fahrzeuge.VehicleSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class VehicleHud {
    private static final int TEXT=0xffdbeae5, ACCENT=0xffa9d9cd, AMBER=0xffffc36f, RED=0xffff8066;

    @SubscribeEvent public static void register(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("vehicle", (gui,g,partial,width,height) -> {
            var mc=Minecraft.getInstance();
            if(mc.options.hideGui || mc.player==null)return;
            VehicleDirectoryScreen.renderLocator(g,mc,width,height);
            if(mc.screen!=null || !(mc.player.getVehicle() instanceof NgCityEntity car))return;
            boolean details=VehicleHudSettings.details();
            var layout=HudLayout.fit(width,height,mc.getWindow().getGuiScale(),VehicleHudSettings.scale(),details);
            g.pose().pushPose();
            try {
                g.pose().scale((float)layout.scale(),(float)layout.scale(),1);
                g.pose().translate(layout.x(),layout.y(),0);
                g.fill(0,0,layout.width(),layout.height(),0xcc102b29);
                g.fill(0,0,2,layout.height(),0xff6cddc4);
                line(g,mc,car.getName(),6,ACCENT);
                var m=car.systems.mechanics;
                String gear=m.gear()==-1?"R":m.gear()==0?"N":Integer.toString(m.gear());
                line(g,mc,Component.literal(Math.round(Math.abs(car.getDriveSpeed())*72)+" km/h  ·  ")
                        .append(tr("gear",gear)),19,0xffffffff);
                line(g,mc,tr("fuel",decimal(car.systems.fuel()),Math.round(car.systems.tankCapacity())),32,TEXT);
                float fraction=Math.max(0,Math.min(1,car.systems.fuel()/Math.max(1,car.systems.tankCapacity())));
                g.fill(8,43,192,46,0xff344e49);
                g.fill(8,43,8+Math.round(184*fraction),46,fraction<0.1f?AMBER:0xff59d5b6);
                Component warning=warning(mc,car);
                line(g,mc,warning!=null?warning:tr("engine_state",on(car.engineRunning()),tr(car.isParkingBraked()?"brake_on":"brake_off")),50,warning!=null?RED:ACCENT);
                line(g,mc,tr("details_hint",DrivingClient.HUD_DETAILS.getTranslatedKeyMessage()),63,ACCENT);
                if(details)details(g,mc,car);
            } finally { g.pose().popPose(); }
        });
    }

    private static Component warning(Minecraft mc,NgCityEntity car) {
        if(car.systems.mechanics.temperature()>105)return tr("engine_hot");
        if(car.systems.fuel()<5)return tr("fuel_low");
        if(car.systems.comfort.battery()<5)return tr("battery_low");
        if(car.systems.upgrades.weakest()<15)return tr("tyres_low");
        if(car.spec()==VehicleSpec.BUS && car.systems.expansion.stopRequested())return tr("stop");
        if(car.getDriver()==mc.player && car.engineRunning() && !car.isParkingBraked()
                && car.spec()!=VehicleSpec.MOTORCYCLE && !car.systems.expansion.belted(mc.player))return tr("belt_warning");
        return null;
    }

    private static void details(GuiGraphics g,Minecraft mc,NgCityEntity car) {
        var e=car.systems.expansion;var m=car.systems.mechanics;
        g.fill(8,78,192,79,0xff344e49);
        line(g,mc,tr("distance",String.format(Locale.ROOT,"%.2f",car.systems.metres()/1000),
                String.format(Locale.ROOT,"%.2f",car.systems.trip()/1000)),84,TEXT);
        line(g,mc,Component.translatable("gui.ng_fahrzeuge.condition",Math.round(car.systems.body()),Math.round(car.systems.motor())),96,TEXT);
        Component signal=Component.translatable("state.ng_fahrzeuge.signal_"+car.signal());
        if(car.systems.cruise()>0)signal=signal.copy().append(" · ").append(tr("cruise"));
        line(g,mc,signal,108,AMBER);
        line(g,mc,tr("tyres",Math.round(car.systems.upgrades.weakest()),Math.round(car.systems.upgrades.dirt())),120,TEXT);
        line(g,mc,tr("light_modes",tr("light_"+Math.max(0,Math.min(3,e.lightMode()))),on(e.fog())),132,TEXT);
        line(g,mc,car.spec()==VehicleSpec.MOTORCYCLE?tr("stand",on(e.stand())):
                tr("belts",on(e.belted(mc.player)),Integer.bitCount(e.beltMask()),car.getPassengers().size()),144,TEXT);
        line(g,mc,tr("rpm",tr(m.manual()?"manual":"automatic"),Math.round(m.rpm())),156,TEXT);
        line(g,mc,tr("cooling",Math.round(m.temperature()),decimal(m.coolant())),168,m.temperature()>105?RED:TEXT);
        String assist=(m.awd()?"4×4 ":"")+(m.low()?"LOW ":"")+(m.hillTicks()>0?tr("hill").getString()+" ":"");
        line(g,mc,Component.literal(assist).append(tr(m.cargoLocked()?"trunk_locked":"trunk_open")),180,AMBER);
        line(g,mc,tr("battery",Math.round(car.systems.comfort.battery())),192,TEXT);
        if(car.spec()==VehicleSpec.BUS)line(g,mc,e.stopRequested()?tr("stop"):Component.literal(e.route()+" · "+e.destination()),204,AMBER);
        else if(car.systems.attached())line(g,mc,tr("coupled"),204,ACCENT);
    }

    private static void line(GuiGraphics g,Minecraft mc,Component value,int y,int color) {
        g.drawString(mc.font,mc.font.plainSubstrByWidth(value.getString(),184),8,y,color,false);
    }
    private static Component tr(String key,Object... args) { return Component.translatable("hud.ng_fahrzeuge."+key,args); }
    private static Component on(boolean value) { return tr(value?"on":"off"); }
    private static String decimal(double value) { return String.format(Locale.ROOT,"%.1f",value); }
    private VehicleHud() {}
}
