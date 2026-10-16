package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class VehicleServiceScreen extends AbstractContainerScreen<VehicleServiceMenu> {
    private int page;
    public VehicleServiceScreen(VehicleServiceMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=316;imageHeight=230;inventoryLabelY=136;}
    @Override protected void init(){super.init();
        if(menu.service==0)addRenderableWidget(Button.builder(Component.literal("◀  "+(page+1)+"/2  ▶"),b->{page=1-page;clearWidgets();init();}).bounds(leftPos+220,topPos+114,88,18).build());
        if(menu.service==0)for(var spec:VehicleSpec.values()){
            int i=spec.ordinal();if(i/4!=page)continue;int slot=i%4;addRenderableWidget(Button.builder(Component.translatable("item.ng_fahrzeuge."+spec.id),b->act(20+i)).bounds(leftPos+8+(slot%2)*152,topPos+22+(slot/2)*48,101,20).build());
            addRenderableWidget(Button.builder(Component.translatable("gui.ng_fahrzeuge.sell"),b->act(30+i)).tooltip(Tooltip.create(Component.translatable("gui.ng_fahrzeuge.sale_quote",menu.sales[i]))).bounds(leftPos+110+(slot%2)*152,topPos+22+(slot/2)*48,48,20).build());
        }else{
            for(int i=0;i<menu.previews.size();i++){int n=i;String name=menu.previews.get(i).getHoverName().getString();String label=(i+1)+". "+(name.length()>11?name.substring(0,10)+"…":name);
                addRenderableWidget(Button.builder(Component.literal(label),b->act(n)).tooltip(Tooltip.create(menu.previews.get(i).getHoverName())).bounds(leftPos+8+(i%3)*100,topPos+22+(i/3)*25,98,22).build());}
            addRenderableWidget(Button.builder(Component.translatable("gui.ng_fahrzeuge.store"),b->act(9)).bounds(leftPos+8,topPos+108,300,20).build());
        }
    }
    private void act(int button){minecraft.gameMode.handleInventoryButtonClick(menu.containerId,button);}
    @Override protected void renderBg(GuiGraphics g,float partial,int mx,int my){
        g.fill(leftPos,topPos,leftPos+316,topPos+230,0xff243f39);g.fill(leftPos+3,topPos+3,leftPos+313,topPos+227,0xffd5e7df);
        for(var slot:menu.slots){g.fill(leftPos+slot.x-1,topPos+slot.y-1,leftPos+slot.x+17,topPos+slot.y+17,0xff4b605a);g.fill(leftPos+slot.x,topPos+slot.y,leftPos+slot.x+16,topPos+slot.y+16,0xffa9bdb4);}
        if(menu.service==0){for(int i=page*4;i<Math.min(page*4+4,menu.previews.size());i++){int slot=i%4;g.renderItem(menu.previews.get(i),leftPos+10+(slot%2)*152,topPos+44+(slot/2)*48);
            g.drawString(font,Component.literal(menu.prices[i]+" × ").append(menu.payment.getHoverName()),leftPos+30+(slot%2)*152,topPos+48+(slot/2)*48,0xff173b30,false);}
            g.drawString(font,Component.literal("Preis / Ankauf: Item-Handel"),leftPos+8,topPos+119,0xff173b30,false);
        }else if(menu.previews.isEmpty())g.drawString(font,Component.translatable("gui.ng_fahrzeuge.garage_empty"),leftPos+10,topPos+25,0xff173b30,false);
    }
    @Override public void render(GuiGraphics g,int mx,int my,float partial){renderBackground(g,mx,my,partial);super.render(g,mx,my,partial);renderTooltip(g,mx,my);}
}
