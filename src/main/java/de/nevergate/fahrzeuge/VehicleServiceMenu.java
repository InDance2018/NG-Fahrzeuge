package de.nevergate.fahrzeuge;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class VehicleServiceMenu extends AbstractContainerMenu {
    public final BlockPos pos;public final int service;public final int[] prices,sales;public final ItemStack payment;
    public final List<ItemStack> previews;
    public VehicleServiceMenu(int id,Inventory inv,FriendlyByteBuf data){this(id,inv,data.readBlockPos(),data.readVarInt(),readPrices(data),readPrices(data),data.readItem(),readItems(data));}
    private static int[] readPrices(FriendlyByteBuf b){int[] a=new int[4];for(int i=0;i<4;i++)a[i]=b.readVarInt();return a;}
    private static List<ItemStack> readItems(FriendlyByteBuf b){int n=b.readVarInt();if(n<0||n>9)throw new IllegalArgumentException("Garage preview count");var a=new ArrayList<ItemStack>();for(int i=0;i<n;i++)a.add(b.readItem());return a;}
    public VehicleServiceMenu(int id,Inventory inv,BlockPos pos,int service,int[] prices,int[] sales,ItemStack payment,List<ItemStack> previews){
        super(VehicleContent.SERVICE_MENU.get(),id);this.pos=pos;this.service=service;this.prices=prices;this.sales=sales;this.payment=payment;this.previews=previews;
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,col+row*9+9,77+18*col,148+18*row));
        for(int col=0;col<9;col++)addSlot(new Slot(inv,col,77+18*col,206));
    }
    public static void open(Player p,BlockPos pos,int mode){
        if(!(p instanceof net.minecraft.server.level.ServerPlayer server))return;
        int[] sales=Arrays.stream(VehicleSpec.values()).mapToInt(s->{var c=VehicleDealerConfig.saleCandidate(p,s);return c==null?0:VehicleDealerConfig.quote(c);}).toArray();
        int[] prices=Arrays.stream(VehicleSpec.values()).mapToInt(VehicleDealerConfig::price).toArray();ItemStack payment=new ItemStack(VehicleDealerConfig.payment());List<ItemStack> previews=new ArrayList<>();
        if(mode==0)for(var spec:VehicleSpec.values())previews.add(new ItemStack(VehicleContent.item(spec)));
        else for(var tag:VehicleGarageData.get(server.serverLevel()).entries(p.getUUID())){
            var spec=Arrays.stream(VehicleSpec.values()).filter(s->s.id.equals(tag.getString("GarageSpec"))).findFirst().orElse(VehicleSpec.CITY);
            ItemStack item=new ItemStack(VehicleContent.item(spec));
            if(tag.contains("CustomName",8)){var name=Component.Serializer.fromJson(tag.getString("CustomName"));if(name!=null)item.setHoverName(name);}previews.add(item);
        }
        server.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,who)->new VehicleServiceMenu(id,inv,pos,mode,prices,sales,payment,previews),Component.translatable(mode==0?"container.ng_fahrzeuge.dealer":"container.ng_fahrzeuge.garage")),b->{
            b.writeBlockPos(pos);b.writeVarInt(mode);for(int value:prices)b.writeVarInt(value);for(int value:sales)b.writeVarInt(value);b.writeItem(payment);b.writeVarInt(previews.size());for(var item:previews)b.writeItem(item);
        });
    }
    @Override public boolean stillValid(Player p){return p.isAlive()&&!p.isSpectator()&&p.getAbilities().mayBuild&&p.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<=64
            &&p.level().mayInteract(p,pos)&&p.level().getBlockState(pos).is(service==0?VehicleContent.DEALER.get():VehicleContent.GARAGE.get());}
    @Override public boolean clickMenuButton(Player p,int button){
        if(!stillValid(p)||p.level().isClientSide)return false;boolean ok=false;
        if(service==0){if(button>=0&&button<4)ok=VehicleDealerConfig.buy(p,VehicleSpec.values()[button]);else if(button>=4&&button<8)ok=VehicleDealerConfig.sell(p,VehicleDealerConfig.saleCandidate(p,VehicleSpec.values()[button-4]));if(ok)open(p,pos,service);}
        else if(p.level() instanceof net.minecraft.server.level.ServerLevel level){
            var data=VehicleGarageData.get(level);
            if(button==9){var list=level.getEntitiesOfClass(NgCityEntity.class,new net.minecraft.world.phys.AABB(pos).inflate(6),c->c.equipment.isOwner(p)&&c.systems.serviceAllowed(p));list.sort(java.util.Comparator.comparingDouble(c->c.distanceToSqr(pos.getX()+.5,pos.getY(),pos.getZ()+.5)));if(!list.isEmpty())ok=data.store(list.get(0),p);}
            else ok=data.recall(p,button,pos);
            if(ok)open(p,pos,service);
        }
        p.displayClientMessage(Component.translatable(ok?"message.ng_fahrzeuge.service_ok":"message.ng_fahrzeuge.service_failed"),true);broadcastChanges();return ok;
    }
    @Override public ItemStack quickMoveStack(Player p,int index){
        if(!stillValid(p)||index<0||index>=36)return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();if(index<27?!moveItemStackTo(stack,27,36,false):!moveItemStackTo(stack,0,27,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
}
