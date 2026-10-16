package de.nevergate.fahrzeuge;

import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Dev11 state; only this server instance may change seats, lamps or route information. */
public final class VehicleExpansion {
    public static final int STOP=19,BELT=20,HIGH=21,STAND=22,FOG=23;
    private final NgCityEntity car;
    private String route="",destination="";
    private boolean stop,stand,fog,turned;
    private int lightMode,seatForward,seatHeight;
    private float signalYaw;
    private final Set<UUID> belts=new HashSet<>();
    VehicleExpansion(NgCityEntity car){this.car=car;}
    private boolean client(){return car.level().isClientSide;}
    private CompoundTag tag(){return car.systemTag();}
    public String route(){return client()?tag().getString("Route"):route;}
    public String destination(){return client()?tag().getString("Destination"):destination;}
    public boolean stopRequested(){return client()?tag().getBoolean("StopRequested"):stop;}
    public boolean stand(){return car.spec()==VehicleSpec.MOTORCYCLE&&(client()?tag().getBoolean("SideStand"):stand);}
    public boolean fog(){return client()?tag().getBoolean("Fog"):fog;}
    public int lightMode(){return client()?tag().getInt("LightMode"):lightMode;}
    public int seatForward(){return client()?tag().getInt("SeatForward"):seatForward;}
    public int seatHeight(){return client()?tag().getInt("SeatHeight"):seatHeight;}
    public Vec3 seatOffsetWorld(){double a=Math.toRadians(car.getYRot()),z=seatForward()*.04;return new Vec3(-Math.sin(a)*z,seatHeight()*.025,Math.cos(a)*z);}
    public int beltMask(){if(client())return tag().getInt("BeltMask");int n=0;for(int i=0;i<car.getPassengers().size();i++)if(belts.contains(car.getPassengers().get(i).getUUID()))n|=1<<i;return n;}
    public boolean belted(Player p){int seat=car.getPassengers().indexOf(p);return seat>=0&&(beltMask()&(1<<seat))!=0;}
    public void reconcileBelts(){belts.removeIf(id->car.getPassengers().stream().noneMatch(p->p.getUUID().equals(id)));}
    public boolean action(Player p,int action){
        if(client()||car.isRemoved()||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||!car.hasPassenger(p))return false;
        if(action==BELT){if(car.spec()==VehicleSpec.MOTORCYCLE)return false;if(!belts.add(p.getUUID()))belts.remove(p.getUUID());car.systems.sync();return true;}
        if(action==STOP){if(car.spec()!=VehicleSpec.BUS||car.getDriver()==p||stop)return false;stop=true;car.systems.sync();return true;}
        if(car.getDriver()!=p)return false;
        if(action==HIGH){if(lightMode<2)return false;lightMode=lightMode==3?2:3;}
        else if(action==FOG)fog=!fog;
        else if(action==STAND){if(car.spec()!=VehicleSpec.MOTORCYCLE||!car.isParkingBraked()||car.engineRunning()||Math.abs(car.getDriveSpeed())>.001F)return false;stand=!stand;}
        else return false;
        car.systems.sync();return true;
    }
    public boolean setRoute(Player p,String line,String target){
        if(client()||car.spec()!=VehicleSpec.BUS||!car.systems.serviceAllowed(p)||!car.equipment.isOwner(p)||line.length()>8||target.length()>32||!safe(line)||!safe(target))return false;
        route=line.strip();destination=target.strip();car.systems.sync();return true;
    }
    private static boolean safe(String s){return s.codePoints().noneMatch(c->Character.isISOControl(c)||c==0xA7);}
    public boolean setSeat(Player p,int forward,int height){
        if(client()||!car.systems.serviceAllowed(p)||!car.equipment.isOwner(p)||forward< -5||forward>5||height< -4||height>4)return false;
        seatForward=forward;seatHeight=height;car.systems.sync();return true;
    }
    public void clearStop(){stop=false;}
    public void raiseStand(){if(!client()){stand=false;car.systems.sync();}}
    public void cycleLights(){if(client())return;lightMode=lightMode>=2?0:lightMode+1;car.systems.sync();}
    public void signalStarted(){signalYaw=car.getYRot();turned=false;}
    public void tick(){
        if(client())return;
        if(car.signal()==1||car.signal()==2){float change=Mth.wrapDegrees(car.getYRot()-signalYaw);if((car.signal()==1?-change:change)>18)turned=true;if(turned&&car.steeringInput()==0&&Math.abs(car.getDriveSpeed())>.02F)car.clearSignal();}
        if(car.spec()==VehicleSpec.MOTORCYCLE&&car.getPassengers().isEmpty()&&car.isParkingBraked()&&!car.engineRunning())stand=true;
    }
    public int cargoCrates(){return client()?tag().getInt("CargoCrates"):crates(car.equipment.cargo);}
    public int roofCrates(){return client()?tag().getInt("RoofCrates"):crates(car.systems.upgrades.roof);}
    private static int crates(net.minecraft.world.Container c){int used=0;for(int i=0;i<c.getContainerSize();i++)if(!c.getItem(i).isEmpty())used++;return used==0?0:Math.min(6,(used+4)/5);}
    public void snapshot(CompoundTag t){t.putInt("BeltMask",beltMask());t.putInt("CargoCrates",cargoCrates());t.putInt("RoofCrates",roofCrates());}
    public void save(CompoundTag t){t.putString("Route",route);t.putString("Destination",destination);t.putBoolean("StopRequested",stop);t.putBoolean("SideStand",stand);t.putInt("LightMode",lightMode);t.putBoolean("Fog",fog);t.putInt("SeatForward",seatForward);t.putInt("SeatHeight",seatHeight);}
    public void load(CompoundTag t){route=t.getString("Route");destination=t.getString("Destination");if(route.length()>8||!safe(route))route="";if(destination.length()>32||!safe(destination))destination="";stop=t.getBoolean("StopRequested");stand=car.spec()==VehicleSpec.MOTORCYCLE&&t.getBoolean("SideStand");lightMode=t.contains("LightMode",99)?Mth.clamp(t.getInt("LightMode"),0,3):car.lightsOn()?2:0;fog=t.getBoolean("Fog");seatForward=Mth.clamp(t.getInt("SeatForward"),-5,5);seatHeight=Mth.clamp(t.getInt("SeatHeight"),-4,4);belts.clear();signalStarted();}
}
