package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class VehicleManagementScreen extends AbstractContainerScreen<VehicleManagementMenu> {
    private EditBox name,target,plate;
    public VehicleManagementScreen(VehicleManagementMenu menu,Inventory inventory,Component title){super(menu,inventory,title);imageWidth=300;imageHeight=258;inventoryLabelY=-1000;}
    private Component text(String key){return Component.translatable("gui.ng_fahrzeuge."+key);}
    @Override protected void init(){
        super.init();
        plate=new EditBox(font,leftPos+10,topPos+181,180,18,text("plate"));plate.setMaxLength(12);
        if(menu.car(minecraft.player)!=null)plate.setValue(menu.car(minecraft.player).systems.comfort.plate());addRenderableWidget(plate);
        addRenderableWidget(Button.builder(text("plate"),b->edit(3,plate.getValue())).bounds(leftPos+196,topPos+180,94,20).build());
        String[] comfort={"wipers","windows","cabin"};for(int i=0;i<3;i++){final int n=4+i;
            addRenderableWidget(Button.builder(text(comfort[i]),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,n)).bounds(leftPos+10+i*94,topPos+206,92,20).build());}
        name=new EditBox(font,leftPos+10,topPos+32,180,18,text("name"));name.setMaxLength(48);
        if(menu.car(minecraft.player)!=null)name.setValue(menu.car(minecraft.player).getName().getString());addRenderableWidget(name);
        target=new EditBox(font,leftPos+10,topPos+74,180,18,text("new_owner"));target.setMaxLength(16);addRenderableWidget(target);
        addRenderableWidget(Button.builder(text("save_name"),b->edit(0,name.getValue())).bounds(leftPos+196,topPos+31,94,20).build());
        addRenderableWidget(Button.builder(text("transfer"),b->edit(1,target.getValue())).bounds(leftPos+196,topPos+73,94,20).build());
        addRenderableWidget(Button.builder(text("revoke_keys"),b->edit(2,"")).bounds(leftPos+10,topPos+101,280,20).build());
        String[] keys={"doors","hatch","reset_trip","couple"};
        for(int i=0;i<4;i++){final int n=i;addRenderableWidget(Button.builder(text(keys[i]),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,n))
                .bounds(leftPos+10+(i%2)*142,topPos+129+(i/2)*25,138,20).build());}
    }
    private void edit(int op,String value){VehicleNetwork.CHANNEL.send(new VehicleNetwork.Manage(menu.carId,op,value),net.minecraftforge.network.PacketDistributor.SERVER.noArg());}
    @Override protected void renderBg(GuiGraphics g,float partial,int mx,int my){
        g.fill(leftPos,topPos,leftPos+300,topPos+258,0xff233e39);g.fill(leftPos+3,topPos+3,leftPos+297,topPos+255,0xffd5e7df);
        g.drawString(font,text("name"),leftPos+10,topPos+21,0xff19392e,false);
        g.drawString(font,text("new_owner"),leftPos+10,topPos+62,0xff19392e,false);
        var car=menu.car(minecraft.player);
        if(car!=null){g.drawString(font,Component.translatable("gui.ng_fahrzeuge.condition",Math.round(car.systems.body()),Math.round(car.systems.motor())),leftPos+10,topPos+234,0xff19392e,false);
            g.drawString(font,text("owner_hint"),leftPos+10,topPos+246,0xff19392e,false);}
    }
    @Override public void render(GuiGraphics g,int mx,int my,float partial){renderBackground(g,mx,my,partial);super.render(g,mx,my,partial);renderTooltip(g,mx,my);}
}
