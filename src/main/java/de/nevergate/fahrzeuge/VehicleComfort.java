package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/** Persistent comfort and recovery state; clients receive it with the regular system snapshot. */
public final class VehicleComfort {
    private final NgCityEntity car;
    private float battery=100;
    private boolean wipers,windows,cabin,ramp;
    private String plate="";
    private int variant,mode;
    private UUID parent,target;
    private net.minecraft.core.BlockPos lift;
    public net.minecraft.core.BlockPos lift(){return lift;}
    public void lift(net.minecraft.core.BlockPos pos){lift=pos==null?null:pos.immutable();}
    VehicleComfort(NgCityEntity car){this.car=car;}
    private CompoundTag client(){return car.systemTag();}
    public float battery(){return car.level().isClientSide?client().getFloat("Battery"):battery;}
    public boolean wipers(){return car.level().isClientSide?client().getBoolean("Wipers"):wipers;}
    public boolean windows(){return car.level().isClientSide?client().getBoolean("Windows"):windows;}
    public boolean cabin(){return (car.level().isClientSide?client().getBoolean("Cabin"):cabin)||car.systems.doors()!=0;}
    public boolean ramp(){return car.level().isClientSide?client().getBoolean("Ramp"):ramp;}
    public String plate(){return car.level().isClientSide?client().getString("Plate"):plate;}
    public int variant(){return car.level().isClientSide?client().getInt("Variant"):variant;}
    public UUID parent(){return car.level().isClientSide?(client().hasUUID("RecoveryParent")?client().getUUID("RecoveryParent"):null):parent;}
    public UUID target(){return target;}
    public int mode(){return mode;}
    public boolean restrained(){return car.level().isClientSide?client().getBoolean("Restrained"):parent!=null;}
    public boolean connected(){return parent!=null||target!=null;}
    public boolean setPlate(String value){
        if(car.level().isClientSide||!value.matches("[A-Za-z0-9 -]{0,12}"))return false;
        plate=value.strip().toUpperCase(java.util.Locale.ROOT);car.systems.sync();return true;
    }
    public void setVariant(int value){variant=Math.max(0,Math.min(1,value));car.systems.sync();}
    public boolean action(Player p,int action){
        if(car.level().isClientSide||!car.equipment.authorized(p)||!p.getAbilities().mayBuild||p.isSpectator()||p.distanceToSqr(car)>64)return false;
        if(action==VehicleNetwork.WIPER_ACTION)wipers=!wipers;
        else if(action==VehicleNetwork.WINDOW_ACTION){if(battery<.1F)return false;windows=!windows;battery=Math.max(0,battery-.1F);}
        else if(action==VehicleNetwork.CABIN_ACTION)cabin=!cabin;
        else return false;
        car.systems.sync();return true;
    }
    public boolean consume(float amount){if(car.level().isClientSide||!Float.isFinite(amount)||amount<0||battery<amount)return false;battery-=amount;return true;}
    public void startCost(){battery=Math.max(0,battery-2);car.systems.sync();}
    public float charge(float amount){if(car.level().isClientSide||!Float.isFinite(amount)||amount<=0)return 0;float added=Math.min(amount,100-battery);battery+=added;car.systems.sync();return added;}
    public boolean jumpFrom(NgCityEntity donor,Player p){
        if(car.level().isClientSide||donor==car||donor.level()!=car.level()||car.distanceToSqr(donor)>64||!car.systems.serviceAllowed(p)
                ||!donor.equipment.authorized(p)||!donor.isParkingBraked()||donor.systems.comfort.battery()<20)return false;
        if(battery>=20)return false;
        donor.systems.comfort.battery-=10;battery=Math.min(100,battery+10);car.systems.sync();donor.systems.sync();return true;
    }
    public void tick(){
        if(car.engineRunning())battery=Math.min(100,battery+.015F);
        else battery=Math.max(0,battery-(car.lightsOn()?.0015F:0)-(cabin()?.0005F:0)-(wipers?.0007F:0)-(car.systems.upgrades.beacon()?.001F:0)-(car.systems.expansion.fog()?.001F:0)-(car.systems.expansion.lightMode()==3?.001F:0));
        if(battery==0)wipers=false;
    }
    public void parent(UUID id,int mode){parent=id;if(id==null)lift=null;this.mode=id==null?0:mode;car.systems.sync();}
    public void target(UUID id){target=id;car.systems.sync();}
    public void toggleRamp(){ramp=!ramp;car.systems.sync();}
    public void save(CompoundTag tag){
        tag.putFloat("Battery",battery);tag.putBoolean("Wipers",wipers);tag.putBoolean("Windows",windows);tag.putBoolean("Cabin",cabin);tag.putBoolean("Ramp",ramp);
        tag.putString("Plate",plate);tag.putInt("Variant",variant);tag.putBoolean("Restrained",parent!=null);tag.putInt("RecoveryMode",mode);
        if(lift!=null)tag.putLong("LiftPos",lift.asLong());
        if(parent!=null)tag.putUUID("RecoveryParent",parent);if(target!=null)tag.putUUID("RecoveryTarget",target);
    }
    public void load(CompoundTag tag){
        battery=tag.contains("Battery",99)&&Float.isFinite(tag.getFloat("Battery"))?Math.max(0,Math.min(100,tag.getFloat("Battery"))):100;
        wipers=tag.getBoolean("Wipers");windows=tag.getBoolean("Windows");cabin=tag.getBoolean("Cabin");ramp=tag.getBoolean("Ramp");
        plate=tag.getString("Plate").matches("[A-Za-z0-9 -]{0,12}")?tag.getString("Plate"):"";variant=Math.max(0,Math.min(1,tag.getInt("Variant")));
        lift=tag.contains("LiftPos",4)?net.minecraft.core.BlockPos.of(tag.getLong("LiftPos")):null;
        parent=tag.hasUUID("RecoveryParent")?tag.getUUID("RecoveryParent"):null;target=tag.hasUUID("RecoveryTarget")?tag.getUUID("RecoveryTarget"):null;mode=tag.getInt("RecoveryMode");
    }
}
