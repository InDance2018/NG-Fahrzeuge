package de.nevergate.fahrzeuge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class VehicleDealerConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<String> PAYMENT;
    public static final ForgeConfigSpec.IntValue SELL_PERCENT;
    private static final java.util.List<ForgeConfigSpec.IntValue> PRICES=new java.util.ArrayList<>();
    static{var b=new ForgeConfigSpec.Builder();b.push("dealer");PAYMENT=b.define("payment_item","minecraft:emerald");
        for(VehicleSpec spec:VehicleSpec.values())PRICES.add(b.defineInRange(spec.id+"_price",new int[]{24,40,56,96,144,32,64,80,112,64,160}[spec.ordinal()],1,4096));SELL_PERCENT=b.defineInRange("sell_percent",50,1,100);b.pop();SPEC=b.build();}
    public static int price(VehicleSpec spec){return PRICES.get(spec.ordinal()).get();}
    public static Item payment(){var id=ResourceLocation.tryParse(PAYMENT.get());var item=id==null?null:ForgeRegistries.ITEMS.getValue(id);return item==null||item==Items.AIR?Items.EMERALD:item;}
    public static boolean buy(net.minecraft.world.entity.player.Player p,VehicleSpec spec){
        if(p.level().isClientSide||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||p.getInventory().getFreeSlot()<0)return false;
        Item item=payment();int price=price(spec);
        if(!p.getAbilities().instabuild){if(p.getInventory().countItem(item)<price)return false;
            int left=price;for(int i=0;i<p.getInventory().getContainerSize()&&left>0;i++){var stack=p.getInventory().getItem(i);if(stack.is(item)){int n=Math.min(left,stack.getCount());stack.shrink(n);left-=n;}}}
        p.getInventory().add(new net.minecraft.world.item.ItemStack(VehicleContent.item(spec)));p.getInventory().setChanged();return true;
    }
    public static int quote(NgCityEntity car){
        float condition=(car.systems.body()+car.systems.motor()+car.systems.upgrades.weakest())/300F;
        return Math.max(1,(int)Math.floor(price(car.spec())*SELL_PERCENT.get()/100F*condition));
    }
    public static NgCityEntity saleCandidate(net.minecraft.world.entity.player.Player p,VehicleSpec spec){
        var cars=p.level().getEntitiesOfClass(NgCityEntity.class,p.getBoundingBox().inflate(8),c->c.spec()==spec&&c.equipment.isOwner(p)&&c.systems.serviceAllowed(p));
        cars.sort(java.util.Comparator.comparingDouble(p::distanceToSqr));return cars.isEmpty()?null:cars.get(0);
    }
    public static boolean sell(net.minecraft.world.entity.player.Player p,NgCityEntity car){
        if(p.level().isClientSide||!p.isAlive()||car==null||car.level()!=p.level()||!car.equipment.isOwner(p)||!car.systems.serviceAllowed(p)||car.isVehicle()||car.equipment.hasOpeners()||car.systems.attached()||car.systems.comfort.connected()||car.systems.upgrades.jacked()||!car.equipment.cargo.isEmpty()||!car.systems.upgrades.empty())return false;
        var payment=payment();int amount=quote(car),capacity=0;var prototype=new net.minecraft.world.item.ItemStack(payment);for(int i=0;i<36;i++){var stack=p.getInventory().getItem(i);if(stack.isEmpty())capacity+=payment.getMaxStackSize();else if(net.minecraft.world.item.ItemStack.isSameItemSameTags(stack,prototype))capacity+=Math.max(0,payment.getMaxStackSize()-stack.getCount());}
        if(capacity<amount)return false;
        // All gates precede payout; one server-thread transaction discards the sold instance exactly once.
        car.discard();int left=amount;while(left>0){int n=Math.min(left,payment.getMaxStackSize());p.getInventory().add(new net.minecraft.world.item.ItemStack(payment,n));left-=n;}p.getInventory().setChanged();return true;
    }
    private VehicleDealerConfig(){}
}
