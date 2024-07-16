package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

import me.clip.placeholderapi.PlaceholderAPI;

public class WCSConfigManager {
    public static String PluginPrefix = "";
    private static YamlConfiguration LanguageFile = null;
    public static void initConfig(){
        WeaveCustomSchedule.getInstance().saveDefaultConfig();
        WeaveCustomSchedule.getInstance().saveResource("lang/en_US.yml", false);
        WeaveCustomSchedule.getInstance().saveResource("schedules/eg_schedule.yml", false);
        WeaveCustomSchedule.getInstance().saveResource("countdowns/eg_countdown.yml", false);
        String langFileName = getConfig().getString("language");
        //check lang/langFileName.yml exists
        //if not, load lang/en_US.yml, else load lang/langFileName.yml
        File langFile = new File(WeaveCustomSchedule.getInstance().getDataFolder(), "lang/"+langFileName+".yml");
        if(!langFile.exists()){
            LanguageFile = YamlConfiguration.loadConfiguration(
                    new File(WeaveCustomSchedule.getInstance().getDataFolder(), "lang/en_US.yml")
            );
            WeaveCustomSchedule.warning(getTranslation("plugin.noSuchLang".formatted(langFileName)));
        }else{
            LanguageFile = YamlConfiguration.loadConfiguration(langFile);
        }
        PluginPrefix = getConfig().getString("prefix");
        WCSTableManager.loadCustomSchedules();
    }
    public static FileConfiguration getConfig(){
        return WeaveCustomSchedule.getInstance().getConfig();
    }
    public static String getTranslation(String key){
        String rtn = LanguageFile.getString(key);
        if (rtn != null){
            return LanguageFile.getString(key);
        }else{
            WeaveCustomSchedule.getInstance().warning("Localized key name \'" + key + "\' not detected");
            return "";
        }

    }
    public static void sendPrefixMessage(CommandSender sender, String message){
        sender.sendMessage(PluginPrefix.replace("&", "§")+message.replace("&", "§"));
    }
    public static void sendTranslatedMessage(CommandSender sender, String configNode, String... varargs){
        sendPrefixMessage(sender, getTranslation(configNode).formatted((Object) varargs));
    }
    public static void reloadConfig(){
        WeaveCustomSchedule.getInstance().reloadConfig();
        initConfig();
    }
    public static String applyPlaceHolder(String str, Player player){
        str = str.replace("&", "§").replace("§§", "&");
        if (WeaveCustomSchedule.PAPI){
            return PlaceholderAPI.setPlaceholders(player, str);
        }else{
            return str;
        }
    }
}
