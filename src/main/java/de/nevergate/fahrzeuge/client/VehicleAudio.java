package de.nevergate.fahrzeuge.client;

import java.util.HashMap;
import java.util.Map;
import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.VehicleSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

/** Two local looping sources per running vehicle; pitch follows synchronized speed. */
public final class VehicleAudio {
    private static final Map<NgCityEntity, Loop[]> ACTIVE = new HashMap<>();
    public static void tick(Minecraft mc) {
        ACTIVE.entrySet().removeIf(entry -> {
            NgCityEntity car=entry.getKey();
            if(mc.level!=car.level() || car.isRemoved() || !car.engineRunning()
                    || mc.player==null || mc.player.distanceToSqr(car)>4096) {
                for(Loop loop:entry.getValue())loop.finish(); return true;
            }
            return false;
        });
        if(mc.level==null || mc.player==null)return;
        for(var entity:mc.level.entitiesForRendering()) if(entity instanceof NgCityEntity car
                && car.engineRunning() && mc.player.distanceToSqr(car)<=4096 && !ACTIVE.containsKey(car)) {
            Loop[] loops={new Loop(car,false),new Loop(car,true)};ACTIVE.put(car,loops);
            for(Loop loop:loops)mc.getSoundManager().play(loop);
        }
    }
    private static final class Loop extends AbstractTickableSoundInstance {
        private final NgCityEntity car;
        private final boolean road;
        Loop(NgCityEntity car,boolean road) {
            super(road?VehicleSounds.ROAD.get():VehicleSounds.engine(car.spec()),SoundSource.NEUTRAL,SoundInstance.createUnseededRandom());
            this.car=car;this.road=road;looping=true;delay=0;relative=false;volume=.01F;tick();
        }
        void finish(){stop();}
        @Override public boolean canStartSilent(){return true;}
        @Override public void tick(){
            if(car.isRemoved() || !car.engineRunning()){stop();return;}
            x=car.getX();y=car.getY()+.4;z=car.getZ();
            float speed=Math.min(1,Math.abs(car.getDriveSpeed())/NgCityEntity.MAX_FORWARD);
            pitch=road?.8F+speed*.5F:.75F+speed*1.15F;
            volume=road?Math.max(.001F,speed*.32F):.23F+speed*.28F;
        }
    }
    private VehicleAudio(){}
}
