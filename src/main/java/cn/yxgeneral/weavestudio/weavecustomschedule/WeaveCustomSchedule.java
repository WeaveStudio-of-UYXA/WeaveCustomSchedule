/*
* Weave Custom Schedule (SpigotMC Plugin)
*
* */
package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WeaveCustomSchedule extends JavaPlugin {
    static WeaveCustomSchedule Instance;
    static boolean PAPI = false;
    static WCSCommandHandler CHandler = null;
    static WCSEventHandler EHandler = null;
    static boolean JustStarted = true;
    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;
        if (getServer().getPluginManager().getPlugin("PlaceHolderAPI")!=null){
            info("PlaceHolderAPI found, enabling PlaceHolderAPI support...");
            PAPI = true;
        }else{
            warning("PlaceHolderAPI not found, disabling PlaceHolderAPI support...");
            PAPI = false;
        }
        CHandler = new WCSCommandHandler();
        Bukkit.getPluginCommand("weavecustomschedule").setExecutor(CHandler);
        Bukkit.getPluginCommand("weavecustomschedule").setTabCompleter(CHandler);
        EHandler = new WCSEventHandler();
        Bukkit.getPluginManager().registerEvents(EHandler, this);
        WCSConfigManager.initConfig(false);
        WCSTimer.start();
        JustStarted = false;
        WCSInteractExecutor.gInfo(WCSConfigManager.getTranslation("plugin.loaded"));
    }
    public static boolean isJustStarted(){
        return JustStarted;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static WeaveCustomSchedule getInstance() {
        return Instance;
    }
    public static void info(String info){
        Instance.getLogger().info(info);
    }
    public static void warning(String warning){
        Instance.getLogger().warning(warning);
    }
}
