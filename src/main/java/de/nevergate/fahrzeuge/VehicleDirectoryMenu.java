package de.nevergate.fahrzeuge;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class VehicleDirectoryMenu extends AbstractContainerMenu {
    public final List<CompoundTag> records;public final int page,total;
    public VehicleDirectoryMenu(int id,Inventory inv,FriendlyByteBuf b){this(id,read(b),b.readVarInt(),b.readVarInt());}
    private static List<CompoundTag> read(FriendlyByteBuf b){int n=b.readVarInt();if(n<0||n>8)throw new IllegalArgumentException("Directory size");var a=new ArrayList<CompoundTag>();for(int i=0;i<n;i++)a.add(b.readNbt());return a;}
    public VehicleDirectoryMenu(int id,List<CompoundTag> list,int page,int total){super(VehicleContent.DIRECTORY_MENU.get(),id);this.records=list;this.page=page;this.total=total;}
    public static void open(Player p,int requested){if(!(p instanceof net.minecraft.server.level.ServerPlayer s)||p.isSpectator())return;
        var all=VehicleDirectoryData.get(s.serverLevel()).own(p.getUUID());int pages=Math.max(1,(all.size()+7)/8),page=Math.max(0,Math.min(requested,pages-1));var list=new ArrayList<>(all.subList(Math.min(page*8,all.size()),Math.min(page*8+8,all.size())));
        // Only ownership-filtered display fields travel to this player; never inventories or key identifiers.
        for(var t:list){t.remove("Owner");boolean loaded=false;for(var l:s.server.getAllLevels())if(l.getEntity(t.getUUID("Id")) instanceof NgCityEntity c&&p.getUUID().equals(c.equipment.owner()))loaded=true;t.putBoolean("Loaded",loaded);}
        s.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,who)->new VehicleDirectoryMenu(id,list,page,pages),Component.translatable("container.ng_fahrzeuge.directory")),b->{b.writeVarInt(list.size());for(var t:list)b.writeNbt(t);b.writeVarInt(page);b.writeVarInt(pages);});
    }
    @Override public boolean stillValid(Player p){return p.isAlive()&&!p.isSpectator();}
    @Override public boolean clickMenuButton(Player p,int n){if(p.level().isClientSide||!stillValid(p)||(n!=0&&n!=1))return false;open(p,page+(n==0?-1:1));return true;}
    @Override public ItemStack quickMoveStack(Player p,int i){return ItemStack.EMPTY;}
}
