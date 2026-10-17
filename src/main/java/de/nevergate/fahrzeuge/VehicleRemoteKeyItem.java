package de.nevergate.fahrzeuge;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
/** Reuses existing revocable key tokens; targets only already loaded matching vehicles. */
public final class VehicleRemoteKeyItem extends Item {
 public VehicleRemoteKeyItem(Properties p){super(p);}
 @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand h){var key=p.getItemInHand(h);if(l.isClientSide)return InteractionResultHolder.success(key);if(!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild)return InteractionResultHolder.fail(key);
  var list=l.getEntitiesOfClass(NgCityEntity.class,p.getBoundingBox().inflate(32),c->!c.isRemoved()&&c.distanceToSqr(p)<=1024&&c.equipment.matches(key));
  boolean ok=false;if(list.size()==1){var c=list.get(0);if(p.isShiftKeyDown())ok=c.systems.mechanics.toggleCargoLock(p);else ok=c.toggleLock(p);}
  p.displayClientMessage(Component.translatable(ok?"message.ng_fahrzeuge.remote_ok":"message.ng_fahrzeuge.remote_failed"),true);return ok?InteractionResultHolder.consume(key):InteractionResultHolder.fail(key);
 }
}
