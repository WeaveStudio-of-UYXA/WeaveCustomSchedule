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
    // Enable/Disable
    public static Boolean canEnableAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.enable.*") || isAdmin(p);
    }
    public static Boolean canEnableCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.enable." + countdownCallID) || canEnableAnyCountdown(p);
    }
    // Start/Pause/Stop
    public static Boolean canToggleAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.toggle.*") || isAdmin(p);
    }
    public static Boolean canToggleCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.toggle." + countdownCallID) || canToggleAnyCountdown(p);
    }
    // Means commands.player should be executed by the player
    public static Boolean beConsideredByAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.consider.*") || isAdmin(p);
    }
    public static Boolean beConsideredByCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.consider." + countdownCallID) || beConsideredByAnyCountdown(p);
    }
    public static Boolean canEnableAnySchedule(Player p){
        return p.hasPermission("weavecustomschedule.schedule.enable.*") || isAdmin(p);
    }
    public static Boolean canEnableSchedule(Player p, String scheduleID){
        return p.hasPermission("weavecustomschedule.schedule.enable." + scheduleID) || canEnableAnySchedule(p);
    }
    public static Boolean beConsideredByAnySchedule(Player p){
        return p.hasPermission("weavecustomschedule.schedule.consider.*") || isAdmin(p);
    }
    public static Boolean beConsideredBySchedule(Player p, String scheduleID){
        return p.hasPermission("weavecustomschedule.schedule.consider." + scheduleID) || beConsideredByAnySchedule(p);
    }
    public static Boolean getDashboard(Player p){
        return p.hasPermission("weavecustomschedule.dashboard") || isAdmin(p);
    }
}
