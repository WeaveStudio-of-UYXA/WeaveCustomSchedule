package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.ConfigurationSection;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.regex.*;

public class WCSSingleScheduleEvent {
    private String EventName;
    private String EventID;
    private String Rule_HM;
    private String Rule_Day;
    private String Rule_Month;
    private Integer LastMinute = -1;
    private Boolean Rule_Immediate;
    private List<String> ScheduleCommands;
    private List<String> ScheduleBroadcasts;
    private Integer LastCommandIndex = -1;
    private Boolean NeedExecute = false;

    public WCSSingleScheduleEvent(String id, ConfigurationSection config){
        EventID = id;
        initFromConfig(config);
    }
    public WCSSingleScheduleEvent(String id){
        EventID = id;
    }
    public void initFromManual(String eventName, String rule_day, String rule_HHMM,
                               String rule_month, Boolean rule_immediate, List<String> commands,
                                List<String> broadcasts){
        EventName = eventName;
        Rule_Day = rule_day;
        Rule_HM = rule_HHMM;
        Rule_Month = rule_month;
        Rule_Immediate = rule_immediate;
        ScheduleCommands = commands;
        ScheduleBroadcasts = broadcasts;
    }
    public void initFromConfig(ConfigurationSection config){
        EventName = config.getString("name");
        Rule_Day = config.getString("rule.day");
        Rule_HM = config.getString("rule.HHMM");
        Rule_Month = config.getString("rule.month");
        Rule_Immediate = config.getBoolean("rule.immediate");
        ScheduleCommands = config.getStringList("commands");
        ScheduleBroadcasts = config.getStringList("broadcasts");
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
            executeBroadcast();
            if (Rule_Immediate){
                if (LastCommandIndex==-1) {
                    for (String command : ScheduleCommands) {
                        executeCommand(command);
                    }
                    LastCommandIndex = ScheduleCommands.size() - 1;
                }
            } else {
                Integer CurrentCommandIndex;
                if (percent < 1) {
                    CurrentCommandIndex = ((Double) (percent * ScheduleCommands.size())).intValue();
                } else {
                    CurrentCommandIndex = ScheduleCommands.size() - 1;
                }
                if (CurrentCommandIndex > LastCommandIndex) {
                    for (int i = LastCommandIndex + 1; i <= CurrentCommandIndex; i++) {
                        executeCommand(ScheduleCommands.get(i));
                    }
                    LastCommandIndex = CurrentCommandIndex;
                }

            }
            if (percent >= 1.0) {
                LastCommandIndex = -1;
            }
        }
        return true;
    }
    private Boolean needExecute(Month month, DayOfWeek week_day, Integer month_day,
                                Integer hour, Integer minute){
        if (minute != LastMinute) {
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
    private Boolean executeCommand(String command){
        WCSInteractiveExecutor.consoleExecuteCommand(command);
        return true;
    }
    private Boolean executeBroadcast(){
        if (LastCommandIndex==-1){
            for(String msg : ScheduleBroadcasts) {
                WCSInteractiveExecutor.broadcast(msg);
            }
        }
        return true;
    }
}
