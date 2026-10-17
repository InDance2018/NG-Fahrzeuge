package de.nevergate.fahrzeuge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
public final class VehicleDiagnosisMenu extends AbstractContainerMenu {
 public final int carId;
 public VehicleDiagnosisMenu(int id,Inventory inv,FriendlyByteBuf b){this(id,b.readVarInt());}
 public VehicleDiagnosisMenu(int id,int car){super(VehicleContent.DIAGNOSIS_MENU.get(),id);carId=car;}
 public NgCityEntity car(Player p){return p.level().getEntity(carId) instanceof NgCityEntity c?c:null;}
 @Override public boolean stillValid(Player p){var c=car(p);return c!=null&&c.systems.serviceAllowed(p);}
 public static boolean open(NgCityEntity c,Player p){if(!(p instanceof net.minecraft.server.level.ServerPlayer s)||!c.systems.serviceAllowed(p))return false;s.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,who)->new VehicleDiagnosisMenu(id,c.getId()),Component.translatable("container.ng_fahrzeuge.diagnosis")),b->b.writeVarInt(c.getId()));return true;}
 @Override public ItemStack quickMoveStack(Player p,int i){return ItemStack.EMPTY;}
}
