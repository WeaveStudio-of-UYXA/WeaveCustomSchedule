package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.Set;

public class WCSScheduleObject {
    private final String ConfigFileName;
    private String CallID;
    private String Name;
    private String Description;
    private Boolean Enable;
    private ArrayList<WCSSingleScheduleEvent> Events;
    public WCSScheduleObject(String configFileName) {
        ConfigFileName = configFileName;
    }
    public boolean loadConfig(){
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(
                    new File(WeaveCustomSchedule.getInstance().getDataFolder(), "schedules/" + ConfigFileName)
            );
            Name = yaml.getString("name");
            CallID = yaml.getString("callID");
            Description = yaml.getString("description");
            Enable = yaml.getBoolean("enable");
            Set<String> eventKeys = yaml.getConfigurationSection("events").getKeys(false);
            Events = new ArrayList<>();
            for (String eventKey : eventKeys) {
                Events.add(new WCSSingleScheduleEvent(CallID, Name, eventKey, yaml.getConfigurationSection("events." + eventKey)));
            }
            WCSInteractExecutor.gInfo(WCSUtils.applyAll(
                    WCSConfigManager.getTranslation("schedule.loaded").replace("#s", Name), null));
            return true;
        }catch (Exception e){
            WCSInteractExecutor.gWarning(WCSUtils.applyAll(
                    WCSConfigManager.getTranslation("schedule.loadFailed").replace("#s", ConfigFileName), null));
            return false;
        }
    }
    public String getName(){
        return Name;
    }
    public String getCallID(){
        return CallID;
    }
    public String getDescription(){
        return Description;
    }
    public void setEnable(Boolean enable){
        Enable = enable;
    }
    public Boolean isEnable(){
        return Enable;
    }
    protected void onTick(Month month, DayOfWeek week_day, Integer month_day,
                          Integer hour, Integer minute, Double percent){
        for (WCSSingleScheduleEvent event : Events){
            event.onTick(month, week_day, month_day, hour, minute, percent);
        }
    }
}
