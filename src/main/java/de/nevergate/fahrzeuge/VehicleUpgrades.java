package de.nevergate.fahrzeuge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.chat.Component;

/** Server-owned Dev10 equipment and wear. Cargo is saved with the vehicle, never in a separate item. */
public final class VehicleUpgrades {
    private final NgCityEntity car;
    private boolean hood,rack,beacon,jacked;private float dirt;private final float[] tyres={100,100,100,100};private int openers;
    public final SimpleContainer roof=new SimpleContainer(18){
        @Override public void startOpen(Player p){openers++;}
        @Override public void stopOpen(Player p){openers=Math.max(0,openers-1);}
        @Override public boolean stillValid(Player p){return rack&&car.systems.serviceAllowed(p);}
    };
    VehicleUpgrades(NgCityEntity car){this.car=car;}
    private CompoundTag client(){return car.systemTag();}
    public boolean hood(){return car.level().isClientSide?client().getBoolean("Hood"):hood;}
    public boolean rack(){return car.level().isClientSide?client().getBoolean("RoofRack"):rack;}
    public boolean beacon(){return car.level().isClientSide?client().getBoolean("Beacon"):beacon;}
    public boolean jacked(){return car.level().isClientSide?client().getBoolean("Jacked"):jacked;}
    public float dirt(){return car.level().isClientSide?client().getFloat("Dirt"):dirt;}
    public float tyre(int n){return car.level().isClientSide?client().getFloat("Tyre"+n):tyres[n];}
    public float weakest(){float n=100;for(int i=0;i<4;i++)n=Math.min(n,tyre(i));return n;}
    public boolean hasOpeners(){return openers>0;}
    public boolean empty(){return roof.isEmpty();}
    public boolean action(Player p,int action){
        if(car.level().isClientSide||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||!car.equipment.authorized(p)||p.distanceToSqr(car)>64)return false;
        if(action==VehicleNetwork.BEACON_ACTION){if(car.spec()!=VehicleSpec.TOW_TRUCK)return false;beacon=!beacon;}
        else if(!car.systems.serviceAllowed(p))return false;
        else if(action==VehicleNetwork.HOOD_ACTION){hood=!hood;}
        else if(action==VehicleNetwork.ROOF_ACTION){if(!rack)return false;openRoof(p);return true;}
        else return false;
        car.systems.sync();return true;
    }
    private void openRoof(Player p){
        p.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,who)->new ChestMenu(MenuType.GENERIC_9x2,id,inv,roof,2){
            @Override public boolean stillValid(Player player){return rack&&car.systems.serviceAllowed(player);}
            @Override public void clicked(int s,int b,net.minecraft.world.inventory.ClickType t,Player player){if(stillValid(player))super.clicked(s,b,t,player);}
            @Override public ItemStack quickMoveStack(Player player,int slot){return stillValid(player)?super.quickMoveStack(player,slot):ItemStack.EMPTY;}
        },Component.translatable("container.ng_fahrzeuge.roof")));
    }
    public boolean tool(Player p,ItemStack stack){
        if(car.level().isClientSide||!car.systems.serviceAllowed(p)||car.isVehicle()||car.systems.comfort.connected()||car.systems.attached()||car.equipment.hasOpeners()||hasOpeners())return false;
        if(stack.is(VehicleContent.RACK.get())){
            if(car.spec()==VehicleSpec.SPORT||car.spec()==VehicleSpec.TRACTOR||car.spec()==VehicleSpec.MOTORCYCLE||car.spec()==VehicleSpec.BUS||car.spec()==VehicleSpec.TOW_TRUCK||car.spec()==VehicleSpec.FAMILY&&car.systems.comfort.variant()==1)return false;
            if(rack){if(!p.isShiftKeyDown()||!roof.isEmpty())return false;rack=false;if(!p.getAbilities().instabuild&&!p.addItem(new ItemStack(VehicleContent.RACK.get())))p.drop(new ItemStack(VehicleContent.RACK.get()),false);}
            else{rack=true;if(!p.getAbilities().instabuild)stack.shrink(1);}
        }else if(stack.is(VehicleContent.JACK.get())){jacked=!jacked;}
        else if(stack.is(VehicleContent.SPARE_WHEEL.get())){
            if(!jacked||!p.getInventory().contains(new ItemStack(VehicleContent.REPAIR_TOOL.get())))return false;
            int n=0;for(int i=1;i<4;i++)if(tyres[i]<tyres[n])n=i;if(tyres[n]>=100)return false;tyres[n]=100;if(!p.getAbilities().instabuild)stack.shrink(1);
        }else return false;car.systems.sync();return true;
    }
    public void impact(float amount){if(car.level().isClientSide||!Float.isFinite(amount)||amount<=0)return;int index=(int)(car.getUUID().getLeastSignificantBits()&3);tyres[index]=Math.max(0,tyres[index]-amount);car.systems.sync();}
    public void travelled(double distance){
        if(!Double.isFinite(distance)||distance<0||distance>2)return;
        if(distance>0){dirt=Math.min(100,dirt+(float)distance*(car.systems.grip()<1?.18F:.025F));for(int i=0;i<4;i++)tyres[i]=Math.max(0,tyres[i]-(float)distance*.0002F);}
        if(car.getDriveSpeed()<-.01F&&car.engineRunning()&&(car.spec()==VehicleSpec.TRANSPORTER||car.spec()==VehicleSpec.TOW_TRUCK)&&car.tickCount%20==0)
            car.level().playSound(null,car.blockPosition(),VehicleSounds.REVERSE.get(),net.minecraft.sounds.SoundSource.NEUTRAL,.55F,1);
    }
    public boolean wash(Player p){if(car.level().isClientSide||!car.systems.serviceAllowed(p)||dirt<=0)return false;dirt=0;car.systems.sync();return true;}
    public void save(CompoundTag tag){tag.putBoolean("Hood",hood);tag.putBoolean("RoofRack",rack);tag.putBoolean("Beacon",beacon);tag.putBoolean("Jacked",jacked);tag.putFloat("Dirt",dirt);for(int i=0;i<4;i++)tag.putFloat("Tyre"+i,tyres[i]);ListTag list=new ListTag();for(int i=0;i<18;i++)if(!roof.getItem(i).isEmpty()){var item=roof.getItem(i).save(new CompoundTag());item.putInt("Slot",i);list.add(item);}tag.put("RoofCargo",list);}
    public void load(CompoundTag tag){hood=tag.getBoolean("Hood");rack=tag.getBoolean("RoofRack");beacon=tag.getBoolean("Beacon");jacked=tag.getBoolean("Jacked");dirt=number(tag,"Dirt",0);for(int i=0;i<4;i++)tyres[i]=number(tag,"Tyre"+i,100);roof.clearContent();var items=tag.getList("RoofCargo",10);for(int i=0;i<items.size();i++){var t=items.getCompound(i);int slot=t.getInt("Slot");if(slot>=0&&slot<18)roof.setItem(slot,ItemStack.of(t));}if(!roof.isEmpty())rack=true;}
    private static float number(CompoundTag tag,String key,float fallback){float n=tag.contains(key,99)?tag.getFloat(key):fallback;return Float.isFinite(n)?Math.max(0,Math.min(100,n)):fallback;}
}
