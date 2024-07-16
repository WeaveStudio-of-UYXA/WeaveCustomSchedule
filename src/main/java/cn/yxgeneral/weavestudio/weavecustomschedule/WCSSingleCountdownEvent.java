package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class WCSSingleCountdownEvent {
    protected String EventID;
    protected String EventName;
    protected String Description;
    protected List<String> Commands;
    protected List<String> Broadcasts;
    WCSSingleCountdownEvent(String id, ConfigurationSection config){
        EventID = id;
        initFromConfig(config);
    }
    WCSSingleCountdownEvent(String id){
        EventID = id;
    }
    public void initFromConfig(ConfigurationSection config){
        EventName = config.getString("name");
        Description = config.getString("description");
        Commands = config.getStringList("commands");
        Broadcasts = config.getStringList("broadcasts");
    }
    public String getEventName(){
        return EventName;
    }
    public String getEventID(){
        return EventID;
    }
    public void doEvent(){
        executeBroadcast();
        executeCommands();
    }
    private void executeCommands(){
        for (String command : Commands){
            WCSInteractiveExecutor.consoleExecuteCommand(command);
        }
    }
    private void executeBroadcast(){
        for (String broadcast : Broadcasts){
            WCSInteractiveExecutor.broadcast(broadcast);
        }
    }
}
