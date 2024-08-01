package cn.yxgeneral.weavestudio.weavecustomschedule;

import cn.yxgeneral.weavestudio.weavecustomschedule.container.countdown.WCSCountdownEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.container.countdown.WCSCountdownEventContainerData;
import cn.yxgeneral.weavestudio.weavecustomschedule.container.schedule.WCSScheduleEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.container.tps.WCSTpsEventContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WCSContainerManager {
    private static Map<String, WCSScheduleEventContainer> ScheduleContainers = new HashMap<>();
    private static List<WCSScheduleEventContainer> ScheduleContainersList = new ArrayList<>();
    private static Map<String, WCSCountdownEventContainer> CountdownContainers = new HashMap<>();
    private static List<WCSCountdownEventContainer> CountdownContainersList = new ArrayList<>();
    private static Map<String, WCSTpsEventContainer> TpsContainers = new HashMap<>();
    private static List<WCSTpsEventContainer> TpsContainersList = new ArrayList<>();
    protected static void loadScheduleContainers(){ //refactor of loadCustomSchedules
        ScheduleContainers.clear();
        ScheduleContainersList.clear();
        //get all the .yml or .yaml filenames in the schedules folder, use java method
        File ScheduleFolder = new File(WeaveCustomSchedule.getInstance().getDataFolder(), "schedule");
        File[] ScheduleFiles = ScheduleFolder.listFiles(File::isFile);
        if (ScheduleFiles == null){
            return;
        }
        for (File ScheduleFile : ScheduleFiles){
            if (!ScheduleFile.getName().endsWith(".yml") && !ScheduleFile.getName().endsWith(".yaml")){
                continue;
            }
            WCSScheduleEventContainer container = new WCSScheduleEventContainer();
            if(container.initFromConfig("schedule/"+ScheduleFile.getName())) {
                if (ScheduleContainers.containsKey(container.getContainerID())){
                    WeaveCustomSchedule.warning("Duplicate schedule container id: " + container.getContainerID());
                }else {
                    ScheduleContainers.put(container.getContainerID(), container);
                    ScheduleContainersList.add(container);
                }
            }
        }
    }
    protected static void loadCountdownContainers(Boolean forceClearCounter){
        for (WCSCountdownEventContainer container : CountdownContainersList){
            container.stop();
        }
        Map<String, WCSCountdownEventContainerData> dataCache = new HashMap<>();
        if (!forceClearCounter){
            for (WCSCountdownEventContainer container : CountdownContainersList) {
                dataCache.put(container.getContainerID(), container.getContainerData());
            }
        }
        CountdownContainers.clear();
        CountdownContainersList.clear();
        //get all the .yml or .yaml filenames in the countdowns folder, use java method
        File CountdownFolder = new File(WeaveCustomSchedule.getInstance().getDataFolder(), "countdown");
        File[] CountdownFiles = CountdownFolder.listFiles(File::isFile);
        if (CountdownFiles == null){
            return;
        }
        for (File CountdownFile : CountdownFiles){
            if (!CountdownFile.getName().endsWith(".yml") && !CountdownFile.getName().endsWith(".yaml")){
                continue;
            }
            WCSCountdownEventContainer obj = new WCSCountdownEventContainer();
            if (obj.initFromConfig("countdown/"+CountdownFile.getName())) {
                if (CountdownContainers.containsKey(obj.getContainerID())){
                    WeaveCustomSchedule.warning("Duplicate countdown callID: " + obj.getContainerID());
                }else {
                    if (dataCache.containsKey(obj.getContainerID())){
                        obj.setContainerData(dataCache.get(obj.getContainerID()));
                    }else{
                        obj.setContainerData(new WCSCountdownEventContainerData());
                    }
                    CountdownContainers.put(obj.getContainerID(), obj);
                    CountdownContainersList.add(obj);
                }
            }
        }
    }
    public static void loadTpsContainers(){
        TpsContainers.clear();
        TpsContainersList.clear();
        //get all the .yml or .yaml filenames in the tps folder, use java method
        File TpsFolder = new File(WeaveCustomSchedule.getInstance().getDataFolder(), "tps");
        File[] TpsFiles = TpsFolder.listFiles(File::isFile);
        if (TpsFiles == null){
            return;
        }
        for (File TpsFile : TpsFiles){
            if (!TpsFile.getName().endsWith(".yml") && !TpsFile.getName().endsWith(".yaml")){
                continue;
            }
            WCSTpsEventContainer container = new WCSTpsEventContainer();
            if(container.initFromConfig("tps/"+TpsFile.getName())) {
                if (TpsContainers.containsKey(container.getContainerID())){
                    WeaveCustomSchedule.warning("Duplicate tps container id: " + container.getContainerID());
                }else {
                    TpsContainers.put(container.getContainerID(), container);
                    TpsContainersList.add(container);
                }
            }
        }
    }
    protected static void updateCountdownAvailablePlayers(){
        for (WCSCountdownEventContainer container : CountdownContainersList){
            container.updateAvailablePlayer();
        }
    }
    public static boolean isCountdownIDExist(String id){
        return CountdownContainers.containsKey(id);
    }
    public static boolean isScheduleIDExist(String id){
        return ScheduleContainers.containsKey(id);
    }
    public static boolean isTpsIDExist(String id){
        return TpsContainers.containsKey(id);
    }
    public static void setScheduleEnable(String id, Boolean enable){
        if (ScheduleContainers.containsKey(id)){
            ScheduleContainers.get(id).setEnable(enable);
        }else{
            WeaveCustomSchedule.warning("No such schedule id: " + id);
        }
    }
    public static void setCountdownEnable(String id, Boolean enable){
        if (CountdownContainers.containsKey(id)){
            CountdownContainers.get(id).setEnable(enable);
        }else{
            WeaveCustomSchedule.warning("No such countdown id: " + id);
        }
    }
    public static void setTpsEnable(String id, Boolean enable){
        if (TpsContainers.containsKey(id)){
            TpsContainers.get(id).setEnable(enable);
        }else{
            WeaveCustomSchedule.warning("No such tps id: " + id);
        }
    }
    public static void setCountdownRunning(String id, Boolean running){
        if (CountdownContainers.containsKey(id)){
            if (running){
                CountdownContainers.get(id).start();
            }else{
                CountdownContainers.get(id).stop();
            }
        }else{
            WeaveCustomSchedule.warning("No such countdown id: " + id);
        }
    }
    public static void pauseCountdown(String id){
        if (CountdownContainers.containsKey(id)){
            CountdownContainers.get(id).pause();
        }else{
            WeaveCustomSchedule.warning("No such countdown id: " + id);
        }
    }
    public static void printCountdownStatus(){
        for (String id : CountdownContainers.keySet()){
            WCSInteractExecutor.gInfo(id + " " + CountdownContainers.get(id).getContainerName());
        }
    }
    public static void printScheduleStatus(){
        for (String id : ScheduleContainers.keySet()){
            WCSInteractExecutor.gInfo(id + " " + ScheduleContainers.get(id).getContainerName());
        }
    }
    public static List<String> getScheduleContainerIDList(){
        return new ArrayList<>(ScheduleContainers.keySet());
    }
    public static List<String> getCountdownContainerIDList(){
        return new ArrayList<>(CountdownContainers.keySet());
    }
    public static List<String> getTpsContainerIDList(){
        return new ArrayList<>(TpsContainers.keySet());
    }
    protected static void onTick(Double percent){
        for (WCSCountdownEventContainer container : CountdownContainersList){
            if (container.isEnable()){
                container.onTick(percent);
            }
        }
        for (WCSScheduleEventContainer container : ScheduleContainersList){
            if (container.isEnable()){
                container.onTick(percent);
            }
        }
        for (WCSTpsEventContainer container : TpsContainersList){
            if (container.isEnable()){
                container.onTick(percent);
            }
        }
    }
}
