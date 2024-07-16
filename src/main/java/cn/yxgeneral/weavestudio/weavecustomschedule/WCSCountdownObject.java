package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class WCSCountdownObject {
    private String EventName;
    private String EventCallID;
    private String Description;
    private Boolean StartOnLoad = false;
    private Boolean Enable = false;
    private Integer CurrentTick = 0;
    private Integer Interval;
    private Integer TotalCount;
    private List<String> onStartCommands;
    private List<String> onStartBroadcasts;
    private Integer CurrentEventIndex = 0;
    private List<WCSSingleCountdownEvent> Events;
    private WCSCountdownNotifier Notifier;
    public WCSCountdownObject(String configFileName){
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(
                new File(WeaveCustomSchedule.getInstance().getDataFolder(), "schedules/"+configFileName)
        );
        EventName = yaml.getString("name");
        Description = yaml.getString("description");
        Enable = yaml.getBoolean("enable");
        StartOnLoad = yaml.getBoolean("startOnLoad");
        Interval = yaml.getInt("interval") * 20; // NOTICE: 20 ticks = 1 second
        TotalCount = yaml.getInt("total");
        String NoticeMode = yaml.getString("notice.mode");
        List<String> noticeColorBar = yaml.getStringList("notice.colorBar");
        String bossbarModeText = yaml.getString("notice.bossbarModeText");
        Map<Integer, String> broadcastAndTitleModeText = new HashMap<>();
        Set<String> broadcastAndTitleModeTextKeys = yaml.getConfigurationSection("notice.broadcastAndTitleModeText").getKeys(false);
        for (String key : broadcastAndTitleModeTextKeys){
            broadcastAndTitleModeText.put(Integer.parseInt(key), yaml.getString("notice.broadcastAndTitleModeText."+key));
        }

        Notifier = new WCSCountdownNotifier(EventName, noticeColorBar);
        Notifier.setNoticeMode(NoticeMode);
        Notifier.setBossbarModeText(bossbarModeText);
        Notifier.setBroadcastAndTitleModeText(broadcastAndTitleModeText);

        onStartCommands = yaml.getStringList("onStart.commands");
        onStartBroadcasts = yaml.getStringList("onStart.broadcasts");
        Set<String> eventKeys = yaml.getConfigurationSection("events").getKeys(false);
        Events = new ArrayList<>();
        for (String eventKey : eventKeys){
            Events.add(new WCSSingleCountdownEvent(eventKey, yaml.getConfigurationSection("events."+eventKey)));
        }
        WeaveCustomSchedule.info(WCSConfigManager.getTranslation("countdown.loaded").replace("@c", EventName));
    }
    public Boolean isEnable(){
        return Enable;
    }
    public void setEnable(Boolean enable){
        Enable = enable;
    }
    public String getCallID(){
        return EventCallID;
    }
    public void updateAvailablePlayer(){
        Notifier.updateAvailablePlayer();
    }
    public void onTick(){
        CurrentTick += 1;
        if (CurrentTick==Interval){
            CurrentTick = 0;
            WCSTableManager.countdownCurrentIndexRecord(EventCallID);
            if (WCSTableManager.getCountdownCurrentIndex(EventCallID) >= Events.size()){
                WCSTableManager.countdownResetCurrentIndexRecord(EventCallID);
                WCSTableManager.countdownCurrentTotalRecord(EventCallID);
                if (WCSTableManager.getCountdownCurrentTotal(EventCallID) >= TotalCount){
                    setEnable(false);
                    return;
                }
            }
            Events.get(CurrentEventIndex).doEvent();
        }
    }
}
