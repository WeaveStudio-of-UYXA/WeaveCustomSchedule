package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.entity.Player;

public class WCSPermission {
    public static boolean isAdmin(Player p){
        return p.hasPermission("weavecustomschedule.admin");
    }
    public static boolean receiveAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.receive.*") || isAdmin(p);
    }
    public static boolean receiveCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.receive." + countdownCallID) || receiveAnyCountdown(p);
    }
    // Enable/Disable
    public static boolean canEnableAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.enable.*") || isAdmin(p);
    }
    public static boolean canEnableCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.enable." + countdownCallID) || canEnableAnyCountdown(p);
    }
    // Start/Pause/Stop
    public static boolean canToggleAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.toggle.*") || isAdmin(p);
    }
    public static boolean canToggleCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.toggle." + countdownCallID) || canToggleAnyCountdown(p);
    }
    // Means commands.player should be executed by the player
    public static boolean beConsideredByAnyCountdown(Player p){
        return p.hasPermission("weavecustomschedule.countdown.consider.*") || isAdmin(p);
    }
    public static boolean beConsideredByCountdown(Player p, String countdownCallID){
        return p.hasPermission("weavecustomschedule.countdown.consider." + countdownCallID) || beConsideredByAnyCountdown(p);
    }
    public static boolean canEnableAnySchedule(Player p){
        return p.hasPermission("weavecustomschedule.schedule.enable.*") || isAdmin(p);
    }
    public static boolean canEnableSchedule(Player p, String scheduleID){
        return p.hasPermission("weavecustomschedule.schedule.enable." + scheduleID) || canEnableAnySchedule(p);
    }
    public static boolean beConsideredByAnySchedule(Player p){
        return p.hasPermission("weavecustomschedule.schedule.consider.*") || isAdmin(p);
    }
    public static boolean beConsideredBySchedule(Player p, String scheduleID){
        return p.hasPermission("weavecustomschedule.schedule.consider." + scheduleID) || beConsideredByAnySchedule(p);
    }
    public static boolean canEnableAnyTps(Player p){
        return p.hasPermission("weavecustomschedule.tps.enable.*") || isAdmin(p);
    }
    public static boolean canEnableTps(Player p, String tpsID){
        return p.hasPermission("weavecustomschedule.tps.enable." + tpsID) || canEnableAnyTps(p);
    }
    public static boolean beConsideredByAnyTps(Player p){
        return p.hasPermission("weavecustomschedule.tps.consider.*") || isAdmin(p);
    }
    public static boolean beConsideredByTps(Player p, String tpsID){
        return p.hasPermission("weavecustomschedule.tps.consider." + tpsID) || beConsideredByAnyTps(p);
    }
    public static boolean getDashboard(Player p){
        return p.hasPermission("weavecustomschedule.dashboard") || isAdmin(p);
    }
}
