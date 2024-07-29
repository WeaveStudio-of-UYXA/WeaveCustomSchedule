package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.regex.*;

public class WCSSingleScheduleEvent {
    private WCSScheduleObject ParentSchedule;
    private String EventName;
    private String EventID;
    private String Rule_HM;
    private String Rule_Day;
    private String Rule_Month;
    private Integer LastMinute = -1;
    private Boolean Rule_Immediate;
    private List<String> ScheduleConsoleCommands;
    private List<String> SchedulePlayerCommands;
    private List<String> ScheduleBroadcasts;
    private Integer LastConsoleCommandIndex = -1;
    private Integer LastPlayerCommandIndex = -1;
    private Boolean BroadcastExecuted = false;
    private Boolean NeedExecute = false;

    public WCSSingleScheduleEvent(WCSScheduleObject parentSchedule,String eventId, ConfigurationSection config){
        ParentSchedule = parentSchedule;
        EventID = eventId;
        initFromConfig(config);
    }
    public void initFromConfig(ConfigurationSection config){
        EventName = config.getString("name");
        Rule_Day = config.getString("rule.day");
        Rule_HM = config.getString("rule.HHMM");
        Rule_Month = config.getString("rule.month");
        Rule_Immediate = config.getBoolean("rule.immediate");
        ScheduleConsoleCommands = config.getStringList("command.console");
        SchedulePlayerCommands = config.getStringList("command.player");
        ScheduleBroadcasts = config.getStringList("broadcast");
    }
    public String getEventName(){
        return EventName;
    }
    public String getEventID(){
        return EventID;
    }
    public Boolean onTick(Month month, DayOfWeek week_day, Integer month_day,
                             Integer hour, Integer minute, Double percent){
        if (needExecute(month, week_day, month_day, hour, minute)){
            if (!BroadcastExecuted) {
                BroadcastExecuted = true;
                executeBroadcast();
            }
            if (Rule_Immediate){
                if (LastConsoleCommandIndex.equals(-1)) {
                    LastConsoleCommandIndex = ScheduleConsoleCommands.size() - 1;
                    for (String command : ScheduleConsoleCommands) {
                        WCSInteractExecutor.consoleExecuteCommand(applyPlaceHolder(command));
                        WCSInteractExecutor.gInfo(applyPlaceHolder(command));
                    }
                }
                if (LastPlayerCommandIndex.equals(-1)) {
                    LastPlayerCommandIndex = SchedulePlayerCommands.size() - 1;
                    for (String command : SchedulePlayerCommands) {
                        executePlayerCommand(command);
                    }

                }
            } else {
                if (ScheduleConsoleCommands.size()!=0) {
                    Integer CurrentConsoleCommandIndex;
                    if (percent < 1) {
                        CurrentConsoleCommandIndex = ((Double) (percent * (ScheduleConsoleCommands.size() - 1))).intValue();
                    } else {
                        CurrentConsoleCommandIndex = ScheduleConsoleCommands.size() - 1;
                    }
                    if (CurrentConsoleCommandIndex > LastConsoleCommandIndex) {
                        for (int i = LastConsoleCommandIndex + 1; i <= CurrentConsoleCommandIndex; i++) {
                            WCSInteractExecutor.consoleExecuteCommand(applyPlaceHolder(ScheduleConsoleCommands.get(i)));
                        }
                        LastConsoleCommandIndex = CurrentConsoleCommandIndex;
                    }
                }
                if (SchedulePlayerCommands.size()!=0) {
                    Integer CurrentPlayerCommandIndex;
                    if (percent < 1) {
                        CurrentPlayerCommandIndex = ((Double) (percent * (SchedulePlayerCommands.size() - 1))).intValue();
                    } else {
                        CurrentPlayerCommandIndex = SchedulePlayerCommands.size() - 1;
                    }
                    if (CurrentPlayerCommandIndex > LastPlayerCommandIndex) {
                        for (int i = LastPlayerCommandIndex + 1; i <= CurrentPlayerCommandIndex; i++) {
                            executePlayerCommand(SchedulePlayerCommands.get(i));
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
        return true;
    }
    private Boolean needExecute(Month month, DayOfWeek week_day, Integer month_day,
                                Integer hour, Integer minute){
        if (!LastMinute.equals(minute)) {
            LastMinute = minute;
            //check month
            NeedExecute = false;
            String[] months = Rule_Month.split(",");
            for (String m : months) {
                m = m.trim();
                if (m.equals(String.valueOf(month.getValue()))) {
                    NeedExecute = true;
                    break;
                }
            }
            if (!NeedExecute) {
                return false;
            }
            NeedExecute = false;
            String[] days = Rule_Day.split(",");
            for (String d : days) {
                d = d.trim();
                d = d.replace("?", "[0-9]");
                if (Pattern.matches("^" + d + "$", String.format("%02d", month_day))) {
                    NeedExecute = true;
                    break;
                }
                if (d.toUpperCase().equals(week_day.toString())) {
                    NeedExecute = true;
                    break;
                }
            }
            if (!NeedExecute) {
                return false;
            }
            NeedExecute = false;
            String[] hours_minutes = Rule_HM.split(",");
            for (String hm : hours_minutes) {
                String[] hm_split = hm.split(":");
                if (hm_split.length == 2) {
                    String h = hm_split[0].trim();
                    h = h.replace("?", "[0-9]");
                    String m = hm_split[1].trim();
                    m = m.replace("?", "[0-9]");
                    if (Pattern.matches("^" + h + "$", String.format("%02d", hour)) &&
                            Pattern.matches("^" + m + "$", String.format("%02d", minute))) {
                        NeedExecute = true;
                        break;
                    }
                }
            }
        }
        return NeedExecute;
    }
    private void executePlayerCommand(String command){
        for(Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()){
            if (WCSPermission.beConsideredBySchedule(player, ParentSchedule.getCallID())){
                WCSInteractExecutor.playerExecuteCommand(player, applyPlaceHolder(command));
            }
        }
    }
    private void executeBroadcast(){
        if (WCSConfigManager.getBroadcastMode().equals("vanilla")){
            for (String broadcast : ScheduleBroadcasts){
                WCSInteractExecutor.vanillaBroadcast(applyPlaceHolder(broadcast));
            }
        } else {
            for (Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()){
                if (WCSPermission.beConsideredBySchedule(player, ParentSchedule.getCallID())){
                    for (String broadcast : ScheduleBroadcasts){
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
    private String applyPlaceHolder(String str){
        return str.replace("#s", ParentSchedule.getName()).replace("#e", EventName);
    }
}
