package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class VehicleDirectoryScreen extends AbstractContainerScreen<VehicleDirectoryMenu> {
    private static CompoundTag selected;private static java.util.UUID viewer;
    public VehicleDirectoryScreen(VehicleDirectoryMenu m,Inventory i,Component t){super(m,i,t);imageWidth=350;imageHeight=250;inventoryLabelY=-1000;}
    @Override protected void init(){super.init();for(int i=0;i<menu.records.size();i++){var t=menu.records.get(i);addRenderableWidget(Button.builder(Component.literal(t.getString("Name")),b->{selected=t.copy();viewer=minecraft.player.getUUID();}).bounds(leftPos+8,topPos+24+i*24,148,20).build());}
        addRenderableWidget(Button.builder(Component.literal("◀"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0)).bounds(leftPos+8,topPos+222,40,20).build());
        addRenderableWidget(Button.builder(Component.literal("▶"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,1)).bounds(leftPos+52,topPos+222,40,20).build());
        addRenderableWidget(Button.builder(Component.literal("Ortung aus"),b->selected=null).bounds(leftPos+235,topPos+222,106,20).build());}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){g.fill(leftPos,topPos,leftPos+350,topPos+250,0xff123931);g.fill(leftPos+3,topPos+3,leftPos+347,topPos+247,0xffd5e7df);for(int i=0;i<menu.records.size();i++){var t=menu.records.get(i);int yy=topPos+25+i*24;g.drawString(font,Component.translatable("item.ng_fahrzeuge."+t.getString("Type")),leftPos+160,yy,0xff173b30,false);g.drawString(font,(t.getBoolean("Stored")?"Garage":t.getBoolean("Loaded")?"Geladen":"Letzter Standort")+" · "+Math.round(t.getDouble("X"))+", "+Math.round(t.getDouble("Z")),leftPos+160,yy+10,0xff415b51,false);}g.drawString(font,(menu.page+1)+" / "+menu.total,leftPos+105,topPos+228,0xff173b30,false);if(menu.records.isEmpty())g.drawString(font,"Noch keine eigenen Fahrzeuge erfasst",leftPos+12,topPos+45,0xff173b30,false);}
    public static void renderLocator(GuiGraphics g,Minecraft mc,int w,int h){if(mc.player==null||mc.options.hideGui||selected==null)return;if(!mc.player.getUUID().equals(viewer)){selected=null;return;}var t=selected;String text=t.getString("Name")+" · ";if(!mc.level.dimension().location().toString().equals(t.getString("Dimension")))text+=t.getString("Dimension");else{double dx=t.getDouble("X")-mc.player.getX(),dz=t.getDouble("Z")-mc.player.getZ();float bearing=(float)Math.toDegrees(Math.atan2(-dx,dz)),relative=net.minecraft.util.Mth.wrapDegrees(bearing-mc.player.getYRot());String direction=Math.abs(relative)<25?"↑":relative>=25&&relative<155?"→":relative<=-25&&relative> -155?"←":"↓";text+=direction+" "+Math.round(Math.hypot(dx,dz))+" m";}text+=" · letzter Standort";g.fill(5,h-33,Math.min(w-5,mc.font.width(text)+15),h-14,0xdd102b29);g.drawString(mc.font,text,10,h-27,0xffb7f0df,false);}
    @Override public void render(GuiGraphics g,int x,int y,float p){renderBackground(g,x,y,p);super.render(g,x,y,p);}
}
