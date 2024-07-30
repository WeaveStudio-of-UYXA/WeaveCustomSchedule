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
        WCSTickLoop.LastNow = LocalDateTime.now();
        //get local time utc
        WCSTickLoop.UTC = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
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
    public static class WCSTickLoop extends BukkitRunnable{
        private static LocalDateTime LastNow;
        private static ZoneOffset UTC = ZoneOffset.UTC;
        public static LocalDateTime getLastNow(){
            return LastNow;
        }
        @Override
        public void run() {
            if (ConfigReloadConfirmTick!=0){
               ConfigReloadConfirmTick++;
               if (ConfigReloadConfirmTick >= 1200){
                   ConfigReloadConfirmTick = 0;
               }
            }
            LocalDateTime now = LocalDateTime.ofEpochSecond(
                    System.currentTimeMillis()/1000, 0, UTC
            );
            if(now.getMinute() == LastNow.getMinute()){
                double DurationPercent = now.getSecond() / 60.0;
                WCSContainerManager.onTick(DurationPercent);
            }else{
                WCSContainerManager.onTick(1.0);
                LastNow = LastNow.plusMinutes(1); //
            }
        }
    }
}
