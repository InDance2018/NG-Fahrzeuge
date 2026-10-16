package de.nevergate.fahrzeuge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
/** Held dye is the explicit colour choice; one dye plus one iron ingot per successful repaint. */
public final class VehiclePaintBlock extends Block {
    public VehiclePaintBlock(Properties p){super(p);}
    public static boolean paint(NgCityEntity car,Player p,ItemStack dye){
        if(p.level().isClientSide||!(dye.getItem() instanceof DyeItem d)||car.level()!=p.level()||!car.systems.serviceAllowed(p)||!car.equipment.isOwner(p)||car.isVehicle()||car.equipment.hasOpeners()||car.color()==d.getDyeColor().getId())return false;
        if(!p.getAbilities().instabuild&&p.getInventory().countItem(Items.IRON_INGOT)<1)return false;
        if(!car.paint(d.getDyeColor().getId()))return false;
        if(!p.getAbilities().instabuild){dye.shrink(1);for(int i=0;i<p.getInventory().getContainerSize();i++)if(p.getInventory().getItem(i).is(Items.IRON_INGOT)){p.getInventory().getItem(i).shrink(1);break;}}p.getInventory().setChanged();return true;
    }
    @Override public InteractionResult use(BlockState state,Level l,BlockPos pos,Player p,InteractionHand h,BlockHitResult hit){
        if(h!=InteractionHand.MAIN_HAND)return InteractionResult.PASS;
        if(!p.getAbilities().mayBuild||!l.mayInteract(p,pos))return InteractionResult.FAIL;
        if(l.isClientSide)return InteractionResult.SUCCESS;
        var cars=l.getEntitiesOfClass(NgCityEntity.class,new AABB(pos).inflate(6),c->c.equipment.isOwner(p)&&c.systems.serviceAllowed(p));cars.sort(java.util.Comparator.comparingDouble(c->c.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)));
        boolean ok=!cars.isEmpty()&&paint(cars.get(0),p,p.getItemInHand(h));p.displayClientMessage(net.minecraft.network.chat.Component.translatable(ok?"message.ng_fahrzeuge.paint_ok":"message.ng_fahrzeuge.paint_hint"),true);return ok?InteractionResult.CONSUME:InteractionResult.FAIL;
    }
}
