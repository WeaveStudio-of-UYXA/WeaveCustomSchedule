package cn.yxgeneral.weavestudio.weavecustomschedule.container.schedule;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSPermission;
import cn.yxgeneral.weavestudio.weavecustomschedule.WCSTimer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.regex.Pattern;

public class WCSSingleScheduleEvent extends WCSAbstractSingleEvent {
    private String Rule_HM;
    private String Rule_Day;
    private String Rule_Month;
    private int LastMinute = -1;
    private Month LastMonth = null;
    private DayOfWeek LastWeekDay = null;
    private int LastMonthDay = -1;
    private int LastHour = -1;
    private boolean NotTheMonth = true;
    private boolean NotTheDay = true;
    private boolean NotTheHour = true;
    private boolean NeedExecute = false;
    public WCSSingleScheduleEvent(WCSAbstractEventContainer parentContainer, String eventID, ConfigurationSection config) {
        super(parentContainer, eventID, config);
        initFromConfig_Schedule(config);
    }
    private void initFromConfig_Schedule(ConfigurationSection config){
        Rule_HM = config.getString("rule.HHMM");
        Rule_Day = config.getString("rule.day");
        Rule_Month = config.getString("rule.month");
    }
    @Override
    public void onTick(double percent){
        if (needExecute()){
            super.onTick(percent);
        }
    }
    private boolean needExecute(){
        if (WCSTimer.getMinute() == LastMinute){
            return NeedExecute;
        }
        if (WCSTimer.getMonth().equals(LastMonth) && NotTheMonth){
            return false;
        }else if(!WCSTimer.getMonth().equals(LastMonth)){
            LastMonth = WCSTimer.getMonth();
            NotTheMonth = true;
            if (!Rule_Month.equals("*")){
                String[] months = Rule_Month.split(",");
                for (String m : months) {
                    m = m.trim();
                    if (m.equals(String.valueOf(WCSTimer.getMonth().getValue()))) {
                        NotTheMonth = false;
                        break;
                    }
                }
                if (NotTheMonth){
                    NeedExecute = false;
                    return false;
                }
            }else{
                NotTheMonth = false;
            }
        }
        if (WCSTimer.getWeekDay().equals(LastWeekDay) && NotTheDay &&
                LastMonthDay == WCSTimer.getDayOfMonth()){
            return false;
        }else if (!WCSTimer.getWeekDay().equals(LastWeekDay) ||
                LastMonthDay != WCSTimer.getDayOfMonth()){
            LastWeekDay = WCSTimer.getWeekDay();
            LastMonthDay = WCSTimer.getDayOfMonth();
            NotTheDay = true;
            if (!Rule_Day.equals("*")){
                String[] days = Rule_Day.split(",");
                for (String d : days) {
                    d = d.trim();
                    d = d.replace("?", "[0-9]");
                    if (Pattern.matches("^" + d + "$", String.format("%02d", WCSTimer.getDayOfMonth()))) {
                        NotTheDay = false;
                        break;
                    }
                    if (d.toUpperCase().equals(WCSTimer.getWeekDay().toString())) {
                        NotTheDay = false;
                        break;
                    }
                }
                if (NotTheDay) {
                    NeedExecute = false;
                    return false;
                }
            }else{
                NotTheDay = false;
            }
        }
        if (LastHour == WCSTimer.getHour() && NotTheHour){
            return false;
        }else if (LastHour != WCSTimer.getHour()){
            LastHour = WCSTimer.getHour();
            NotTheHour = true;
            String[] hours_minutes = Rule_HM.split(",");
            for (String hm : hours_minutes) {
                String[] hm_split = hm.split(":");
                if (hm_split.length == 2) {
                    String h = hm_split[0].trim();
                    h = h.replace("?", "[0-9]");
                    if (Pattern.matches("^" + h + "$", String.format("%02d", WCSTimer.getHour()))) {
                        NotTheHour = false;
                        break;
                    }
                }
            }
            if (NotTheHour){
                NeedExecute = false;
                return false;
            }
        }
        if (LastMinute != WCSTimer.getMinute()){
            LastMinute = WCSTimer.getMinute();
            NeedExecute = false;
            String[] hours_minutes = Rule_HM.split(",");
            for (String hm : hours_minutes) {
                String[] hm_split = hm.split(":");
                if (hm_split.length == 2) {
                    String m = hm_split[1].trim();
                    m = m.replace("?", "[0-9]");
                    String h = hm_split[0].trim();
                    h = h.replace("?", "[0-9]");
                    if (Pattern.matches("^" + m + "$", String.format("%02d", WCSTimer.getMinute())) &&
                            Pattern.matches("^" + h + "$", String.format("%02d", WCSTimer.getHour()))) {
                        NeedExecute = true;
                        break;
                    }
                }
            }
        }
        return NeedExecute;
    }
    @Override
    public boolean checkPermission(Player player){
        return WCSPermission.beConsideredBySchedule(player, getParentContainer().getContainerID());
    }
}
