package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class VehicleManagementScreen extends AbstractContainerScreen<VehicleManagementMenu> {
    private int page;
    public VehicleManagementScreen(VehicleManagementMenu m,Inventory i,Component t){super(m,i,t);imageWidth=300;imageHeight=250;inventoryLabelY=-1000;}
    private void button(String text,int x,int y,int w,Runnable action){addRenderableWidget(Button.builder(Component.literal(text),b->action.run()).bounds(leftPos+x,topPos+y,w,20).build());}
    private void field(String label,String value,int max,int op,int y){var box=new EditBox(font,leftPos+10,topPos+y,176,18,Component.literal(label));box.setMaxLength(max);box.setValue(value);addRenderableWidget(box);button(label,192,y-1,98,()->edit(op,box.getValue()));}
    private void edit(int op,String value){VehicleNetwork.CHANNEL.send(new VehicleNetwork.Manage(menu.carId,op,value),net.minecraftforge.network.PacketDistributor.SERVER.noArg());}
    @Override protected void init(){super.init();String[] tabs={"Fahrzeug","Bedienung","Sitz / Linie"};for(int i=0;i<3;i++){int p=i;button(tabs[i],8+i*95,20,93,()->{page=p;clearWidgets();init();});}var c=menu.car(minecraft.player);if(c==null)return;
        if(page==0){field("Name",c.getName().getString(),48,0,65);field("Besitzer", "",16,1,100);field("Kennzeichen",c.systems.comfort.plate(),12,3,135);button("Alle Schlüssel widerrufen",10,174,280,()->edit(2,""));}
        if(page==1){String[] names={"Vordere Türen","Heck / hintere Tür","Tageskilometer","Anhänger","Scheibenwischer","Seitenfenster","Innenlicht","Motorhaube","Warnbalken","Dachgepäck"};for(int i=0;i<names.length;i++){int n=i;button(names[i],10+(i%2)*142,56+(i/2)*31,138,()->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,n));}}
        if(page==2){if(c.spec()==VehicleSpec.BUS){field("Linie",c.systems.expansion.route(),8,4,65);field("Fahrtziel",c.systems.expansion.destination(),32,5,95);}button("Sitz zurück",10,137,138,()->seat(-1,0));button("Sitz vor",152,137,138,()->seat(1,0));button("Sitz tiefer",10,164,138,()->seat(0,-1));button("Sitz höher",152,164,138,()->seat(0,1));button("Sitz zurücksetzen",10,195,280,()->edit(6,"0,0"));}
    }
    private void seat(int f,int h){var c=menu.car(minecraft.player);if(c!=null)edit(6,Math.max(-5,Math.min(5,c.systems.expansion.seatForward()+f))+","+Math.max(-4,Math.min(4,c.systems.expansion.seatHeight()+h)));}
    @Override protected void renderBg(GuiGraphics g,float p,int x,int y){g.fill(leftPos,topPos,leftPos+300,topPos+250,0xff173d35);g.fill(leftPos+3,topPos+3,leftPos+297,topPos+247,0xffd5e7df);var c=menu.car(minecraft.player);if(c!=null){g.drawString(font,"Karosserie "+Math.round(c.systems.body())+"% · Motor "+Math.round(c.systems.motor())+"%",leftPos+10,topPos+233,0xff173b30,false);if(page==2)g.drawString(font,"Sitz: "+c.systems.expansion.seatForward()+" / "+c.systems.expansion.seatHeight(),leftPos+10,topPos+123,0xff173b30,false);}}
    @Override public void render(GuiGraphics g,int x,int y,float p){renderBackground(g,x,y,p);super.render(g,x,y,p);renderTooltip(g,x,y);}
}
