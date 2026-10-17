package de.nevergate.fahrzeuge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
/** Server-owned transmission, cooling, individual doors and off-road equipment. */
public final class VehicleMechanics {
    public static final int AWD=24,LOW=25,HILL=26,GEAR_MODE=27,UP=28,DOWN=29,TAXI=30,CARGO_LOCK=31,DOOR_FL=32,DOOR_FR=33,DOOR_RL=34,DOOR_RR=35;
    private final NgCityEntity car;
    private boolean awd,low,hill=true,manual,taxi=true,cargoLocked;
    private int gear=1,doorMask,holdTicks;private float radiator=100,coolant=5,temperature=20,rpm=800;
    VehicleMechanics(NgCityEntity c){car=c;}
    private boolean client(){return car.level().isClientSide;}
    private CompoundTag tag(){return car.systemTag();}
    public boolean awd(){return client()?tag().getBoolean("AWD"):awd;}
    public boolean low(){return client()?tag().getBoolean("LowRange"):low;}
    public boolean hill(){return client()?tag().getBoolean("HillAssist"):hill;}
    public boolean manual(){return client()?tag().getBoolean("ManualGears"):manual;}
    public boolean taxiEnabled(){return client()?tag().getBoolean("TaxiSign"):taxi;}
    public boolean taxiFree(){return client()?tag().getBoolean("TaxiFree"):taxiEnabled()&&car.getPassengers().stream().noneMatch(p->p!=car.observedDriver());}
    public boolean cargoLocked(){return client()?tag().getBoolean("CargoLocked"):cargoLocked;}
    public int gear(){return client()?tag().getInt("Gear"):gear;}
    public int doorMask(){return client()?tag().getInt("IndividualDoors"):doorMask;}
    public boolean door(int index){return (doorMask()&(1<<index))!=0;}
    public int doorCount(){return car.spec().seats>=4?4:2;}
    public float radiator(){return client()?tag().getFloat("Radiator"):radiator;}
    public float coolant(){return client()?tag().getFloat("Coolant"):coolant;}
    public float temperature(){return client()?tag().getFloat("Temperature"):temperature;}
    public float rpm(){return client()?tag().getFloat("RPM"):rpm;}
    public int hillTicks(){return client()?tag().getInt("HillTicks"):holdTicks;}
    public boolean action(Player p,int action){
        if(client()||car.isRemoved()||(car.getDriver()!=p&&!(car.equipment.isOwner(p)&&car.systems.serviceAllowed(p)))||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||!car.equipment.authorized(p))return false;
        if(action==UP||action==DOWN){if(!manual)return false;int next=Mth.clamp(gear+(action==UP?1:-1),-1,6);if(next==gear||Math.abs(car.getDriveSpeed())>.015F&&(next<=0||gear<=0))return false;if(next>0&&Math.abs(car.getDriveSpeed())>new float[]{0,.15F,.25F,.36F,.48F,.61F,.75F}[next]*1.2F)return false;gear=next;}
        else if(action==GEAR_MODE){if(!stationary())return false;manual=!manual;gear=1;}
        else if(action==AWD||action==LOW){if(car.spec()!=VehicleSpec.OFFROAD||!stationary())return false;if(action==AWD){awd=!awd;if(!awd)low=false;}else{if(!awd)return false;low=!low;}}
        else if(action==HILL){hill=!hill;if(!hill)holdTicks=0;}
        else if(action==TAXI){if(car.spec()!=VehicleSpec.TAXI)return false;taxi=!taxi;}
        else if(action==CARGO_LOCK)return toggleCargoLock(p);
        else if(action>=DOOR_FL&&action<=DOOR_RR)return toggleDoor(p,action-DOOR_FL);
        else return false;car.systems.sync();return true;
    }
    private boolean stationary(){return car.isParkingBraked()&&Math.abs(car.getDriveSpeed())<.001F;}
    public boolean toggleCargoLock(Player p){if(client()||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||p.level()!=car.level()||p.distanceToSqr(car)>1024||!car.equipment.authorized(p)||!stationary()||car.engineRunning()||car.equipment.hasOpeners())return false;cargoLocked=!cargoLocked;if(cargoLocked&&car.spec()!=VehicleSpec.BUS)car.systems.closeHatch();car.systems.sync();return true;}
    public boolean toggleDoor(Player p,int index){if(client()||index<0||index>=doorCount()||car.spec()==VehicleSpec.BUS||car.spec()==VehicleSpec.MOTORCYCLE||!car.systems.serviceAllowed(p))return false;doorMask^=1<<index;car.systems.refreshSideDoors();return true;}
    public void toggleAllDoors(){if(!client())doorMask=doorMask==0?(1<<doorCount())-1:0;}
    public void closeDoors(){if(!client())doorMask=0;}
    public void impact(float amount){if(client())return;radiator=Math.max(0,radiator-amount*.55F);if(amount>=20)car.hazardOn();}
    public void repairRadiator(){if(!client())radiator=Math.min(100,radiator+25);}
    public float addCoolant(Player p,float litres){if(client()||!Float.isFinite(litres)||litres<=0||!car.systems.serviceAllowed(p)||!car.systems.upgrades.hood()||temperature>=60)return 0;float n=Math.min(litres,5-coolant);coolant+=n;car.systems.sync();return n;}
    public boolean supportsBlockDrive(){var t=car.systems.trailer();return car.spec()==VehicleSpec.TRACTOR&&car.systems.attached()&&(t==null||t.variant()!=3||t.supports());}
    public float maxSpeed(){return switch(car.spec()){case MOTORCYCLE->.58F;case BUS->.34F;case PICKUP,OFFROAD->.42F;case SPORT->.72F;case TRACTOR->.32F;default->NgCityEntity.MAX_FORWARD;};}
    public float driveSpeed(float current,int throttle,boolean brake,boolean stale,float cruise){
        if(supportsBlockDrive())return 0;
        if(!manual){if(throttle<0||current<-.001F)gear=-1;else{gear=1;float[] thresholds={.105F,.19F,.29F,.39F,.51F};for(float t:thresholds)if(Math.abs(current)>t)gear++;}}
        if(stale)throttle=0;
        if(manual&&(gear==0||gear>0&&throttle<0&&current<=0||gear<0&&throttle>0&&current>=0))throttle=0;
        float ratio=gear<=1?1:gear==2?.88F:gear==3?.76F:gear==4?.65F:gear==5?.57F:.5F;
        float accel=.012F*ratio*(car.spec()==VehicleSpec.SPORT?1.65F:car.spec()==VehicleSpec.MOTORCYCLE?1.4F:car.spec()==VehicleSpec.TRACTOR?.72F:1);
        float next=brake?Mth.approach(current,0,.045F):throttle==0?Mth.approach(current,0,.006F):current*throttle<0?Mth.approach(current,0,.035F):current+throttle*accel;
        if(cruise>0&&!brake)next=Mth.approach(current,cruise,accel);
        float grip=car.systems.grip(),driveGrip=awd&&car.spec()==VehicleSpec.OFFROAD&&grip>=.45F?Math.min(1,grip+.25F):Math.max(.5F,grip);
        next=current+(next-current)*(brake?grip:driveGrip);
        float limit=maxSpeed()*(low?.45F:1)*(car.systems.motor()<25?.5F:1)*(temperature>105?.5F:1)*(car.systems.upgrades.weakest()<15?.35F:car.systems.upgrades.weakest()<40?.65F:1);
        if(manual&&gear>0)limit=Math.min(limit,new float[]{0,.15F,.25F,.36F,.48F,.61F,.75F}[gear]);
        if(gear==0&&manual&&!brake)next=Mth.approach(current,0,.004F);
        if(!car.isParkingBraked()&&car.engineRunning()&&Math.abs(current)<.05F){if(brake&&hill)holdTicks=40;else if(throttle!=0)holdTicks=0;else if(holdTicks>0){holdTicks--;next=0;}else if(car.onGround()){float slope=slope();next-=(float)(Math.signum(slope)*Math.min(.003,Math.abs(slope)*.004));}}
        else holdTicks=0;
        if(car.spec()==VehicleSpec.OFFROAD)car.setMaxUpStep(awd?1.1F:.9F);
        return Math.abs(next)<.0005F?0:Mth.clamp(next,-Math.min(car.spec()==VehicleSpec.MOTORCYCLE?.12F:NgCityEntity.MAX_REVERSE,limit),limit);
    }
    private float slope(){double a=Math.toRadians(car.getYRot());var front=car.position().add(-Math.sin(a)*.65,0,Math.cos(a)*.65);var rear=car.position().add(Math.sin(a)*.65,0,-Math.cos(a)*.65);double f=groundHeight(front),r=groundHeight(rear);return Double.isFinite(f)&&Double.isFinite(r)?(float)(f-r):0;}
    private double groundHeight(Vec3 p){for(int dy=1;dy>=-2;dy--){var at=BlockPos.containing(p.x,car.getY()+dy,p.z);if(!car.level().hasChunkAt(at))return Double.NaN;var shape=car.level().getBlockState(at).getCollisionShape(car.level(),at);if(!shape.isEmpty())return at.getY()+shape.max(Direction.Axis.Y);}return Double.NaN;}
    public void tick(double distance){
        if(client())return;
        if(supportsBlockDrive())car.stopEngine();
        if(car.engineRunning()){
            coolant=Math.max(0,coolant-(100-radiator)*.00002F);
            float target=coolant<.2F?145:90+(100-radiator)*.32F;
            temperature=Mth.approach(temperature,target,.04F+(float)Math.min(1,distance)*.01F);
            if(temperature>115)car.systems.heatDamage(.035F);
            if(temperature>=130)car.stopEngine();
            float band=gear<=1?.15F:gear==2?.25F:gear==3?.36F:gear==4?.48F:gear==5?.61F:.75F;
            rpm=gear==0?800:Math.min(6500,800+Math.abs(car.getDriveSpeed())/Math.max(.1F,band)*4400);
        }else{temperature=Mth.approach(temperature,20,.08F);rpm=0;}
    }
    public String diagnosis(){return "Karosserie "+Math.round(car.systems.body())+"% · Motor "+Math.round(car.systems.motor())+"%\nKühler "+Math.round(radiator())+"% · Kühlmittel "+String.format(java.util.Locale.ROOT,"%.1f",coolant())+" / 5 l\nTemperatur "+Math.round(temperature())+" °C · Reifen "+Math.round(car.systems.upgrades.weakest())+"%\nBatterie "+Math.round(car.systems.comfort.battery())+"%\nErsatzteile mindestens: "+repairParts()+" · Kühlmittel fehlt: "+String.format(java.util.Locale.ROOT,"%.1f",5-coolant())+" l\n"+(car.systems.upgrades.weakest()<100?"Reifen: Ersatzrad + Wagenheber + Reparaturwerkzeug":"Reifen: kein Radwechsel nötig");}
    public int repairParts(){return (int)Math.ceil(Math.max(Math.max(100-car.systems.body(),100-car.systems.motor()),100-radiator())/25);}
    public void save(CompoundTag t){t.putBoolean("AWD",awd);t.putBoolean("LowRange",low);t.putBoolean("HillAssist",hill);t.putBoolean("ManualGears",manual);t.putBoolean("TaxiSign",taxi);t.putBoolean("TaxiFree",taxiFree());t.putBoolean("CargoLocked",cargoLocked);t.putInt("Gear",gear);t.putInt("IndividualDoors",doorMask);t.putFloat("Radiator",radiator);t.putFloat("Coolant",coolant);t.putFloat("Temperature",temperature);t.putFloat("RPM",rpm);t.putInt("HillTicks",holdTicks);}
    public void load(CompoundTag t){awd=car.spec()==VehicleSpec.OFFROAD&&t.getBoolean("AWD");low=awd&&t.getBoolean("LowRange");hill=!t.contains("HillAssist")||t.getBoolean("HillAssist");manual=t.getBoolean("ManualGears");taxi=!t.contains("TaxiSign")||t.getBoolean("TaxiSign");cargoLocked=t.getBoolean("CargoLocked");gear=t.contains("Gear",99)?Mth.clamp(t.getInt("Gear"),-1,6):1;doorMask=t.contains("IndividualDoors",99)?Mth.clamp(t.getInt("IndividualDoors"),0,(1<<doorCount())-1):(t.getInt("Doors")&1)!=0?(1<<doorCount())-1:0;radiator=number(t,"Radiator",100,0,100);coolant=number(t,"Coolant",5,0,5);temperature=number(t,"Temperature",20,20,150);rpm=0;holdTicks=0;}
    private static float number(CompoundTag t,String key,float fallback,float lo,float hi){float n=t.contains(key,99)?t.getFloat(key):fallback;return Float.isFinite(n)?Mth.clamp(n,lo,hi):fallback;}
}
