package cn.yxgeneral.weavestudio.weavecustomschedule;

import java.io.File;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WCSTableManager {
    private static ArrayList<WCSScheduleObject> CustomSchedules = new ArrayList<>();
    private static Map<String, WCSScheduleObject> CustomSchedulesMap = new HashMap<>();
    private static ArrayList<WCSCountdownObject> CustomCountdowns = new ArrayList<>();
    private static Map<String, WCSCountdownObject> CustomCountdownsMap = new HashMap<>();
    private static Map<String, Integer> CustomCountdownsTotalCountMap = new HashMap<>();
    private static Map<String, Integer> CustomCountdownsCurrentIndexMap = new HashMap<>();
    protected static void loadCustomSchedules(){
        CustomSchedulesMap.clear();
        CustomSchedules.clear();
        //get all the .yml or .yaml filenames in the schedules folder, use java method
        File ScheduleFolder = new File(WeaveCustomSchedule.getInstance().getDataFolder(), "schedules");
        File[] ScheduleFiles = ScheduleFolder.listFiles(File::isFile);
        if (ScheduleFiles == null){
            return;
        }
        for (File ScheduleFile : ScheduleFiles){
            if (!ScheduleFile.getName().endsWith(".yml") && !ScheduleFile.getName().endsWith(".yaml")){
                continue;
            }
            WCSScheduleObject obj = new WCSScheduleObject(ScheduleFile.getName());
            String id = ScheduleFile.getName().substring(0, ScheduleFile.getName().lastIndexOf("."));
            CustomSchedules.add(obj);
            CustomSchedulesMap.put(id, obj);
        }
    }
    protected static void loadCustomCountdowns(Boolean forceClearCounter){
        CustomCountdownsMap.clear();
        CustomCountdowns.clear();
        if (forceClearCounter){
            CustomCountdownsTotalCountMap.clear();
            CustomCountdownsCurrentIndexMap.clear();
        }
        //get all the .yml or .yaml filenames in the countdowns folder, use java method
        File CountdownFolder = new File(WeaveCustomSchedule.getInstance().getDataFolder(), "countdowns");
        File[] CountdownFiles = CountdownFolder.listFiles(File::isFile);
        if (CountdownFiles == null){
            return;
        }
        for (File CountdownFile : CountdownFiles){
            if (!CountdownFile.getName().endsWith(".yml") && !CountdownFile.getName().endsWith(".yaml")){
                continue;
            }
            WCSCountdownObject obj = new WCSCountdownObject(CountdownFile.getName());
            CustomCountdowns.add(obj);
            CustomCountdownsMap.put(obj.getCallID(), obj);
            if (!CustomCountdownsTotalCountMap.containsKey(obj.getCallID())) {
                CustomCountdownsTotalCountMap.put(obj.getCallID(), 0);
                CustomCountdownsCurrentIndexMap.put(obj.getCallID(), 0);
            }
        }
    }
    public static void updateCountdownAvailablePlayers(){
        for (WCSCountdownObject countdown : CustomCountdowns){
            countdown.updateAvailablePlayer();
        }
    }
    public static void setScheduleEnable(String id, Boolean enable){
        if (CustomSchedulesMap.containsKey(id)){
            CustomSchedulesMap.get(id).setEnable(enable);
        }else{
            WeaveCustomSchedule.warning("No such schedule id: " + id);
        }
    }
    public static void setCountdownEnable(String id, Boolean enable){
        if (CustomCountdownsMap.containsKey(id)){
            CustomCountdownsMap.get(id).setEnable(enable);
        }else{
            WeaveCustomSchedule.warning("No such countdown id: " + id);
        }
    }

    protected static void countdownCurrentTotalRecord(String countdownCallID){
        if (CustomCountdownsTotalCountMap.containsKey(countdownCallID)){
            CustomCountdownsTotalCountMap.put(countdownCallID, CustomCountdownsTotalCountMap.get(countdownCallID) + 1);
        }
    }
    protected static Integer getCountdownCurrentTotal(String countdownCallID){
        if (CustomCountdownsTotalCountMap.containsKey(countdownCallID)){
            return CustomCountdownsTotalCountMap.get(countdownCallID);
        }
        return 0;
    }
    protected static void countdownResetCurrentIndexRecord(String countdownCallID){
        if (CustomCountdownsCurrentIndexMap.containsKey(countdownCallID)){
            CustomCountdownsCurrentIndexMap.put(countdownCallID, 0);
        }
    }
    protected static void countdownCurrentIndexRecord(String countdownCallID){
        if (CustomCountdownsCurrentIndexMap.containsKey(countdownCallID)){
            CustomCountdownsCurrentIndexMap.put(countdownCallID, CustomCountdownsCurrentIndexMap.get(countdownCallID) + 1);
        }
    }
    protected static Integer getCountdownCurrentIndex(String countdownCallID){
        if (CustomCountdownsCurrentIndexMap.containsKey(countdownCallID)){
            return CustomCountdownsCurrentIndexMap.get(countdownCallID);
        }
        return 0;
    }
    protected static void onTick(Month month, DayOfWeek week_day, Integer month_day,
                                 Integer hour, Integer minute, Double percent){
        for (WCSScheduleObject schedule : CustomSchedules){
            if (schedule.isEnable()){
                schedule.onTick(month, week_day, month_day, hour, minute, percent);
            }
        }
        for (WCSCountdownObject countdown : CustomCountdowns){
            if (countdown.isEnable()){
                countdown.onTick();
            }
        }
    }
}
