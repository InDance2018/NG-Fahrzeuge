package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID)
public final class VehicleSceneServer {
    private static int ticks;
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event){
        if(Boolean.getBoolean("ng_fahrzeuge.dev12Visual")||Boolean.getBoolean("ng_fahrzeuge.dev11Visual")||Boolean.getBoolean("ng_fahrzeuge.dev10Visual")||Boolean.getBoolean("ng_fahrzeuge.dev9Visual")||Boolean.getBoolean("ng_fahrzeuge.dev7Visual")||Boolean.getBoolean("ng_fahrzeuge.dev8Visual"))return;
        if(event.phase!=TickEvent.Phase.END || event.getServer().getPlayerList().getPlayers().isEmpty())return;
        ticks++;
        var player=event.getServer().getPlayerList().getPlayers().get(0);var level=player.serverLevel();
        if(ticks==1){
            level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT).set(false,event.getServer());
            level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING).set(false,event.getServer());
        }
        if(ticks==40){
            player.setGameMode(GameType.SPECTATOR);
            level.setDayTime(5000);level.setWeatherParameters(100000,0,false,false);
            for(int i=0;i<3;i++){
                int x=i*48;
                for(int xx=x-9;xx<=x+9;xx++)for(int z=-10;z<=10;z++)level.setBlockAndUpdate(new BlockPos(xx,139,z),Blocks.SMOOTH_STONE.defaultBlockState());
                var car=VehicleContent.type(VehicleSpec.values()[i]).create(level);car.setPos(x,140,0);level.addFreshEntity(car);
            }
            player.teleportTo(4,142.1,6);
            System.out.println("NG_VISUAL_SCENE_READY");
        }
    }
}
