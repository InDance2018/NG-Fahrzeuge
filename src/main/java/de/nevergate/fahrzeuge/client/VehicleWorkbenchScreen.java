package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.VehicleWorkbenchMenu;
import de.nevergate.fahrzeuge.VehicleAssembly;
import de.nevergate.fahrzeuge.VehicleSpec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class VehicleWorkbenchScreen extends AbstractContainerScreen<VehicleWorkbenchMenu> {
    private VehicleSpec selected = VehicleSpec.CITY;
    public VehicleWorkbenchScreen(VehicleWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth=316; imageHeight=208; inventoryLabelY=114;
    }
    @Override protected void init() {
        super.init();
        for (VehicleSpec spec:VehicleSpec.values()) addRenderableWidget(Button.builder(
                Component.translatable("gui.ng_fahrzeuge."+spec.id), b -> selected=spec)
                .bounds(leftPos+7+spec.ordinal()*76,topPos+20,74,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ng_fahrzeuge.assemble"), b -> {
            if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,selected.ordinal());
        }).bounds(leftPos+8,topPos+91,300,20).build());
    }
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY) {
        graphics.fill(leftPos,topPos,leftPos+imageWidth,topPos+imageHeight,0xff243438);
        graphics.fill(leftPos+3,topPos+3,leftPos+313,topPos+205,0xffd6e5df);
        for(var slot:menu.slots){
            graphics.fill(leftPos+slot.x-1,topPos+slot.y-1,leftPos+slot.x+17,topPos+slot.y+17,0xff4b605a);
            graphics.fill(leftPos+slot.x,topPos+slot.y,leftPos+slot.x+16,topPos+slot.y+16,0xffa9bdb4);
        }
        int y=topPos+44;
        for(var cost:VehicleAssembly.costs(selected)) {
            graphics.drawString(font,Component.literal(cost.count()+" × ").append(cost.item().getDescription()),leftPos+9,y,0xff172924,false);y+=9;
        }
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial) {
        renderBackground(graphics,mouseX,mouseY,partial);super.render(graphics,mouseX,mouseY,partial);renderTooltip(graphics,mouseX,mouseY);
    }
}
