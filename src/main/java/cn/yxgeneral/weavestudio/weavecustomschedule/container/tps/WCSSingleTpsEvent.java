package cn.yxgeneral.weavestudio.weavecustomschedule.container.tps;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSPermission;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WCSSingleTpsEvent extends WCSAbstractSingleEvent {
    public enum TpsCondition{
        Equal,
        Greater,
        Less,
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
            Condition = TpsCondition.valueOf(config.getString("condition"));
        }catch (Exception e){
            Condition = TpsCondition.Equal;
        }
        TpsValue = config.getDouble("tps");
        DelayedTick = config.getInt("delayedTick");
        Repeat = config.getBoolean("repeat");
        RepeatInterval = config.getInt("repeatInterval");

    }
    @Override
    public void onTick(double percent) {
        int tps = ((WCSTpsEventContainer)getParentContainer()).getTps();
        switch(Condition){
            case Equal:
                Satisfied = tps == TpsValue;
                break;
            case Greater:
                Satisfied = tps > TpsValue;
                break;
            case Less:
                Satisfied = tps < TpsValue;
                break;
        }
        if (Satisfied) {
            if (DelayedCycle < DelayedTick) {
                DelayedCycle++;
            }else {
                DelayedCycle = 0;
                if (!Repeat && !JustTriggered) {
                    super.onTick(1.0);
                    JustTriggered = true;
                } else if (Repeat) {
                    if (RepeatCycle == 0) {
                        super.onTick(1.0);
                    }
                    RepeatCycle = (RepeatCycle + 1) % RepeatInterval;
                }
            }
        }else{
            JustTriggered = false;
            RepeatCycle = 0;
            DelayedCycle = 0;
        }
    }
    @Override
    public boolean checkPermission(Player p){
        return WCSPermission.beConsideredByTps(p, getParentContainer().getContainerID());
    }
}
