package de.nevergate.fahrzeuge;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class VehicleManagementMenu extends AbstractContainerMenu {
    public final int carId;
    public VehicleManagementMenu(int id,Inventory inventory,int carId){super(VehicleContent.MANAGEMENT_MENU.get(),id);this.carId=carId;}
    public NgCityEntity car(Player player){return player.level().getEntity(carId) instanceof NgCityEntity car?car:null;}
    @Override public boolean stillValid(Player p){var car=car(p);return car!=null&&car.systems.serviceAllowed(p)&&car.equipment.isOwner(p);}
    @Override public boolean clickMenuButton(Player p,int button){
        if(!stillValid(p))return false;
        var car=car(p);
        return switch(button){
            case 0->car.systems.action(p,VehicleNetwork.DOOR_ACTION);
            case 1->car.systems.action(p,VehicleNetwork.HATCH_ACTION);
            case 2->car.systems.action(p,VehicleNetwork.TRIP_ACTION);
            case 3->car.systems.action(p,VehicleNetwork.COUPLE_ACTION);
            case 4->car.systems.comfort.action(p,VehicleNetwork.WIPER_ACTION);
            case 5->car.systems.comfort.action(p,VehicleNetwork.WINDOW_ACTION);
            case 6->car.systems.comfort.action(p,VehicleNetwork.CABIN_ACTION);
            default->false;
        };
    }
    @Override public ItemStack quickMoveStack(Player p,int index){return ItemStack.EMPTY;}
}
