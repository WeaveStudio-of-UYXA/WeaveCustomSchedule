package cn.yxgeneral.weavestudio.weavecustomschedule;

import java.time.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WCSTimer {
    private static BukkitTask RunningTimer = null;
    private static WCSTickLoop TimerTask = null;
    private static Integer ConfigReloadConfirmTick = 0;
    private static LocalDateTime LastNow;
    private static ZoneOffset UTC = ZoneOffset.UTC;
    private static long LastUnixEpoch = 0;
    private static int msPerTick = 50;
    private static Month Month;
    private static DayOfWeek WeekDay;
    private static int DayOfMonth;
    private static int Hour;
    private static int Minute;
    public static void start(){
        TimerTask = new WCSTickLoop();
        LastNow = LocalDateTime.now();
        updateLastNowDerived();
        UTC = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
        RunningTimer = TimerTask.runTaskTimer(
                WeaveCustomSchedule.getInstance(),  0, 1
        );
    }
    public static BukkitTask getRunningTimer(){
        return RunningTimer;
    }
    public static void startReloadTimer(){
        ConfigReloadConfirmTick = 1;
    }
    public static boolean checkConfigReloadable(){
        boolean rtn = ConfigReloadConfirmTick > 0 && ConfigReloadConfirmTick < 1200;
        ConfigReloadConfirmTick = 0;
        return rtn;
    }
    public static LocalDateTime getLastNow(){
        return LastNow;
    }
    protected static void LastNowPlusMinutes(int minutes) {
        LastNow = LastNow.plusMinutes(minutes);
        updateLastNowDerived();
    }
    protected static void updateLastNowDerived(){
        Month = LastNow.getMonth();
        WeekDay = LastNow.getDayOfWeek();
        DayOfMonth = LastNow.getDayOfMonth();
        Hour = LastNow.getHour();
        Minute = LastNow.getMinute();
    }
    public static int getMsPerTick(){
        return msPerTick;
    }
    public static java.time.Month getMonth() {
        return Month;
    }
    public static DayOfWeek getWeekDay() {
        return WeekDay;
    }
    public static int getDayOfMonth() {
        return DayOfMonth;
    }
    public static int getHour() {
        return Hour;
    }
    public static int getMinute() {
        return Minute;
    }
    public static class WCSTickLoop extends BukkitRunnable{
        @Override
        public final void run() {
            if (ConfigReloadConfirmTick!=0){
               ConfigReloadConfirmTick++;
               if (ConfigReloadConfirmTick >= 1200){
                   ConfigReloadConfirmTick = 0;
               }
            }
            long UnixEpoch = System.currentTimeMillis();
            msPerTick = (int) (UnixEpoch - LastUnixEpoch);
            LastUnixEpoch = UnixEpoch;
            LocalDateTime now = LocalDateTime.ofEpochSecond(
                    UnixEpoch/1000, 0, UTC
            );
            if(now.getMinute() == getMinute()){
                double DurationPercent = now.getSecond() / 60.0;
                WCSContainerManager.onTick(DurationPercent);
            }else{
                WCSContainerManager.onTick(1.0);
                LastNowPlusMinutes(1);
            }
        }
    }
}
