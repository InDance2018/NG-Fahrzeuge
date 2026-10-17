package de.nevergate.fahrzeuge;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
public final class VehicleCoolantItem extends Item {
 public VehicleCoolantItem(Properties p){super(p);}
 public static int remaining(ItemStack s){return s.hasTag()&&s.getTag().contains("CoolantMl",99)?Math.max(0,Math.min(4000,s.getTag().getInt("CoolantMl"))):4000;}
 public static boolean fill(NgCityEntity c,Player p,ItemStack can){if(c.level().isClientSide||!can.is(VehicleContent.COOLANT.get())||remaining(can)==0)return false;int available=remaining(can);float n=c.systems.mechanics.addCoolant(p,Math.min(1000,available)/1000F);if(n<=0)return false;if(!p.getAbilities().instabuild)can.getOrCreateTag().putInt("CoolantMl",Math.max(0,available-Math.round(n*1000)));return true;}
 @Override public void appendHoverText(ItemStack s,Level l,java.util.List<Component> lines,TooltipFlag flag){lines.add(Component.literal(remaining(s)+" / 4000 ml"));lines.add(Component.translatable("tooltip.ng_fahrzeuge.coolant"));}
}
