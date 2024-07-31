package cn.yxgeneral.weavestudio.weavecustomschedule.container.countdown;

import cn.yxgeneral.weavestudio.weavecustomschedule.WCSConfigManager;
import cn.yxgeneral.weavestudio.weavecustomschedule.WCSInteractExecutor;
import cn.yxgeneral.weavestudio.weavecustomschedule.WeaveCustomSchedule;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractEventContainer;
import cn.yxgeneral.weavestudio.weavecustomschedule.abstractmodel.WCSAbstractSingleEvent;
import org.bukkit.configuration.ConfigurationSection;

public class WCSCountdownEventContainer extends WCSAbstractEventContainer {
    private boolean IsRunning = false;
    private boolean StartOnLoad = false;
    private boolean StartOnReload = false;
    private int Interval = 0;
    private int TotalCount = 0;
    private int CurrentTick = 0;
    private WCSCountdownEventContainerData Data = new WCSCountdownEventContainerData();
    private WCSCountdownOnStartEvent OnStartEvent = null;
    private WCSCountdownNotifier Notifier = null;
    public WCSCountdownEventContainer(){
        super(ContainerType.Countdown);
    }
    public void setContainerData(WCSCountdownEventContainerData data){
        Data = data;
        if (Data.getCurrentIndex()>= getEventSize()){
            Data.resetCurrentIndex();
        }
    }
    public WCSCountdownEventContainerData getContainerData(){
        return Data;
    }
    @Override
    public boolean initFromConfig(String ConfigFilePath){
        if (super.initFromConfig(ConfigFilePath)){
            try {
                Interval = getConfigFile().getInt("interval") * 20;
                TotalCount = getConfigFile().getInt("total");
                StartOnLoad = getConfigFile().getBoolean("startOnLoad");
                StartOnReload = getConfigFile().getBoolean("startOnReload");
                OnStartEvent = new WCSCountdownOnStartEvent(this, getConfigFile().getConfigurationSection("onStart"));
                Notifier = new WCSCountdownNotifier(this, getConfigFile().getConfigurationSection("notice"));
                WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("container.loadSuccess")));
                if (StartOnLoad && WeaveCustomSchedule.isJustStarted() || StartOnReload) {
                    start();
                }
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
    public void start(){
        if (isEnable() && !IsRunning){
            IsRunning = true;
            OnStartEvent.onTick(1.0);
            Notifier.showBossbar();
            WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("countdown.start")));
        }
    }
    public void pause(){
        if (isEnable() && IsRunning){
            IsRunning = false;
            WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("countdown.pause")));
        }
    }
    public void stop() {
        if (isEnable() && IsRunning) {
            IsRunning = false;
            CurrentTick = 0;
            Notifier.hideBossbar();
            WCSInteractExecutor.gInfo(applyPlaceHolder(WCSConfigManager.getTranslation("countdown.stop")));
        }
    }
    public void updateAvailablePlayer(){
        Notifier.updateAvailablePlayer();
    }
    @Override
    public WCSAbstractSingleEvent onConfigLoadingEvent(String eventID, ConfigurationSection config){
        return new WCSSingleCountdownEvent(this, eventID, config);
    }
    @Override
    public void onTick(double percent){
        if (!IsRunning){
            return;
        }
        CurrentTick += 1;
        int index = Data.getCurrentIndex();
        Notifier.update(getEvents().get(index).getEventName(), CurrentTick, Interval);
        if (CurrentTick >= Interval){
            CurrentTick = 0;
            getEvents().get(index).onTick(1.0);
            Data.currentIndexRecord();
            if (Data.getCurrentIndex()>= getEventSize()){
                Data.resetCurrentIndex();
                if (TotalCount<=0){
                    return;
                }
                Data.currentTotalCountRecord();
                if (Data.getCurrentTotalCount() >= TotalCount){
                    stop();
                }
            }
        }
    }
    @Override
    public String applyPlaceHolder(String str){
        return super.applyPlaceHolder(str).replace("{interval}", String.valueOf(Interval))
                .replace("{remain}", String.format("%.2f", (double)(Interval - CurrentTick) / 20))
                .replace("{total_count}", String.format("%.2f", (double)TotalCount / 20))
                .replace("{total_remain}", String.valueOf(TotalCount - Data.getCurrentTotalCount()))
                .replace("{total_events}", String.valueOf(getEventSize()))
                .replace("{current_event_index}", String.valueOf(Data.getCurrentIndex()+1))
                .replace("{current_event_name}", getEvents().get(Data.getCurrentIndex()).getEventName());
    }
}
