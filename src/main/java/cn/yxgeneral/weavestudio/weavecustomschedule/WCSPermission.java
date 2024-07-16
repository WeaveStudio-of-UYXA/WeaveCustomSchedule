package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.entity.Player;

public class WCSPermission {
    public static Boolean isAdmin(Player p){
        return p.hasPermission("weavecustomschedule.admin");
    }
    public static Boolean receiveAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.receive.*") || isAdmin(p);
    }
    public static Boolean receiveCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.receive." + countdownCallID) || receiveAnyCountdown(p);
    }
    public static Boolean canEnableAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.enable.*") || isAdmin(p);
    }
    public static Boolean canEnableCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.enable." + countdownCallID) || canEnableAnyCountdown(p);
    }
}
