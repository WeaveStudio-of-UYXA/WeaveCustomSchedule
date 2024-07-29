package cn.yxgeneral.weavestudio.weavecustomschedule;

import java.time.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WCSTimer {
    private static BukkitTask RunningTimer = null;
    private static WCSTickLoop TimerTask = null;
    private static Integer ConfigReloadConfirmTick = 0;
    public static void start(){
        TimerTask = new WCSTickLoop();
        TimerTask.LastNow = LocalDateTime.now();
        RunningTimer = TimerTask.runTaskTimer(
                WeaveCustomSchedule.getInstance(),  0, 1
        );
    }
    public static void stop(){
        RunningTimer.cancel();
    }
    public static void startReloadTimer(){
        ConfigReloadConfirmTick = 1;
    }
    public static boolean checkConfigReloadable(){
        boolean rtn = ConfigReloadConfirmTick > 0 && ConfigReloadConfirmTick < 1200;
        ConfigReloadConfirmTick = 0;
        return rtn;
    }
    private static class WCSTickLoop extends BukkitRunnable{
        public LocalDateTime LastNow;
        @Override
        public void run() {
            if (ConfigReloadConfirmTick!=0){
               ConfigReloadConfirmTick++;
               if (ConfigReloadConfirmTick >= 1200){
                   ConfigReloadConfirmTick = 0;
               }
            }
            LocalDateTime now = LocalDateTime.now();
            if(now.getMinute() == LastNow.getMinute()){
                double DurationPercent = now.getSecond() / 60.0;
                WCSTableManager.onTick(
                        now.getMonth(), now.getDayOfWeek(), now.getDayOfMonth(),
                        now.getHour(), now.getMinute(), DurationPercent
                );
            }else{
                WCSTableManager.onTick(
                        LastNow.getMonth(), LastNow.getDayOfWeek(), LastNow.getDayOfMonth(),
                        LastNow.getHour(), LastNow.getMinute(), 1.0
                );
                LastNow = LastNow.plusMinutes(1); //
            }
        }
    }
}
