package de.nevergate.fahrzeuge;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Parkable inventory trailer; coupling UUIDs survive chunk unload without loading chunks. */
public final class TrailerEntity extends Entity {
    private static final EntityDataAccessor<Optional<UUID>> TOW=SynchedEntityData.defineId(TrailerEntity.class,EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> LIGHT=SynchedEntityData.defineId(TrailerEntity.class,EntityDataSerializers.BOOLEAN),
            BRAKE=SynchedEntityData.defineId(TrailerEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SIGNAL=SynchedEntityData.defineId(TrailerEntity.class,EntityDataSerializers.INT);
    private UUID owner;
    private int openers,lerpSteps;
    private double tx,ty,tz;private float yaw;
    public final SimpleContainer cargo=new SimpleContainer(27){
        @Override public void startOpen(Player p){openers++;}
        @Override public void stopOpen(Player p){openers=Math.max(0,openers-1);}
        @Override public boolean stillValid(Player p){return canOpen(p);}
    };
    public TrailerEntity(EntityType<? extends TrailerEntity> type,Level level){super(type,level);setMaxUpStep(.6F);}
    @Override protected void defineSynchedData(){entityData.define(TOW,Optional.empty());entityData.define(LIGHT,false);entityData.define(BRAKE,false);entityData.define(SIGNAL,0);}
    public UUID towId(){return entityData.get(TOW).orElse(null);}
    public boolean lights(){return entityData.get(LIGHT);}
    public boolean braking(){return entityData.get(BRAKE);}
    public int signal(){return entityData.get(SIGNAL);}
    public boolean hasOpeners(){return openers>0;}
    public void claim(Player p){if(owner==null&&!level().isClientSide)owner=p.getUUID();}
    public NgCityEntity tow(){return level() instanceof net.minecraft.server.level.ServerLevel l&&towId()!=null&&l.getEntity(towId()) instanceof NgCityEntity car?car:null;}
    public boolean authorized(Player p){NgCityEntity car=tow();return owner==null||owner.equals(p.getUUID())||car!=null&&car.equipment.authorized(p);}
    public void attach(NgCityEntity car,Player p){if(level().isClientSide)return;claim(p);entityData.set(TOW,Optional.of(car.getUUID()));}
    public void detach(){
        if(level().isClientSide)return;NgCityEntity car=tow();
        entityData.set(TOW,Optional.empty());entityData.set(LIGHT,false);entityData.set(BRAKE,false);entityData.set(SIGNAL,0);
        if(car!=null)car.systems.forgetTrailer(getUUID());
    }
    public boolean canOpen(Player p){
        NgCityEntity car=tow();return !isRemoved()&&p.isAlive()&&!p.isSpectator()&&p.getAbilities().mayBuild&&p.distanceToSqr(this)<=64
                &&authorized(p)&&(towId()==null||car!=null&&car.isParkingBraked()&&!car.engineRunning());
    }
    @Override protected net.minecraft.world.phys.AABB makeBoundingBox(){
        double a=Math.toRadians(getYRot()),x=(Math.abs(Math.cos(a))*1.6+Math.abs(Math.sin(a))*2.3)/2,z=(Math.abs(Math.sin(a))*1.6+Math.abs(Math.cos(a))*2.3)/2;
        return new net.minecraft.world.phys.AABB(getX()-x,getY(),getZ()-z,getX()+x,getY()+1.2,getZ()+z);
    }
    @Override public void setYRot(float yaw){super.setYRot(yaw);setBoundingBox(makeBoundingBox());}
    @Override public boolean canBeCollidedWith(){return !isRemoved();}
    @Override public boolean canCollideWith(Entity other){return !(other instanceof NgCityEntity car&&getUUID().equals(car.systems.trailerId()))&&other.canBeCollidedWith();}
    @Override public boolean isPickable(){return !isRemoved();}
    @Override public void tick(){
        super.tick();
        if(level().isClientSide){if(lerpSteps>0){setPos(getX()+(tx-getX())/lerpSteps,getY()+(ty-getY())/lerpSteps,getZ()+(tz-getZ())/lerpSteps);setYRot(getYRot()+net.minecraft.util.Mth.wrapDegrees(yaw-getYRot())/lerpSteps);lerpSteps--;}return;}
        NgCityEntity car=tow();
        if(car!=null){
            if(!getUUID().equals(car.systems.trailerId())||distanceToSqr(car)>144){detach();return;}
            double angle=Math.toRadians(car.getYRot()),distance=car.spec().length/2+1.65;
            Vec3 target=car.position().add(Math.sin(angle)*distance,0,-Math.cos(angle)*distance);
            Vec3 delta=target.subtract(position());
            if(delta.horizontalDistance()>.03){
                setYRot((float)Math.toDegrees(Math.atan2(-delta.x,delta.z)));
                Vec3 movement=delta.scale(Math.min(1,.55/Math.max(.001,delta.length())));
                move(MoverType.SELF,movement);
                if(horizontalCollision){car.stopEngine();car.systems.cancelCruise();}
            }
            // Align the parked body to the towing vehicle after the hitch reaches its target.
            if(delta.horizontalDistance()<.1)setYRot(car.getYRot());
            entityData.set(LIGHT,car.lightsOn());entityData.set(BRAKE,car.isBraking());entityData.set(SIGNAL,car.signal());
        }else if(towId()==null&&!isNoGravity()){
            double y=getDeltaMovement().y-.08;setDeltaMovement(0,y,0);move(MoverType.SELF,getDeltaMovement());setDeltaMovement(0,onGround()?0:y*.98,0);
        }
    }
    @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int steps){tx=x;ty=y;tz=z;this.yaw=yaw;lerpSteps=Math.max(1,steps);}
    @Override public ItemStack getPickResult(){var stack=new ItemStack(VehicleContent.TRAILER_ITEM.get());saveEquipment(stack.getOrCreateTag());return stack;}
    public void saveEquipment(CompoundTag tag){
        if(owner!=null)tag.putUUID("Owner",owner);
        var items=new net.minecraft.nbt.ListTag();for(int i=0;i<27;i++)if(!cargo.getItem(i).isEmpty()){
            var item=cargo.getItem(i).save(new CompoundTag());item.putInt("Slot",i);items.add(item);
        }tag.put("Cargo",items);
    }
    public void loadEquipment(CompoundTag tag){
        owner=tag.hasUUID("Owner")?tag.getUUID("Owner"):null;cargo.clearContent();
        var items=tag.getList("Cargo",10);for(int i=0;i<items.size();i++){var item=items.getCompound(i);int slot=item.getInt("Slot");if(slot>=0&&slot<27)cargo.setItem(slot,ItemStack.of(item));}
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){saveEquipment(tag);if(towId()!=null)tag.putUUID("Tow",towId());}
    @Override protected void readAdditionalSaveData(CompoundTag tag){loadEquipment(tag);entityData.set(TOW,tag.hasUUID("Tow")?Optional.of(tag.getUUID("Tow")):Optional.empty());setDeltaMovement(Vec3.ZERO);}
    @Override public InteractionResult interact(Player p,InteractionHand hand){
        if(isRemoved()||hand!=InteractionHand.MAIN_HAND||!p.getAbilities().mayBuild||p.isSpectator()||p.distanceToSqr(this)>64||!level().mayInteract(p,blockPosition()))return InteractionResult.FAIL;
        if(p.isShiftKeyDown()&&p.getMainHandItem().is(VehicleContent.PAPERS.get())&&authorized(p)&&towId()!=null){
            NgCityEntity car=tow();if(car!=null&&(!car.isParkingBraked()||car.engineRunning()))return InteractionResult.FAIL;
            if(!level().isClientSide)detach();return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if(!canOpen(p))return InteractionResult.FAIL;
        if(!level().isClientSide){
            claim(p);
            if(p.isShiftKeyDown()&&p.getMainHandItem().isEmpty()){
                if(towId()!=null||hasOpeners())return InteractionResult.FAIL;
                ItemStack item=getPickResult();discard();if(!p.addItem(item))p.drop(item,false);
            }else p.openMenu(new SimpleMenuProvider((id,inventory,who)->new ChestMenu(MenuType.GENERIC_9x3,id,inventory,cargo,3){
                @Override public boolean stillValid(Player player){return canOpen(player);}
                @Override public void clicked(int slot,int button,ClickType click,Player player){if(stillValid(player))super.clicked(slot,button,click,player);}
                @Override public ItemStack quickMoveStack(Player player,int slot){return stillValid(player)?super.quickMoveStack(player,slot):ItemStack.EMPTY;}
            },Component.translatable("container.ng_fahrzeuge.trailer")));
        }return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
