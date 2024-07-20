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
                switch (args.length) {
                    case 0:
                        sendHelpMessage(sender);
                        break;
                    default:
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
                                }else{
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
                        break;
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
                            return WCSTableManager.getScheduleIDList();
                        case "countdown":
                            return WCSTableManager.getCountdownIDList();
                        case "reload":
                            rtn.add("force");
                            return rtn;
                        case "dashboard":
                            rtn.add("all");
                            rtn.add("schedule");
                            rtn.add("countdown");
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
        if (!WCSTableManager.isCountdownIDExist(id)){
            WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noSuchCountdown").replace("#c",id));
            return;
        }
        switch (action){
            case "enable":
            case "disable":
                if (sender instanceof Player && !WCSPermission.canEnableCountdown((Player) sender, id)) {
                    WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
                    return;
                }else{
                    if (action.equals("enable")){
                        WCSTableManager.setCountdownEnable(id, true);
                        WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("countdown.enable").replace("#c",id));
                    }else{
                        WCSTableManager.setCountdownEnable(id, false);
                        WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("countdown.disable").replace("#c",id));
                    }
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
                    WCSTableManager.setCountdownRunning(id, true);
                    WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("countdown.start").replace("#c",id));
                }else if (action.equals("stop")){
                    WCSTableManager.setCountdownRunning(id, false);
                    WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("countdown.stop").replace("#c",id));
                }else{
                    WCSTableManager.pauseCountdown(id);
                    WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("countdown.pause").replace("#c",id));
                }
                break;
            default:
                sendHelpMessage(sender);
        }
    }
    private void handleScheduleAction(CommandSender sender,String id, String action){
        if (!WCSTableManager.isScheduleIDExist(id)){
            WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noSuchSchedule").replace("#s",id));
            return;
        }
        switch (action){
            case "enable":
            case "disable":
                if (sender instanceof Player && !WCSPermission.canEnableSchedule((Player) sender, id)) {
                    WCSInteractExecutor.gWarning(WCSConfigManager.getTranslation("plugin.noPermission"));
                    return;
                }else{
                    if (action.equals("enable")){
                        WCSTableManager.setScheduleEnable(id, true);
                        WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("schedule.enable").replace("#e",id));
                    }else{
                        WCSTableManager.setScheduleEnable(id, false);
                        WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("schedule.disable").replace("#e",id));
                    }
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
                WCSTableManager.printCountdownStatus();
                WCSTableManager.printScheduleStatus();
                break;
            case "schedule":
                WCSTableManager.printScheduleStatus();
                break;
            case "countdown":
                WCSTableManager.printCountdownStatus();
                break;
            default:
                sendHelpMessage(sender);
        }
    }
}
