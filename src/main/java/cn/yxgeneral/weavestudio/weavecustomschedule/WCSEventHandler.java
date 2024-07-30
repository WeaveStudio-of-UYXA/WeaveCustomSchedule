package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class WCSEventHandler implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        WCSContainerManager.updateCountdownAvailablePlayers();
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        WCSContainerManager.updateCountdownAvailablePlayers();
    }
}
