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
        LocalDateTime now = LocalDateTime.now();
        TimerTask.StartEpoch = System.currentTimeMillis();
        TimerTask.Month = now.getMonth();
        TimerTask.Day_month = now.getDayOfMonth();
        TimerTask.Day_week = now.getDayOfWeek();
        TimerTask.Hour = now.getHour();
        TimerTask.Minute = now.getMinute();
        TimerTask.LastMinutes = TimerTask.Minute;
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
        public Long StartEpoch;
        public LocalDateTime Now;
        public Long Duration;
        public Double DurationPercent;
        public Long ActualMinutes = 60000L;
        public Month Month;
        public Integer Day_month;
        public DayOfWeek Day_week;
        public Integer Hour;
        public Integer Minute;
        public Integer LastMinutes;
        @Override
        public void run() {
            if (ConfigReloadConfirmTick!=0){
               ConfigReloadConfirmTick++;
               if (ConfigReloadConfirmTick >= 1200){
                   ConfigReloadConfirmTick = 0;
               }
            }
            Long currentEpoch = System.currentTimeMillis();
            Duration = currentEpoch - StartEpoch;
            DurationPercent = Duration.doubleValue() / ActualMinutes;
            if (Duration >= ActualMinutes) {
                DurationPercent = 1.0;
                WCSTableManager.onTick(
                        Month, Day_week, Day_month, Hour, LastMinutes, DurationPercent
                );
                ActualMinutes = 120000 - (currentEpoch - StartEpoch);
                ActualMinutes = ActualMinutes<0L ? 100L : ActualMinutes;
                ActualMinutes = ActualMinutes>60000L ? 60000L : ActualMinutes;
                StartEpoch = currentEpoch;
                Now = LocalDateTime.now();
                Minute = Now.getMinute();
                if (Minute == LastMinutes){
                    Now = Now.plusSeconds(60);
                    LastMinutes = Now.getMinute();
                }else{
                    LastMinutes = Minute;
                }
                Month = Now.getMonth();
                Day_month = Now.getDayOfMonth();
                Day_week = Now.getDayOfWeek();
                Hour = Now.getHour();
                Minute = Now.getMinute();

            }else{
                WCSTableManager.onTick(
                        Month, Day_week, Day_month, Hour, LastMinutes, DurationPercent
                );
            }
        }
    }
}
