package cn.yxgeneral.weavestudio.weavecustomschedule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WCSCommandHandler implements CommandExecutor, TabCompleter{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        return new ArrayList<>();
    }
}
