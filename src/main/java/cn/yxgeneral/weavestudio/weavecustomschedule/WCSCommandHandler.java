package cn.yxgeneral.weavestudio.weavecustomschedule;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

class WCSExecutorProtector implements AutoCloseable {
    WCSExecutorProtector(CommandSender player){
        if (player instanceof Player) {
            WCSCommandHandler.setExecutor((Player) player);
        }
    }
    @Override
    public void close() {
        WCSCommandHandler.setExecutor(null);
    }
}

public class WCSCommandHandler implements CommandExecutor, TabCompleter{
    private static Player executor = null;
    protected static void setExecutor(Player player){
        executor = player;
    }
    public static @Nullable Player getExecutor(){
        return executor;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase(("weavecustomschedule"))) {
            try (WCSExecutorProtector player = new WCSExecutorProtector(sender)) {
                if (args.length == 0) {
                    sendHelpMessage(sender);
                } else {
                    int argLength = args.length;
                    String arg1 = args[0];
                    switch (arg1) {
                        case "help":
                            sendHelpMessage(sender);
                            break;
                        case "reload":
                            if (sender instanceof Player && !WCSPermission.isAdmin((Player) sender)) {
                                WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
                                return true;
                            }
                            if (WCSTimer.checkConfigReloadable()) {
                                if (argLength == 1) {
                                    WCSConfigManager.reloadConfig(false);
                                } else if (argLength == 2 && args[1].equalsIgnoreCase("force")) {
                                    WCSConfigManager.reloadConfig(true);
                                }
                            } else {
                                for (String warning : WCSConfigManager.getTranslationList("plugin.reloadWarning")) {
                                    WCSInteractExecutor.gWarning(warning);
                                }
                                WCSTimer.startReloadTimer();
                            }
                            break;
                        case "countdown":
                            if (argLength != 3) {
                                sendHelpMessage(sender);
                                return true;
                            }
                            handleCountdownAction(sender, args[1], args[2]);
                            break;
                        case "schedule":
                            if (argLength != 3) {
                                sendHelpMessage(sender);
                                return true;
                            }
                            handleScheduleAction(sender, args[1], args[2]);
                            break;
                        case "tps":
                            if (argLength != 3) {
                                sendHelpMessage(sender);
                                return true;
                            }
                            handleTpsAction(sender, args[1], args[2]);
                            break;
                        case "dashboard":
                            if (argLength != 2) {
                                sendHelpMessage(sender);
                                return true;
                            }
                            handleDashboardAction(sender, args[1]);
                            break;
                        default:
                            sendHelpMessage(sender);
                            break;
                    }
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        List<String> rtn = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase(("weavecustomschedule"))){
            switch (args.length){
                case 1:
                    rtn.add("help");
                    rtn.add("schedule");
                    rtn.add("countdown");
                    rtn.add("reload");
                    rtn.add("dashboard");
                    return rtn;
                case 2:
                    String arg1_2 = args[0];
                    switch (arg1_2){
                        case "schedule":
                            return WCSContainerManager.getScheduleContainerIDList();
                        case "countdown":
                            return WCSContainerManager.getCountdownContainerIDList();
                        case "reload":
                            rtn.add("force");
                            return rtn;
                        case "dashboard":
                            rtn.add("all");
                            rtn.add("schedule");
                            rtn.add("countdown");
                            rtn.add("tps");
                            return rtn;
                    }
                    return null;
                case 3:
                    String arg1_3 = args[0];
                    switch (arg1_3){
                        case "schedule":
                            rtn.add("enable");
                            rtn.add("disable");
                            return rtn;
                        case "countdown":
                            rtn.add("enable");
                            rtn.add("disable");
                            rtn.add("start");
                            rtn.add("stop");
                            rtn.add("pause");
                            return rtn;
                        case "tps":
                            rtn.add("enable");
                            rtn.add("disable");
                            return rtn;
                    }
                    return null;
            }
            return null;
        }
        return null;
    }
    private void sendHelpMessage(CommandSender sender){
        for(String line : WCSConfigManager.getTranslationList("command.help")){
            WCSInteractExecutor.gInfo(line);
        }
    }
    private void handleCountdownAction(CommandSender sender,String id, String action){
        if (!WCSContainerManager.isCountdownIDExist(id)){
            WCSInteractExecutor.gWarning(
                    WCSConfigManager.getTranslation("container.noSuchContainer")
                            .replace("{container_type}",
                                    WCSConfigManager.getTranslation("container.type.Countdown")
                                )
                            .replace("{container_id}",id)
                );
            return;
        }
        switch (action){
            case "enable":
            case "disable":
                if (sender instanceof Player && !WCSPermission.canEnableCountdown((Player) sender, id)) {
                    WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
                    return;
                }else{
                    WCSContainerManager.setCountdownEnable(id, action.equals("enable"));
                }
                break;
            case "start":
            case "stop":
            case "pause":
                if (sender instanceof Player && !WCSPermission.canToggleCountdown((Player) sender, id)) {
                    WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
                    return;
                }
                if (action.equals("start")){
                    WCSContainerManager.setCountdownRunning(id, true);
                }else if (action.equals("stop")){
                    WCSContainerManager.setCountdownRunning(id, false);
                }else{
                    WCSContainerManager.pauseCountdown(id);
                }
                break;
            default:
                sendHelpMessage(sender);
        }
    }
    private void handleScheduleAction(CommandSender sender,String id, String action){
        if (!WCSContainerManager.isScheduleIDExist(id)){
            WCSInteractExecutor.gWarning(
                    WCSConfigManager.getTranslation("plugin.noSuchContainer")
                            .replace("{container_type}",
                                    WCSConfigManager.getTranslation("container.type.Schedule")
                            )
                            .replace("{container_id}",id)
            );
            return;
        }
        switch (action){
            case "enable":
            case "disable":
                if (sender instanceof Player && !WCSPermission.canEnableSchedule((Player) sender, id)) {
                    WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
                    return;
                }else{
                    WCSContainerManager.setScheduleEnable(id, action.equals("enable"));
                }
                break;
            default:
                sendHelpMessage(sender);
        }

    }
    private void handleTpsAction(CommandSender sender,String id, String action){
        if (!WCSContainerManager.isTpsIDExist(id)){
            WCSInteractExecutor.gWarning(
                    WCSConfigManager.getTranslation("plugin.noSuchContainer")
                            .replace("{container_type}",
                                    WCSConfigManager.getTranslation("container.type.Tps")
                            )
                            .replace("{container_id}",id)
            );
            return;
        }
        switch (action){
            case "enable":
            case "disable":
                if (sender instanceof Player && !WCSPermission.canEnableTps((Player) sender, id)) {
                    WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
                    return;
                }else{
                    WCSContainerManager.setTpsEnable(id, action.equals("enable"));
                }
                break;
            default:
                sendHelpMessage(sender);
        }
    }
    private void handleDashboardAction(CommandSender sender,String type){
        if (sender instanceof Player && !WCSPermission.getDashboard((Player) sender)) {
            WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
            return;
        }
        switch (type){
            case "all":
                WCSContainerManager.printCountdownStatus();
                WCSContainerManager.printScheduleStatus();
                break;
            case "schedule":
                WCSContainerManager.printScheduleStatus();
                break;
            case "countdown":
                WCSContainerManager.printCountdownStatus();
                break;
            default:
                sendHelpMessage(sender);
        }
    }
}
