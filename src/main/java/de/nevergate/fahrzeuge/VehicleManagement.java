package de.nevergate.fahrzeuge;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class VehicleManagement {
    public static boolean open(NgCityEntity car,Player p){
        if(!car.systems.serviceAllowed(p)||!car.equipment.isOwner(p))return false;
        if(p instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id,inventory,who)->new VehicleManagementMenu(id,inventory,car.getId()),
                    Component.translatable("container.ng_fahrzeuge.management")),buffer->buffer.writeVarInt(car.getId()));
        return true;
    }
    public static boolean edit(Player p,int id,int operation,String value){
        if(!(p.containerMenu instanceof VehicleManagementMenu menu)||menu.carId!=id||!menu.stillValid(p))return false;
        NgCityEntity car=menu.car(p);if(car==null)return false;
        boolean result=false;
        if(operation==0&&value.length()<=48&&!value.codePoints().anyMatch(c->Character.isISOControl(c)||c==0xA7)){
            car.setCustomName(value.isBlank()?null:Component.literal(value.strip()));result=true;
        }else if(operation==1&&value.matches("[A-Za-z0-9_]{1,16}")){
            var next=p.getServer().getPlayerList().getPlayerByName(value);
            if(next!=null&&next!=p)result=car.equipment.changeOwner(p,next.getUUID());
            if(result)p.closeContainer();
        }else if(operation==2)result=car.equipment.revokeKeys(p);
        else if(operation==3)result=car.systems.comfort.setPlate(value);
        else if(operation==4)result=car.systems.expansion.setRoute(p,value,car.systems.expansion.destination());
        else if(operation==5)result=car.systems.expansion.setRoute(p,car.systems.expansion.route(),value);
        else if(operation==6&&value.matches("-?[0-5],-?[0-4]")){var n=value.split(",");result=car.systems.expansion.setSeat(p,Integer.parseInt(n[0]),Integer.parseInt(n[1]));}
        p.displayClientMessage(Component.translatable(result?"message.ng_fahrzeuge.management_ok":"message.ng_fahrzeuge.management_failed"),true);
        return result;
    }
    private VehicleManagement(){}
}
