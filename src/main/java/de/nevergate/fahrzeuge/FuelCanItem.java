package de.nevergate.fahrzeuge;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** A can contains ten litres of fictional crafted vehicle fuel; partial amounts are retained. */
public final class FuelCanItem extends Item {
    public FuelCanItem(Properties properties){super(properties);}
    public static float litres(ItemStack stack){
        float n=stack.hasTag()&&stack.getTag().contains("Litres",99)?stack.getTag().getFloat("Litres"):10;
        return Float.isFinite(n)?Math.max(0,Math.min(10,n)):0;
    }
    public static void consume(ItemStack stack,float used){stack.getOrCreateTag().putFloat("Litres",Math.max(0,litres(stack)-used));}
    public static boolean refuel(NgCityEntity car,Player player,ItemStack can){
        if(!car.systems.serviceAllowed(player)||!can.is(VehicleContent.FUEL_CAN.get()))return false;
        float used=car.systems.refuel(litres(can));if(used<=0)return false;
        if(!player.getAbilities().instabuild)consume(can,used);
        player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.fuel_added",String.format(java.util.Locale.ROOT,"%.2f",used)),true);return true;
    }
    @Override public void appendHoverText(ItemStack stack,@Nullable Level level,List<Component> lines,TooltipFlag flag){
        lines.add(Component.translatable("tooltip.ng_fahrzeuge.fuel_can",String.format(java.util.Locale.ROOT,"%.2f",litres(stack))));
    }
}
