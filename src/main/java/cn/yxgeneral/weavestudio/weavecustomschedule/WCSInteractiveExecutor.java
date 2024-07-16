package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.entity.Player;

public class WCSInteractiveExecutor {
    public static void consoleExecuteCommand(String cmd){
        //execute the command
        WeaveCustomSchedule.getInstance().getServer().dispatchCommand(
                WeaveCustomSchedule.getInstance().getServer().getConsoleSender(), cmd
            );
    }
    public static void playerExecuteCommand(Player player, String cmd){
        WeaveCustomSchedule.getInstance().getServer().dispatchCommand(player, cmd);
    }
    public static void broadcast(String msg){
        WeaveCustomSchedule.getInstance().getServer().broadcastMessage(msg);
    }
    public static void displayTitle(Player player, String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut){
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
