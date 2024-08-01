package cn.yxgeneral.weavestudio.weavecustomschedule.container.tps;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSConfigManager;
import cn.yxgeneral.weavestudio.weavecustomschedule.WCSInteractExecutor;
import cn.yxgeneral.weavestudio.weavecustomschedule.WCSTimer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;

public class WCSTpsEventContainer extends WCSAbstractEventContainer {
    private int RollingWindow = 10; // consider the last x ticks
    private int MsptRecordIndex = 0;
    private int[] MsptRecord = null;
    private int MsptSum = 0;
    private float Tps = 20.0f;
    public WCSTpsEventContainer(){
        super(ContainerType.Tps);
    }
    @Override
    public boolean initFromConfig(String ConfigFilePath){
        if (super.initFromConfig(ConfigFilePath)){
            try {
                RollingWindow = getConfigFile().getInt("rollingWindow");
                if (RollingWindow <= 1) {
                    RollingWindow = 10;
                }
                MsptRecord = new int[RollingWindow];
                for (int i = 0; i < RollingWindow; i++) {
                    MsptRecord[i] = 50;
                }
                MsptSum = 50 * RollingWindow;
                WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("container.loadSuccess")));
                return true;
            }catch (Exception e){
                WCSInteractExecutor.gWarning(applyPlaceHolder(WCSConfigManager.getTranslation("container.loadFailed")));
                return false;
            }
        }else{
            WCSInteractExecutor.gWarning(applyPlaceHolder(WCSConfigManager.getTranslation("container.loadFailed")));
            return false;
        }
    }
    @Override
    public WCSAbstractSingleEvent onConfigLoadingEvent(String eventID, ConfigurationSection config){
        return new WCSSingleTpsEvent(this, eventID, config);
    }
    @Override
    public void onTick(double percent){
        MsptSum -= MsptRecord[MsptRecordIndex];
        MsptRecord[MsptRecordIndex] = WCSTimer.getMsPerTick();
        MsptSum += MsptRecord[MsptRecordIndex];
        MsptRecordIndex = (MsptRecordIndex + 1) % RollingWindow; // rotate the index
        Tps = 1000.0f / ((float)MsptSum / RollingWindow);
        if (Tps > 20) {
            Tps = 20;
        }
        for(WCSAbstractSingleEvent event: getEvents()){
            event.onTick(1.0);
        }
    }
    @Override
    public String applyPlaceHolder(String str) {
        return super.applyPlaceHolder(str)
                .replace("{tps}", String.valueOf(Tps));
    }
    public float getTps(){
        return Tps;
    }
}
