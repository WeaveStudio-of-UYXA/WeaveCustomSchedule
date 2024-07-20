package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class WCSConfigManager {
    private static String PluginPrefix = "";
    private static Integer CountdownNotifierInterval = 5;
    private static YamlConfiguration LanguageFile = null;
    private static String BroadcastMode = "wcs";
    private static Boolean BroadcastWCSModeWithPrefix = true;
    public static void initConfig(Boolean forceClearCountdown){
        WeaveCustomSchedule.getInstance().saveDefaultConfig();
        WeaveCustomSchedule.getInstance().saveResource("lang/en_US.yml", false);
        WeaveCustomSchedule.getInstance().saveResource("lang/zh_SC.yml", false);
        WeaveCustomSchedule.getInstance().saveResource("schedules/eg_schedule.yml", false);
        WeaveCustomSchedule.getInstance().saveResource("countdowns/eg_countdown.yml", false);
        WeaveCustomSchedule.getInstance().saveResource("countdowns/eg_serverstop.yml", false);
        String langFileName = getConfig().getString("language");
        //check lang/langFileName.yml exists
        //if not, load lang/en_US.yml, else load lang/langFileName.yml
        File langFile = new File(WeaveCustomSchedule.getInstance().getDataFolder(), "lang/"+langFileName+".yml");
        if(!langFile.exists()){
            LanguageFile = YamlConfiguration.loadConfiguration(
                    new File(WeaveCustomSchedule.getInstance().getDataFolder(), "lang/en_US.yml")
            );
            WeaveCustomSchedule.warning(getTranslation("plugin.noSuchLang").formatted(langFileName));
        }else{
            LanguageFile = YamlConfiguration.loadConfiguration(langFile);
        }
        PluginPrefix = getConfig().getString("prefix");
        CountdownNotifierInterval = getConfig().getInt("countdown.notifier.interval");
        BroadcastMode = getConfig().getString("broadcast.mode");
        BroadcastWCSModeWithPrefix = getConfig().getBoolean("broadcast.prefix");
        WCSTableManager.loadCustomSchedules();
        WCSTableManager.loadCustomCountdowns(forceClearCountdown);

    }
    public static FileConfiguration getConfig(){
        return WeaveCustomSchedule.getInstance().getConfig();
    }
    public static String getTranslation(String key){
        String rtn = LanguageFile.getString(key);
        if (rtn != null){
            return LanguageFile.getString(key);
        }else{
            WeaveCustomSchedule.warning("Localized key name '" + key + "' not detected");
            return "";
        }
    }
    public static List<String> getTranslationList(String key){
        List<String> rtn = LanguageFile.getStringList(key);
        if (!rtn.isEmpty()){
            return LanguageFile.getStringList(key);
        }else{
            WeaveCustomSchedule.warning("Localized key name '" + key + "' not detected");
            return new ArrayList<>();
        }
    }
    public static String getPluginPrefix(){
        return PluginPrefix;
    }
    public static Integer getCountdownNotifierInterval(){
        return CountdownNotifierInterval;
    }
    public static String getBroadcastMode(){
        return BroadcastMode;
    }
    public static Boolean isBroadcastWCSModeWithPrefix(){
        return BroadcastWCSModeWithPrefix;
    }
    public static void reloadConfig(Boolean forceClearCountdown){
        WeaveCustomSchedule.getInstance().reloadConfig();
        initConfig(forceClearCountdown);
        WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("plugin.reloaded"));
    }

}
