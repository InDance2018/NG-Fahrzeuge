package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

/** Authoritative vehicle state. Distances are actual travelled metres, never client reports. */
public final class VehicleSystems {
    private final NgCityEntity car;
    public final VehicleComfort comfort;
    public final VehicleUpgrades upgrades;
    public final VehicleExpansion expansion;
    public final VehicleMechanics mechanics;
    private double metres, trip;
    private float fuel, body=100, motor=100, cruise;
    private int doors, impactCooldown;
    private UUID trailer;
    VehicleSystems(NgCityEntity car) { this.car=car;comfort=new VehicleComfort(car);upgrades=new VehicleUpgrades(car);expansion=new VehicleExpansion(car);mechanics=new VehicleMechanics(car);fuel=tankCapacity(); }
    public float tankCapacity(){return switch(car.spec()){case CITY->40;case FAMILY,TAXI->55;case OFFROAD->85;case SPORT->60;case TRACTOR->240;case TRANSPORTER,TOW_TRUCK,PICKUP->80;case BUS->180;case MOTORCYCLE->18;};}
    public float consumption(){return switch(car.spec()){case CITY->6.5F;case FAMILY,TAXI->8;case OFFROAD->14;case SPORT->15;case TRACTOR->32;case TRANSPORTER->12;case TOW_TRUCK->16;case BUS->28;case MOTORCYCLE->4;case PICKUP->13;};}
    private CompoundTag client(){return car.systemTag();}
    public float fuel(){return car.level().isClientSide?client().getFloat("Fuel"):fuel;}
    public float body(){return car.level().isClientSide?client().getFloat("Body"):body;}
    public float motor(){return car.level().isClientSide?client().getFloat("Motor"):motor;}
    public double metres(){return car.level().isClientSide?client().getDouble("Metres"):metres;}
    public double trip(){return car.level().isClientSide?client().getDouble("Trip"):trip;}
    public float cruise(){return car.level().isClientSide?client().getFloat("Cruise"):cruise;}
    public int doors(){return car.level().isClientSide?client().getInt("Doors"):doors;}
    public UUID trailerId(){return trailer;}
    public boolean winching(){var target=VehicleRecovery.find(car,comfort.target());return target!=null&&target.systems.comfort.mode()==4;}
    public boolean canRun(){return mechanics.temperature()<130&&!mechanics.supportsBlockDrive()&&!expansion.stand()&&!winching()&& fuel()>0&&motor()>5&&body()>0&&(car.engineRunning()||comfort.battery()>=5)&&!comfort.restrained()&&!upgrades.jacked()&&!upgrades.hood();}
    public float refuel(float litres){
        if(car.level().isClientSide||!Float.isFinite(litres)||litres<=0)return 0;
        float used=Math.min(litres,tankCapacity()-fuel);fuel+=used;sync();return used;
    }
    public void damage(float amount){
        if(car.level().isClientSide||!Float.isFinite(amount)||amount<=0)return;
        mechanics.impact(amount);upgrades.impact(amount*.7F);body=Math.max(0,body-amount);motor=Math.max(0,motor-amount*.45F);cruise=0;
        if(!canRun())car.stopEngine();sync();
    }
    public void heatDamage(float amount){if(!car.level().isClientSide&&Float.isFinite(amount)&&amount>0){motor=Math.max(0,motor-amount);if(motor<=5)car.stopEngine();}}
    public boolean repair(Player p,net.minecraft.world.item.ItemStack tool){
        if(!serviceAllowed(p)||body>=100&&motor>=100&&mechanics.radiator()>=100)return false;
        int slot=-1;for(int i=0;i<p.getInventory().getContainerSize();i++)if(p.getInventory().getItem(i).is(VehicleContent.PARTS.get())){slot=i;break;}
        if(!p.getAbilities().instabuild&&slot<0)return false;
        if(!p.getAbilities().instabuild){p.getInventory().getItem(slot).shrink(1);tool.hurtAndBreak(1,p,who->who.broadcastBreakEvent(net.minecraft.world.InteractionHand.MAIN_HAND));}
        body=Math.min(100,body+25);motor=Math.min(100,motor+25);mechanics.repairRadiator();sync();return true;
    }
    public boolean serviceAllowed(Player p){return !car.isRemoved()&&!p.isSpectator()&&p.getAbilities().mayBuild
            &&p.distanceToSqr(car)<=64&&car.equipment.authorized(p)&&car.isParkingBraked()&&!car.engineRunning()&&Math.abs(car.getDriveSpeed())<.001F;}
    public boolean action(Player p,int action){
        if(action>=24)return mechanics.action(p,action);
        if(action>=19)return expansion.action(p,action);
        if(action>=VehicleNetwork.HOOD_ACTION&&action<=VehicleNetwork.ROOF_ACTION)return upgrades.action(p,action);
        if(action>=VehicleNetwork.WIPER_ACTION&&action<=VehicleNetwork.CABIN_ACTION)return comfort.action(p,action);
        if(action==VehicleNetwork.CRUISE_ACTION){
            if(cruise>0){cruise=0;sync();return true;}
            if(!car.engineRunning()||car.isParkingBraked()||car.getDriveSpeed()<.03F)return false;
            cruise=car.getDriveSpeed();sync();return true;
        }
        if(car.spec()==VehicleSpec.BUS&&(action==VehicleNetwork.DOOR_ACTION||action==VehicleNetwork.HATCH_ACTION)){if(car.isRemoved()||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||!car.equipment.authorized(p)||p.distanceToSqr(car)>64||!car.isParkingBraked()||Math.abs(car.getDriveSpeed())>.001F)return false;int bit=action==VehicleNetwork.DOOR_ACTION?1:2;doors^=bit;if((doors&bit)!=0)expansion.clearStop();sync();return true;}
        if(!serviceAllowed(p))return false;
        if(car.spec()==VehicleSpec.MOTORCYCLE&&(action==VehicleNetwork.DOOR_ACTION||action==VehicleNetwork.HATCH_ACTION))return false;
        if(action==VehicleNetwork.DOOR_ACTION) {mechanics.toggleAllDoors();refreshSideDoors();}
        else if(action==VehicleNetwork.HATCH_ACTION){if(mechanics.cargoLocked())return false;doors^=2;}
        else if(action==VehicleNetwork.TRIP_ACTION)trip=0;
        else if(action==VehicleNetwork.COUPLE_ACTION)return couple(p);
        else return false;
        sync();return true;
    }
    public void refreshSideDoors(){doors=(doors&2)|(mechanics.doorMask()!=0?1:0);sync();}
    public void closeDoors(){doors=0;mechanics.closeDoors();sync();}
    public void closeHatch(){doors&=1;sync();}
    public void openHatch(){doors|=2;sync();}
    public void cancelCruise(){if(cruise!=0){cruise=0;sync();}}
    public float grip(){
        var state=car.level().getBlockState(net.minecraft.core.BlockPos.containing(car.getX(),car.getY()-.05,car.getZ()));
        if(state.is(Blocks.ICE)||state.is(Blocks.PACKED_ICE)||state.is(Blocks.BLUE_ICE)||state.is(Blocks.FROSTED_ICE))return .22F;
        if(state.is(Blocks.SNOW)||state.is(Blocks.SNOW_BLOCK)||state.is(Blocks.POWDER_SNOW))return .45F;
        if(state.is(Blocks.DIRT)||state.is(Blocks.COARSE_DIRT)||state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.GRAVEL)||state.is(Blocks.SAND)||state.is(Blocks.DIRT_PATH))return .7F;
        return 1;
    }
    public float speed(float current,int throttle,boolean brake,boolean stale){
        if(stale||brake||throttle<0||!car.engineRunning()||car.isParkingBraked())cancelCruise();
        return mechanics.driveSpeed(current,cruise>0&&throttle==0?1:throttle,brake,stale,cruise);
    }

    public void travelled(double distance,float impact){
        comfort.tick();upgrades.travelled(distance);expansion.tick();mechanics.tick(distance);
        if(impactCooldown>0)impactCooldown--;
        if(distance>=0&&distance<2){metres+=distance;trip+=distance;}
        if(car.engineRunning())fuel=Math.max(0,fuel-(float)(Math.max(0,Math.min(2,distance))*consumption()/100000D)-.0001F);
        if(fuel<=0)car.stopEngine();
        if(impact>.12F&&impactCooldown==0){damage(impact*65);impactCooldown=20;}
        if(car.tickCount%5==0)sync();
    }
    public TrailerEntity trailer(){
        return trailer==null||!(car.level() instanceof net.minecraft.server.level.ServerLevel level)?null:
                level.getEntity(trailer) instanceof TrailerEntity t&&!t.isRemoved()?t:null;
    }
    public boolean couple(Player p){
        if(car.level().isClientSide||!serviceAllowed(p)||car.equipment.hasOpeners()||car.systems.comfort.connected()||(car.spec()==VehicleSpec.CITY||car.spec()==VehicleSpec.MOTORCYCLE||car.spec()==VehicleSpec.BUS))return false;
        TrailerEntity current=trailer();
        if(current!=null){if(current.hasOpeners())return false;if(current.variant()==3&&!current.supports())return false;current.detach();trailer=null;sync();return true;}
        // An unloaded saved trailer remains reserved; never silently attach a second one.
        if(trailer!=null)return false;
        double yaw=Math.toRadians(car.getYRot());
        var hitch=car.position().add(Math.sin(yaw)*(car.spec()==VehicleSpec.TRACTOR?1.05:car.spec().length/2+1),0,-Math.cos(yaw)*(car.spec()==VehicleSpec.TRACTOR?1.05:car.spec().length/2+1));
        var candidates=car.level().getEntitiesOfClass(TrailerEntity.class,car.getBoundingBox().inflate(6),
                t->(car.spec()==VehicleSpec.TRACTOR?t.variant()==3:t.variant()!=3)&&t.towId()==null&&t.authorized(p)&&(car.spec()==VehicleSpec.TRACTOR?t.supports()&&t.position().add(-Math.sin(Math.toRadians(t.getYRot()))*3.1,0,Math.cos(Math.toRadians(t.getYRot()))*3.1).distanceToSqr(hitch)<4:t.position().distanceToSqr(hitch)<16)&&!t.hasOpeners());
        candidates.sort(java.util.Comparator.comparingDouble(t->t.distanceToSqr(car)));
        if(candidates.isEmpty())return false;
        TrailerEntity t=candidates.get(0);trailer=t.getUUID();t.attach(car,p);sync();return true;
    }
    public void forgetTrailer(UUID id){if(id.equals(trailer)){trailer=null;sync();}}
    public boolean attached(){return car.level().isClientSide?client().getBoolean("Attached"):trailer!=null;}
    public void save(CompoundTag tag){
        comfort.save(tag);upgrades.save(tag);expansion.save(tag);mechanics.save(tag);
        tag.putFloat("Fuel",fuel);tag.putFloat("Body",body);tag.putFloat("Motor",motor);
        tag.putDouble("Metres",metres);tag.putDouble("Trip",trip);tag.putInt("Doors",doors);
        if(trailer!=null)tag.putUUID("Trailer",trailer);
    }
    public void load(CompoundTag tag){
        comfort.load(tag);upgrades.load(tag);expansion.load(tag);mechanics.load(tag);
        fuel=number(tag,"Fuel",tankCapacity(),0,tankCapacity());body=number(tag,"Body",100,0,100);motor=number(tag,"Motor",100,0,100);
        metres=finiteDistance(tag.getDouble("Metres"));trip=Math.min(metres,finiteDistance(tag.getDouble("Trip")));
        doors=Mth.clamp(tag.getInt("Doors"),0,3);trailer=tag.hasUUID("Trailer")?tag.getUUID("Trailer"):null;cruise=0;sync();
    }
    private static float number(CompoundTag tag,String key,float fallback,float min,float max){
        float value=tag.contains(key,99)?tag.getFloat(key):fallback;return Float.isFinite(value)?Mth.clamp(value,min,max):fallback;
    }
    private static double finiteDistance(double n){return Double.isFinite(n)&&n>0?Math.min(n,1e12):0;}
    public void sync(){if(!car.level().isClientSide){CompoundTag tag=new CompoundTag();save(tag);tag.remove("RoofCargo");expansion.snapshot(tag);tag.putFloat("Cruise",cruise);tag.putBoolean("Attached",trailer!=null);car.syncSystems(tag);}}
}
