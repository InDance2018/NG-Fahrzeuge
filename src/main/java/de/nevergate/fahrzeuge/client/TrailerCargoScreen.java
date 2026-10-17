package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class TrailerCargoScreen extends AbstractContainerScreen<TrailerCargoMenu>{
 public TrailerCargoScreen(TrailerCargoMenu m,Inventory i,Component t){super(m,i,t);imageWidth=176;imageHeight=239;inventoryLabelY=145;}
 @Override protected void init(){super.init();addRenderableWidget(Button.builder(Component.literal("Fracht "+(menu.page+1)+" / 2  ▶"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0)).bounds(leftPos+8,topPos+15,160,16).build());}
 @Override protected void renderBg(GuiGraphics g,float p,int x,int y){g.fill(leftPos,topPos,leftPos+176,topPos+239,0xffd5e7df);for(var s:menu.slots){g.fill(leftPos+s.x-1,topPos+s.y-1,leftPos+s.x+17,topPos+s.y+17,0xff4c625a);g.fill(leftPos+s.x,topPos+s.y,leftPos+s.x+16,topPos+s.y+16,0xffacbfb5);}}
 @Override public void render(GuiGraphics g,int x,int y,float p){renderBackground(g,x,y,p);super.render(g,x,y,p);renderTooltip(g,x,y);}
}
