package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class VehicleDiagnosisScreen extends AbstractContainerScreen<VehicleDiagnosisMenu>{
 public VehicleDiagnosisScreen(VehicleDiagnosisMenu m,Inventory i,Component t){super(m,i,t);imageWidth=340;imageHeight=220;inventoryLabelY=-1000;}
 @Override protected void renderBg(GuiGraphics g,float p,int x,int y){g.fill(leftPos,topPos,leftPos+340,topPos+220,0xff12372e);g.fill(leftPos+3,topPos+3,leftPos+337,topPos+217,0xffd5e7df);var c=menu.car(minecraft.player);if(c==null)return;int yy=topPos+27;g.drawString(font,c.getName(),leftPos+12,yy,0xff173b30,false);yy+=20;for(String line:c.systems.mechanics.diagnosis().split("\n")){for(var part:font.split(Component.literal(line),316)){g.drawString(font,part,leftPos+12,yy,0xff173b30,false);yy+=11;}yy+=9;}g.drawString(font,"Reparatur: parken, Motor aus, Werkzeug + Ersatzteile",leftPos+12,topPos+199,0xff365d4a,false);}
 @Override public void render(GuiGraphics g,int x,int y,float p){renderBackground(g,x,y,p);super.render(g,x,y,p);}
}
