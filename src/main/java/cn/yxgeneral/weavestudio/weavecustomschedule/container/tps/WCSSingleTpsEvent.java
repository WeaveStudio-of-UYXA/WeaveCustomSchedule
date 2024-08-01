package cn.yxgeneral.weavestudio.weavecustomschedule.container.tps;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSPermission;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WCSSingleTpsEvent extends WCSAbstractSingleEvent {
    public enum TpsCondition{
        EQUAL,
        MORE,
        LESS,
    }
    private TpsCondition Condition;
    private double TpsValue;
    private boolean Repeat;
    private int RepeatInterval;
    private int RepeatCycle = 0;
    private int DelayedTick = 0;
    private int DelayedCycle = 0;
    private boolean Satisfied = false;
    private boolean JustTriggered = false;
    public WCSSingleTpsEvent(WCSAbstractEventContainer parentContainer, String eventID, ConfigurationSection config) {
        super(parentContainer, eventID, config);
        initFromConfig_Tps(config);
    }
    private void initFromConfig_Tps(ConfigurationSection config){
        try{
            Condition = TpsCondition.valueOf(config.getString("condition").toUpperCase());
        }catch (Exception e){
            Condition = TpsCondition.EQUAL;
        }
        TpsValue = config.getDouble("tps");
        DelayedTick = config.getInt("delayedTick");
        Repeat = config.getBoolean("repeat");
        RepeatInterval = config.getInt("repeatInterval");

    }
    @Override
    public void onTick(double percent) {
        float tps = ((WCSTpsEventContainer)getParentContainer()).getTps();
        boolean nowSatisfied = false;
        switch(Condition){
            case EQUAL:
                nowSatisfied = tps == TpsValue;
                break;
            case MORE:
                nowSatisfied = tps > TpsValue;
                break;
            case LESS:
                nowSatisfied = tps < TpsValue;
                break;
        }
        if (Satisfied != nowSatisfied){
            if (DelayedCycle < DelayedTick) {
                DelayedCycle++;
            }else{
                DelayedCycle = 0;
                Satisfied = nowSatisfied;
            }
        }else{
            DelayedCycle = 0;
        }
        if (Satisfied) {
            if (!Repeat && !JustTriggered) {
                super.onTick(1.0);
                JustTriggered = true;
            } else if (Repeat) {
                if (RepeatCycle == 0) {
                    super.onTick(1.0);
                }
                RepeatCycle = (RepeatCycle + 1) % RepeatInterval;
            }
        }else{
            JustTriggered = false;
            RepeatCycle = 0;
        }
    }
    @Override
    public boolean checkPermission(Player p){
        return WCSPermission.beConsideredByTps(p, getParentContainer().getContainerID());
    }
}
