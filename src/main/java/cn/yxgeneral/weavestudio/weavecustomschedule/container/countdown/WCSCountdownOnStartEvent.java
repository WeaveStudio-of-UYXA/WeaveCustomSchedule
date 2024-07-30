package cn.yxgeneral.weavestudio.weavecustomschedule.container.countdown;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSPermission;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WCSCountdownOnStartEvent extends WCSAbstractSingleEvent {
    public WCSCountdownOnStartEvent(WCSAbstractEventContainer parentContainer, ConfigurationSection config) {
        super(parentContainer, "onStart", config);
    }
    @Override
    public void onTick(double percent) {
        super.onTick(1.0);
    }
    @Override
    public boolean checkPermission(Player p){
        return WCSPermission.beConsideredByCountdown(p, getParentContainer().getContainerID());
    }
}
