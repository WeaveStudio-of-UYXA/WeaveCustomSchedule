package cn.yxgeneral.weavestudio.weavecustomschedule.container.schedule;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSConfigManager;
import cn.yxgeneral.weavestudio.weavecustomschedule.WCSInteractExecutor;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;

public class WCSScheduleEventContainer extends WCSAbstractEventContainer {
    public WCSScheduleEventContainer(){
        super(ContainerType.Schedule);
    }
    @Override
    public boolean initFromConfig(String ConfigFilePath){
        if (super.initFromConfig(ConfigFilePath)){
            WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("container.loadSuccess")));
            return true;
        }else{
            WCSInteractExecutor.gWarning(applyPlaceHolder(WCSConfigManager.getTranslation("container.loadFailed")));
            return false;
        }
    }
    @Override
    public WCSAbstractSingleEvent onConfigLoadingEvent(String eventID, ConfigurationSection config){
        return new WCSSingleScheduleEvent(this, eventID, config);
    }
    @Override
    public void onTick(double percent){
        for (WCSAbstractSingleEvent event : getEvents()) {
            event.onTick(percent);
        }
    }
}
