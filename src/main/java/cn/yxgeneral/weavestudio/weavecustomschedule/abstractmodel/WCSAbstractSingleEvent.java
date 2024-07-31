package cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel;

import cn.yxgeneral.weavestudio.weavecustomschedule.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class WCSAbstractSingleEvent {
    WCSAbstractEventContainer ParentContainer;
    String EventID;
    String EventName = "Untitled Event";
    String EventDescription = "No Description";
    boolean Rule_Immediate = false;
    List<String> ConsoleCommands = new ArrayList<>();
    List<String> PlayerCommands = new ArrayList<>();
    List<String> Broadcasts = new ArrayList<>();
    int LastConsoleCommandIndex = -1;
    int LastPlayerCommandIndex = -1;
    boolean BroadcastExecuted = false;
    public WCSAbstractSingleEvent(WCSAbstractEventContainer parentContainer, String eventID, ConfigurationSection config){
        ParentContainer = parentContainer;
        EventID = eventID;
        initFromConfig(config);
    }
    private void initFromConfig(ConfigurationSection config){
        EventName = config.getString("name");
        EventName = EventName==null?"Untitled Event":EventName;
        EventDescription = config.getString("description");
        EventDescription = EventDescription==null?"No Description":EventDescription;
        ConsoleCommands = config.getStringList("command.console");
        PlayerCommands = config.getStringList("command.player");
        Broadcasts = config.getStringList("broadcast");
        Rule_Immediate = config.getBoolean("immediate");
    }
    public void onTick(double percent) {
        if (!BroadcastExecuted) {
            BroadcastExecuted = true;
            executeBroadcast();
        }
        if (Rule_Immediate) {
            if (LastConsoleCommandIndex==-1) {
                LastConsoleCommandIndex = ConsoleCommands.size() - 1;
                for (String command : ConsoleCommands) {
                    WCSInteractExecutor.consoleExecuteCommand(applyPlaceHolder(command));
                    WCSInteractExecutor.gInfo(applyPlaceHolder(command));
                }
            }
            if (LastPlayerCommandIndex==-1) {
                LastPlayerCommandIndex = PlayerCommands.size() - 1;
                for (String command : PlayerCommands) {
                    executePlayerCommand(command);
                }

            }
        } else {
            if (!ConsoleCommands.isEmpty()) {
                int CurrentConsoleCommandIndex;
                if (percent < 1) {
                    CurrentConsoleCommandIndex = ((Double) (percent * (ConsoleCommands.size() - 1))).intValue();
                } else {
                    CurrentConsoleCommandIndex = ConsoleCommands.size() - 1;
                }
                if (CurrentConsoleCommandIndex > LastConsoleCommandIndex) {
                    for (int i = LastConsoleCommandIndex + 1; i <= CurrentConsoleCommandIndex; i++) {
                        WCSInteractExecutor.consoleExecuteCommand(applyPlaceHolder(ConsoleCommands.get(i)));
                    }
                    LastConsoleCommandIndex = CurrentConsoleCommandIndex;
                }
            }
            if (!PlayerCommands.isEmpty()) {
                int CurrentPlayerCommandIndex;
                if (percent < 1) {
                    CurrentPlayerCommandIndex = ((Double) (percent * (PlayerCommands.size() - 1))).intValue();
                } else {
                    CurrentPlayerCommandIndex = PlayerCommands.size() - 1;
                }
                if (CurrentPlayerCommandIndex > LastPlayerCommandIndex) {
                    for (int i = LastPlayerCommandIndex + 1; i <= CurrentPlayerCommandIndex; i++) {
                        executePlayerCommand(PlayerCommands.get(i));
                    }
                    LastPlayerCommandIndex = CurrentPlayerCommandIndex;
                }
            }
        }
        if (percent >= 1.0) {
            LastConsoleCommandIndex = -1;
            LastPlayerCommandIndex = -1;
            BroadcastExecuted = false;
        }
    }
    abstract public boolean checkPermission(Player player);
    private void executePlayerCommand(String command){
        for(Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()){
            if (checkPermission(player)){
                WCSInteractExecutor.playerExecuteCommand(player, applyPlaceHolder(command));
            }
        }
    }
    private void executeBroadcast(){
        if (WCSConfigManager.getBroadcastMode().equals("vanilla")){
            for (String broadcast : Broadcasts){
                WCSInteractExecutor.vanillaBroadcast(applyPlaceHolder(broadcast));
            }
        } else {
            for (Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()){
                if (checkPermission(player)){
                    for (String broadcast : Broadcasts){
                        if (WCSConfigManager.isBroadcastWCSModeWithPrefix()){
                            WCSInteractExecutor.sendPrefixMessage(player, applyPlaceHolder(broadcast));
                        } else {
                            WCSInteractExecutor.sendNormalMessage(player, applyPlaceHolder(broadcast));
                        }
                    }
                }
            }
        }
    }
    protected String applyPlaceHolder(String str){
        return ParentContainer.applyPlaceHolder(str).replace("{event_id}", EventID)
                .replace("{event_name}", EventName)
                .replace("{event_description}", EventDescription)
                .replace("{month}", WCSTimer.getMonth().toString())
                .replace("{week_day}", WCSTimer.getWeekDay().toString())
                .replace("{month_day}", String.valueOf(WCSTimer.getDayOfMonth()))
                .replace("{hour}", String.valueOf(WCSTimer.getHour()))
                .replace("{minute}", String.valueOf(WCSTimer.getMinute()));
    }
    public WCSAbstractEventContainer getParentContainer() {
        return ParentContainer;
    }

    public String getEventID() {
        return EventID;
    }

    public String getEventName() {
        return EventName;
    }

    public String getEventDescription() {
        return EventDescription;
    }
}
