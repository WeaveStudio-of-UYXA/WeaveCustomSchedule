package cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel;

import cn.yxgeneral.weavestudio.weavecustomschedule.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class WCSAbstractEventContainer {
    public enum ContainerType{
        Unknown,
        Schedule,
        Countdown,
        Tps,
        PlayerCount,
    }
    ContainerType Type;
    String ContainerID = "Uninitialized Container";
    String ContainerName = "Untitled Container";
    String ContainerDescription = "No Description";
    boolean Enable = false;
    List<WCSAbstractSingleEvent> Events = new ArrayList<>();
    YamlConfiguration ConfigFile;
    public WCSAbstractEventContainer(ContainerType type){
        Type = type;
    }
    public boolean initFromConfig(String configFilePath){
        try {
            ConfigFile = YamlConfiguration.loadConfiguration(
                    new File(WeaveCustomSchedule.getInstance().getDataFolder(), configFilePath)
            );
            ContainerID = ConfigFile.getString("id");
            ContainerName = ConfigFile.getString("name");
            ContainerDescription = ConfigFile.getString("description");
            Enable = ConfigFile.getBoolean("enable");
            Set<String> eventKeys = ConfigFile.getConfigurationSection("events").getKeys(false);
            for (String eventID : eventKeys) {
                Events.add(onConfigLoadingEvent(eventID, ConfigFile.getConfigurationSection("events." + eventID)));
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
    protected abstract WCSAbstractSingleEvent onConfigLoadingEvent(String eventID, ConfigurationSection config);
    public abstract void onTick(double percent);
    public String applyPlaceHolder(String str){
        return str.replace("{container_id}", ContainerID)
                .replace("{container_name}", ContainerName)
                .replace("{container_description}", ContainerDescription)
                .replace("{container_type}", WCSConfigManager.getTranslation("container.type."+Type.toString()));
    }
    public String getContainerID(){
        return this.ContainerID;
    }
    public String getContainerName(){
        return this.ContainerName;
    }
    public String getContainerDescription(){
        return this.ContainerDescription;
    }
    public boolean isEnable(){
        return this.Enable;
    }
    public void setEnable(boolean enable){
        this.Enable = enable;
        if (this.Enable){
            WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("container.enable")));
        }else{
            WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("container.disable")));
        }
    }
    public YamlConfiguration getConfigFile(){
        return this.ConfigFile;
    }
    public List<WCSAbstractSingleEvent> getEvents(){
        return this.Events;
    }
    public int getEventSize(){
        return this.Events.size();
    }
}
