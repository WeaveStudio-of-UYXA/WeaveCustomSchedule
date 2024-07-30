package cn.yxgeneral.weavestudio.weavecustomschedule.container.schedule;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSPermission;
import cn.yxgeneral.weavestudio.weavecustomschedule.WCSTimer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class WCSSingleScheduleEvent extends WCSAbstractSingleEvent {
    private String Rule_HM;
    private String Rule_Day;
    private String Rule_Month;
    private int LastMinute = -1;
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
        LocalDateTime now = WCSTimer.WCSTickLoop.getLastNow();
        if (LastMinute!=now.getMinute()){
            LastMinute = now.getMinute();
            NeedExecute = false;
            String[] months = Rule_Month.split(",");
            for (String m : months) {
                m = m.trim();
                if (m.equals(String.valueOf(now.getMonth().getValue()))) {
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
                if (Pattern.matches("^" + d + "$", String.format("%02d", now.getDayOfMonth()))) {
                    NeedExecute = true;
                    break;
                }
                if (d.toUpperCase().equals(now.getDayOfWeek().toString())) {
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
                    if (Pattern.matches("^" + h + "$", String.format("%02d", now.getHour())) &&
                            Pattern.matches("^" + m + "$", String.format("%02d", now.getMinute()))) {
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
