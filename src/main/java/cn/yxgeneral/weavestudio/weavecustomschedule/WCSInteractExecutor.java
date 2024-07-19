package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.entity.Player;

public class WCSInteractExecutor {
    public static void consoleExecuteCommand(String cmd){
        //execute the command
        WeaveCustomSchedule.getInstance().getServer().dispatchCommand(
                WeaveCustomSchedule.getInstance().getServer().getConsoleSender(), cmd
            );
    }
    public static void gInfo(String msg){
        WeaveCustomSchedule.info(WCSUtils.applyConsoleColorCode(WCSUtils.applyPlaceHolder(msg, null)));
        Player player = WCSCommandHandler.getExecutor();
        if (player != null){
            sendPrefixMessage(player, msg);
        }
    }
    public static void gWarning(String msg){
        WeaveCustomSchedule.warning(WCSUtils.applyConsoleColorCode(WCSUtils.applyPlaceHolder(msg, null)));
        Player player = WCSCommandHandler.getExecutor();
        if (player != null){
            sendPrefixMessage(player, msg);
        }
    }
    public static void playerExecuteCommand(Player player, String cmd){
        WeaveCustomSchedule.getInstance().getServer().dispatchCommand(player, WCSUtils.applyAll(cmd, player));
    }
    public static void vanillaBroadcast(String msg){
        WeaveCustomSchedule.getInstance().getServer().broadcastMessage(WCSUtils.applyAll(msg, null));
    }
    public static void displayTitle(Player player, String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut){
        player.sendTitle(WCSUtils.applyAll(title, player), WCSUtils.applyAll(subtitle, player), fadeIn, stay, fadeOut);
    }

    public static void sendPrefixMessage(Player sender, String message){
        sender.sendMessage(WCSUtils.applyAll(WCSConfigManager.getPluginPrefix() + message, sender));
    }
    public static void sendNormalMessage(Player sender, String message){
        sender.sendMessage(WCSUtils.applyAll(message, sender));
    }
}
