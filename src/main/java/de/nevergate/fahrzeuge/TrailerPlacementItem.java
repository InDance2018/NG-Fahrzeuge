package de.nevergate.fahrzeuge;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public final class TrailerPlacementItem extends Item {
    private final int variant;
    public TrailerPlacementItem(Properties properties){this(properties,0);}
    public TrailerPlacementItem(Properties properties,int variant){super(properties);this.variant=variant;}
    @Override public InteractionResult useOn(UseOnContext ctx){
        var p=ctx.getPlayer();var level=ctx.getLevel();
        if(p==null||ctx.getClickedFace()!=Direction.UP||!p.getAbilities().mayBuild||!level.mayInteract(p,ctx.getClickedPos())
                ||!p.mayUseItemAt(ctx.getClickedPos().above(),Direction.UP,ctx.getItemInHand()))return InteractionResult.FAIL;
        var trailer=VehicleContent.TRAILER.get().create(level);if(trailer==null)return InteractionResult.FAIL;
        trailer.setPos(ctx.getClickedPos().getX()+.5,ctx.getClickLocation().y,ctx.getClickedPos().getZ()+.5);
        trailer.setYRot(Math.round(ctx.getRotation()/90)*90);if(ctx.getItemInHand().hasTag())trailer.loadEquipment(ctx.getItemInHand().getTag());
        if(variant!=0)trailer.variant(variant);
        if(!level.noCollision(trailer,trailer.getBoundingBox().deflate(.001))||!level.getWorldBorder().isWithinBounds(trailer.getBoundingBox()))return InteractionResult.FAIL;
        if(!level.isClientSide){trailer.claim(p);if(!level.addFreshEntity(trailer))return InteractionResult.FAIL;if(!p.getAbilities().instabuild)ctx.getItemInHand().shrink(1);}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
