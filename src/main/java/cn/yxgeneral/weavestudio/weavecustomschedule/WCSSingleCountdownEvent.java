package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class WCSSingleCountdownEvent {
    protected String EventID;
    protected String EventName;
    protected String Description;
    protected List<String> ConsoleCommands;
    protected List<String> PlayerCommands;
    protected List<String> Broadcasts;
    WCSSingleCountdownEvent(String id, ConfigurationSection config){
        EventID = id;
        initFromConfig(config);
    }
    public void initFromConfig(ConfigurationSection config){
        EventName = config.getString("name");
        Description = config.getString("description");
        ConsoleCommands = config.getStringList("command.console");
        PlayerCommands = config.getStringList("command.player");
        Broadcasts = config.getStringList("broadcast");
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
        for (String command : ConsoleCommands){
            WCSInteractExecutor.consoleExecuteCommand(command);
        }
        for (Player p: WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()){
            if (WCSPermission.beConsideredByCountdown(p, EventID)) {
                for (String command : PlayerCommands) {
                    WCSInteractExecutor.playerExecuteCommand(p, command);
                }
            }
        }
    }
    private void executeBroadcast(){
        if (WCSConfigManager.getBroadcastMode().equals("vanilla")) {
            for (String broadcast : Broadcasts) {
                WCSInteractExecutor.vanillaBroadcast(broadcast);
            }
        } else {
            for (Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()) {
                if (WCSPermission.beConsideredByCountdown(player, EventID)) {
                    for (String broadcast : Broadcasts) {
                        if (WCSConfigManager.isBroadcastWCSModeWithPrefix()) {
                            WCSInteractExecutor.sendPrefixMessage(player, broadcast);
                        } else {
                            WCSInteractExecutor.sendNormalMessage(player, broadcast);
                        }
                    }
                }
            }
        }
    }
}
