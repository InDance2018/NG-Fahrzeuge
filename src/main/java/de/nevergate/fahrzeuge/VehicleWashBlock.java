package de.nevergate.fahrzeuge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
public final class VehicleWashBlock extends Block {
 public VehicleWashBlock(Properties p){super(p);}
 @Override public InteractionResult use(BlockState s,Level l,BlockPos pos,Player p,InteractionHand hand,BlockHitResult hit){
  if(hand!=InteractionHand.MAIN_HAND||p.isSpectator()||!p.getAbilities().mayBuild||!l.mayInteract(p,pos))return InteractionResult.FAIL;
  if(l.isClientSide)return InteractionResult.SUCCESS;
  if(!p.getAbilities().instabuild&&!p.getMainHandItem().is(net.minecraft.world.item.Items.WATER_BUCKET))return InteractionResult.FAIL;
  var cars=l.getEntitiesOfClass(NgCityEntity.class,new net.minecraft.world.phys.AABB(pos).inflate(5),c->c.systems.serviceAllowed(p)&&c.systems.upgrades.dirt()>0);cars.sort(java.util.Comparator.comparingDouble(c->c.distanceToSqr(pos.getX()+.5,pos.getY(),pos.getZ()+.5)));
  if(cars.isEmpty()||!cars.get(0).systems.upgrades.wash(p))return InteractionResult.FAIL;
  if(!p.getAbilities().instabuild)p.setItemInHand(hand,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BUCKET));
  ((net.minecraft.server.level.ServerLevel)l).sendParticles(net.minecraft.core.particles.ParticleTypes.SPLASH,cars.get(0).getX(),cars.get(0).getY()+1,cars.get(0).getZ(),40,1,.5,1,.1);return InteractionResult.CONSUME;
 }
}
