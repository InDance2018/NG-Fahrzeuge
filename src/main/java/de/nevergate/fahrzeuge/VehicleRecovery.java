package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

public final class VehicleRecovery {
    public static NgCityEntity find(NgCityEntity car,UUID id){return id!=null&&car.level() instanceof ServerLevel l&&l.getEntity(id) instanceof NgCityEntity c?c:null;}
    public static boolean related(NgCityEntity a,NgCityEntity b){return b.getUUID().equals(a.systems.comfort.parent())||b.getUUID().equals(a.systems.comfort.target())||a.getUUID().equals(b.systems.comfort.parent())||a.getUUID().equals(b.systems.comfort.target());}
    public static boolean connect(NgCityEntity parent,NgCityEntity child,Player p,int mode){
        if((mode!=1&&mode!=2&&mode!=4)||mode==4&&(!parent.systems.comfort.ramp()||parent.systems.comfort.battery()<1)||parent==child||parent.level()!=child.level()||parent.level().isClientSide||parent.distanceToSqr(child)>64
                ||!parent.systems.serviceAllowed(p)||!child.systems.serviceAllowed(p)||child.isVehicle()
                ||parent.systems.comfort.connected()||child.systems.comfort.connected()||parent.systems.attached()||child.systems.attached()
                ||parent.equipment.hasOpeners()||child.equipment.hasOpeners()||(mode==2||mode==4)&&parent.spec()!=VehicleSpec.TOW_TRUCK)return false;
        parent.systems.comfort.target(child.getUUID());child.systems.comfort.parent(parent.getUUID(),mode);return true;
    }
    public static boolean detach(NgCityEntity parent,Player p){
        if(!parent.systems.serviceAllowed(p))return false;
        var child=find(parent,parent.systems.comfort.target());if(child==null)return false;
        double angle=Math.toRadians(parent.getYRot()),offset=parent.spec().length/2+child.spec().length/2+1;
        Vec3 at=parent.position().add(Math.sin(angle)*offset,0,-Math.cos(angle)*offset);
        var box=child.getBoundingBox().move(at.subtract(child.position()));
        if(!parent.level().hasChunkAt(net.minecraft.core.BlockPos.containing(at))||!parent.level().getWorldBorder().isWithinBounds(box)||!parent.level().noCollision(child,box))return false;
        child.setPos(at);child.systems.comfort.parent(null,0);parent.systems.comfort.target(null);return true;
    }
    public static boolean useTool(NgCityEntity car,Player p,ItemStack tool){
        if(!p.getAbilities().mayBuild||p.isSpectator()||p.distanceToSqr(car)>64||!car.equipment.authorized(p))return false;
        if(tool.is(VehicleContent.REMOTE.get())){
            if(car.spec()!=VehicleSpec.TOW_TRUCK||!car.systems.serviceAllowed(p))return false;
            if(p.isShiftKeyDown()){car.systems.comfort.toggleRamp();return true;}
            if(car.systems.comfort.target()!=null)return detach(car,p);
            if(!car.systems.comfort.ramp())return false;
            var list=car.level().getEntitiesOfClass(NgCityEntity.class,car.getBoundingBox().inflate(8),c->c!=car&&c.equipment.authorized(p)&&!c.systems.comfort.connected());
            list.sort(java.util.Comparator.comparingDouble(car::distanceToSqr));return !list.isEmpty()&&connect(car,list.get(0),p,4);
        }
        if(tool.is(VehicleContent.ROPE.get())&&p.isShiftKeyDown()){
            NgCityEntity parent=car.systems.comfort.parent()!=null?find(car,car.systems.comfort.parent()):car;
            if(parent!=null&&detach(parent,p)){tool.removeTagKey("Selected");return true;}
            tool.removeTagKey("Selected");return true;
        }
        var tag=tool.getOrCreateTag();
        if(!tag.hasUUID("Selected")){tag.putUUID("Selected",car.getUUID());p.displayClientMessage(Component.translatable("message.ng_fahrzeuge.selected"),true);return true;}
        NgCityEntity selected=find(car,tag.getUUID("Selected"));tag.remove("Selected");if(selected==null)return false;
        boolean ok=tool.is(VehicleContent.JUMPER.get())?car.systems.comfort.jumpFrom(selected,p):connect(selected,car,p,1);
        p.displayClientMessage(Component.translatable(ok?"message.ng_fahrzeuge.connection_ok":"message.ng_fahrzeuge.connection_failed"),true);return ok;
    }
    public static boolean tick(NgCityEntity car){
        var state=car.systems.comfort;if(state.parent()==null)return false;
        car.stopEngine();
        if(state.mode()==3){var pos=state.lift();if(pos!=null&&car.level().hasChunkAt(pos)&&(!(car.level().getBlockEntity(pos) instanceof VehicleLiftEntity lift)||lift.car()!=car)){state.parent(null,0);return false;}car.setDeltaMovement(Vec3.ZERO);return true;}
        var parent=find(car,state.parent());
        if(parent==null)return true; // Keep the saved connection without loading chunks.
        if(!car.getUUID().equals(parent.systems.comfort.target())){state.parent(null,0);return false;}
        if(car.isVehicle())car.ejectPassengers();
        double angle=Math.toRadians(parent.getYRot());
        double distance=(state.mode()==2||state.mode()==4)?1.2:parent.spec().length/2+car.spec().length/2+1;
        Vec3 target=parent.position().add(Math.sin(angle)*distance,(state.mode()==2||state.mode()==4)?1.1:0,-Math.cos(angle)*distance);
        if(car.distanceToSqr(parent)>400){state.parent(null,0);parent.systems.comfort.target(null);return false;}
        Vec3 delta=target.subtract(car.position());
        if(state.mode()==4){
            if(!parent.isParkingBraked()||parent.engineRunning()||!parent.systems.comfort.ramp()||!parent.systems.comfort.consume(.002F)){parent.stopEngine();return true;}
            if(delta.length()<.08){state.parent(parent.getUUID(),2);return true;}
            Vec3 movement=delta.scale(Math.min(1,.12/Math.max(.001,delta.length())));
            if(!car.level().getBlockCollisions(car,car.getBoundingBox().move(movement)).iterator().hasNext()){car.setPos(car.position().add(movement));car.setYRot(parent.getYRot());}
        }else if(state.mode()==2){
            var box=car.getBoundingBox().move(delta);boolean obstructed=car.level().getBlockCollisions(car,box).iterator().hasNext();
            if(obstructed){parent.stopEngine();return true;}car.setPos(target);car.setYRot(parent.getYRot());
        }else if(delta.length()>.03){
            car.move(MoverType.SELF,delta.scale(Math.min(1,.6/Math.max(.001,delta.length()))));car.setYRot(parent.getYRot());
            if(car.horizontalCollision)parent.stopEngine();
        }
        car.setDeltaMovement(Vec3.ZERO);if(car.tickCount%5==0)car.systems.sync();return true;
    }
    private VehicleRecovery(){}
}
