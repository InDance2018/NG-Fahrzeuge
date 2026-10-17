package de.nevergate.fahrzeuge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
/** Two server-bound pages over the same persistent 108 slots; never copies cargo into a preview container. */
public final class TrailerCargoMenu extends AbstractContainerMenu {
 public final int entityId,page;private final TrailerEntity trailer;
 public TrailerCargoMenu(int id,Inventory inv,FriendlyByteBuf b){this(id,inv,b.readVarInt(),b.readUnsignedByte());}
 public TrailerCargoMenu(int id,Inventory inv,int entityId,int page){super(VehicleContent.SEMI_MENU.get(),id);this.entityId=entityId;this.page=page==1?1:0;trailer=inv.player.level().getEntity(entityId) instanceof TrailerEntity t?t:null;
  var cargo=trailer!=null&&!inv.player.level().isClientSide?trailer.cargo:new net.minecraft.world.SimpleContainer(108);
  for(int y=0;y<6;y++)for(int x=0;x<9;x++)addSlot(new Slot(cargo,this.page*54+y*9+x,8+x*18,32+y*18));
  for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,x+y*9+9,8+x*18,157+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,215));if(trailer!=null&&!inv.player.level().isClientSide)trailer.cargo.startOpen(inv.player);
 }
 public static void open(TrailerEntity t,Player p,int page){if(!(p instanceof net.minecraft.server.level.ServerPlayer s)||t.variant()!=3||!t.canOpen(p))return;s.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,who)->new TrailerCargoMenu(id,inv,t.getId(),page),Component.translatable("container.ng_fahrzeuge.semi")),b->{b.writeVarInt(t.getId());b.writeByte(page);});}
 @Override public boolean stillValid(Player p){return trailer!=null&&trailer.variant()==3&&trailer.canOpen(p);}
 @Override public boolean clickMenuButton(Player p,int b){if(p.level().isClientSide||!stillValid(p)||b!=0)return false;open(trailer,p,1-page);return true;}
 @Override public void removed(Player p){super.removed(p);if(trailer!=null&&!p.level().isClientSide)trailer.cargo.stopOpen(p);}
 @Override public void clicked(int s,int b,ClickType t,Player p){if(stillValid(p))super.clicked(s,b,t,p);}
 @Override public ItemStack quickMoveStack(Player p,int i){if(!stillValid(p)||i<0||i>=slots.size())return ItemStack.EMPTY;var slot=slots.get(i);if(!slot.hasItem())return ItemStack.EMPTY;var item=slot.getItem();var copy=item.copy();if(i<54?!moveItemStackTo(item,54,90,true):!moveItemStackTo(item,0,54,false))return ItemStack.EMPTY;if(item.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
