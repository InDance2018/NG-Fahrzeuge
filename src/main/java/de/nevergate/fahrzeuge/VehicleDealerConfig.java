package de.nevergate.fahrzeuge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class VehicleDealerConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<String> PAYMENT;
    private static final java.util.List<ForgeConfigSpec.IntValue> PRICES=new java.util.ArrayList<>();
    static{var b=new ForgeConfigSpec.Builder();b.push("dealer");PAYMENT=b.define("payment_item","minecraft:emerald");
        for(VehicleSpec spec:VehicleSpec.values())PRICES.add(b.defineInRange(spec.id+"_price",new int[]{24,40,56,96}[spec.ordinal()],1,4096));b.pop();SPEC=b.build();}
    public static int price(VehicleSpec spec){return PRICES.get(spec.ordinal()).get();}
    public static Item payment(){var id=ResourceLocation.tryParse(PAYMENT.get());var item=id==null?null:ForgeRegistries.ITEMS.getValue(id);return item==null||item==Items.AIR?Items.EMERALD:item;}
    public static boolean buy(net.minecraft.world.entity.player.Player p,VehicleSpec spec){
        if(p.level().isClientSide||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||p.getInventory().getFreeSlot()<0)return false;
        Item item=payment();int price=price(spec);
        if(!p.getAbilities().instabuild){if(p.getInventory().countItem(item)<price)return false;
            int left=price;for(int i=0;i<p.getInventory().getContainerSize()&&left>0;i++){var stack=p.getInventory().getItem(i);if(stack.is(item)){int n=Math.min(left,stack.getCount());stack.shrink(n);left-=n;}}}
        p.getInventory().add(new net.minecraft.world.item.ItemStack(VehicleContent.item(spec)));p.getInventory().setChanged();return true;
    }
    private VehicleDealerConfig(){}
}
