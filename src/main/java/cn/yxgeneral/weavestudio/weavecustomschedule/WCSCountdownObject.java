package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;


public class WCSCountdownObject {
    private final String ConfigFileName;
    private String CountdownName;
    private String CallID;
    private String Description;
    private Boolean StartOnLoad;
    private Boolean StartOnReload;
    private Boolean Enable;
    private Boolean IsRunning = false;
    private Integer CurrentTick = 0;
    private Integer Interval;
    private Integer TotalCount;
    private List<String> onStartConsoleCommands;
    private List<String> onStartPlayerCommands;
    private List<String> onStartBroadcasts;
    private Integer CurrentEventIndex = 0;
    private List<WCSSingleCountdownEvent> Events;
    private WCSCountdownNotifier Notifier;
    public WCSCountdownObject(String configFileName) {
        ConfigFileName = configFileName;
    }
    public boolean loadConfig(){
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(
                    new File(WeaveCustomSchedule.getInstance().getDataFolder(), "countdowns/" + ConfigFileName)
            );
            CountdownName = yaml.getString("name");
            CallID = yaml.getString("callID");
            Description = yaml.getString("description");
            Enable = yaml.getBoolean("enable");
            StartOnLoad = yaml.getBoolean("startOnLoad");
            StartOnReload = yaml.getBoolean("startOnReload");
            Interval = yaml.getInt("interval") * 20; // NOTICE: 20 ticks = 1 second
            TotalCount = yaml.getInt("total");
            String NoticeMode = yaml.getString("notice.mode");
            List<String> noticeColorBar = yaml.getStringList("notice.colorbar");
            String bossbarModeText = yaml.getString("notice.bossbarModeText");
            Map<Integer, String> broadcastAndTitleModeText = new HashMap<>();
            Set<String> broadcastAndTitleModeTextKeys =
                    yaml.getConfigurationSection("notice.broadcastAndTitleModeText").getKeys(false);
            for (String key : broadcastAndTitleModeTextKeys) {
                broadcastAndTitleModeText.put(Integer.parseInt(key), yaml.getString("notice.broadcastAndTitleModeText." + key));
            }

            Notifier = new WCSCountdownNotifier(this, noticeColorBar);
            Notifier.setNoticeMode(NoticeMode);
            Notifier.setBossbarModeText(bossbarModeText);
            Notifier.setBroadcastAndTitleModeText(broadcastAndTitleModeText);

            onStartConsoleCommands = yaml.getStringList("onStart.command.console");
            onStartPlayerCommands = yaml.getStringList("onStart.command.player");
            onStartBroadcasts = yaml.getStringList("onStart.broadcast");
            Set<String> eventKeys = yaml.getConfigurationSection("events").getKeys(false);
            Events = new ArrayList<>();
            for (String eventKey : eventKeys) {
                Events.add(new WCSSingleCountdownEvent(this, eventKey, yaml.getConfigurationSection("events." + eventKey)));
            }
            WCSInteractExecutor.gInfo(applyPlaceHolders(WCSConfigManager.getTranslation("countdown.loaded")));
            if (StartOnLoad && WeaveCustomSchedule.isJustStarted() || StartOnReload){
                start();
            }
            return true;
        }catch (Exception e){
            WCSInteractExecutor.gWarning(applyPlaceHolders(WCSConfigManager.getTranslation("countdown.loadFailed")));
            return false;
        }
    }
    public Boolean isEnable(){
        return Enable;
    }
    public void setEnable(Boolean enable){
        Enable = enable;
        if (!Enable){
            stop();
        }
    }
    public String getCallID(){
        return CallID;
    }
    public String getName(){
        return CountdownName;
    }
    public void updateAvailablePlayer(){
        Notifier.updateAvailablePlayer();
    }
    public void start(){
        if (!Enable || IsRunning){
            return;
        }
        IsRunning = true;
        onStart();
    }
    public void pause(){
        IsRunning = false;
    }
    public void stop(){
        Notifier.hideBossbar();
        IsRunning = false;
    }
    private void onStart(){
        for (String command : onStartConsoleCommands){
            WCSInteractExecutor.consoleExecuteCommand(applyPlaceHolders(command));
        }
        for (Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()){
            if (WCSPermission.beConsideredByCountdown(player, CallID)){
                for (String command : onStartPlayerCommands){
                    WCSInteractExecutor.playerExecuteCommand(player, applyPlaceHolders(command));
                }
            }
        }
        for (String broadcast : onStartBroadcasts){
            if (WCSConfigManager.getBroadcastMode().equals("vanilla")) {
                WCSInteractExecutor.vanillaBroadcast(applyPlaceHolders(broadcast));
            } else {
                for (Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()) {
                    if (WCSPermission.beConsideredByCountdown(player, CallID)) {
                        if (WCSConfigManager.isBroadcastWCSModeWithPrefix()) {
                            WCSInteractExecutor.sendPrefixMessage(player, applyPlaceHolders(broadcast));
                        } else {
                            WCSInteractExecutor.sendNormalMessage(player, applyPlaceHolders(broadcast));
                        }
                    }
                }
            }
        }
        Notifier.showBossbar();
    }

    public void onTick(){
        if (!IsRunning){
            return;
        }
        CurrentTick += 1;
        int nextEventIndex = WCSTableManager.getCountdownCurrentIndex(CallID);
        Notifier.update(Events.get(nextEventIndex).getEventName(), CurrentTick, Interval);
        if (CurrentTick>=Interval){
            CurrentTick = 0;
            Events.get(WCSTableManager.getCountdownCurrentIndex(CallID)).doEvent();
            WCSTableManager.countdownCurrentIndexRecord(CallID);
            if (WCSTableManager.getCountdownCurrentIndex(CallID) >= Events.size()){
                WCSTableManager.countdownResetCurrentIndexRecord(CallID);
                if (TotalCount<=0){
                    return;
                }
                WCSTableManager.countdownCurrentTotalRecord(CallID);
                if (WCSTableManager.getCountdownCurrentTotal(CallID) >= TotalCount){
                    stop();
                }
            }
        }
    }
    public String applyPlaceHolders(String msg){
        return msg
                .replace("#tt", TotalCount.toString())
                .replace("#tr", ((Integer)(TotalCount-WCSTableManager.getCountdownCurrentTotal(CallID))).toString())
                .replace("#etn", ((Integer)Events.size()).toString())
                .replace("#eix", ((Integer)(WCSTableManager.getCountdownCurrentIndex(CallID)+1)).toString())
                .replace("#c", CountdownName)
                .replace("#e", Events.get(WCSTableManager.getCountdownCurrentIndex(CallID)).getEventName())
                .replace("#s", "%.2f".formatted((double)(Interval - CurrentTick) / 20))
                .replace("#i", Interval.toString())
                ;
    }
}
