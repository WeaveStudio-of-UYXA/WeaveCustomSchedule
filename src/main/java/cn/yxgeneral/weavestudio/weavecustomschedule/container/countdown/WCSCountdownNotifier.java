package cn.yxgeneral.weavestudio.weavecustomschedule.container.countdown;

import cn.yxgeneral.weavestudio.weavecustomschedule.*;
import org.bukkit.Color;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class WCSCountdownNotifier {
    private final WCSCountdownEventContainer ParentCountdown;
    private final List<Color> ColorBar = new ArrayList<>();
    private final List<Player> AvailablePlayers = new ArrayList<>();
    private String NoticeMode;
    private String BossbarModeText;
    private Map<Integer, String> BroadcastAndTitleModeText;
    private List<Integer> BroadcastAndTitleModeTextKeys;
    private Integer LastTextIndex = 0;
    private BossBar bossBar = null;
    private Integer CurrentTick = 0;
    public WCSCountdownNotifier(WCSCountdownEventContainer parentContainer, ConfigurationSection config){
        ParentCountdown = parentContainer;
        initFromConfig(config);
    }
    public void initFromConfig(ConfigurationSection config){
        setColorBar(config.getStringList("colorbar"));
        setNoticeMode(config.getString("mode"));
        setBossbarModeText(config.getString("bossbarModeText"));
        Map<Integer, String> broadcastAndTitleModeText = new HashMap<>();
        Set<String> broadcastAndTitleModeTextKeys =
                config.getConfigurationSection("broadcastAndTitleModeText").getKeys(false);
        for (String key : broadcastAndTitleModeTextKeys) {
            broadcastAndTitleModeText.put(Integer.parseInt(key), config.getString("broadcastAndTitleModeText." + key));
        }
        setBroadcastAndTitleModeText(broadcastAndTitleModeText);
    }
    public void setColorBar(List<String> colorBar){
        ColorBar.clear();
        for (String color : colorBar){
            if (color.startsWith("#")){
                color = color.substring(1);
            }
            ColorBar.add(Color.fromRGB(Integer.parseInt(color, 16)));
        }
    }
    public void setNoticeMode(String mode){
        NoticeMode = mode;
        if ("bossbar".equals(mode)||"all".equals(mode)){
            bossBar = WeaveCustomSchedule.getInstance().getServer().createBossBar(
                    "", BarColor.WHITE, BarStyle.SOLID);
            bossBar.setVisible(false);
        }
        updateAvailablePlayer();
    }
    public void setBossbarModeText(String text){
        BossbarModeText = text;
        if (bossBar != null){
            bossBar.setTitle(text);
        }
    }
    public void hideBossbar(){
        if (bossBar != null) {
            bossBar.setVisible(false);
        }
    }
    public void showBossbar(){
        if (bossBar != null) {
            bossBar.setVisible(true);

        }
    }
    public void setBroadcastAndTitleModeText(Map<Integer, String> text){
        BroadcastAndTitleModeText = text;
        ArrayList<Integer> keys = new ArrayList<>(text.keySet());
        // big to small
        keys.sort(Collections.reverseOrder());
        BroadcastAndTitleModeTextKeys = keys;
        LastTextIndex = 0;
    }
    public void updateAvailablePlayer(){
        //get All online player
        AvailablePlayers.clear();
        for(Player player : WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers()){
            if (WCSPermission.receiveCountdown(player, ParentCountdown.getContainerID())){
                AvailablePlayers.add(player);
            }
        }
        if (bossBar != null){
            bossBar.removeAll();
            for (Player player : AvailablePlayers){
                bossBar.addPlayer(player);
            }
        }
    }
    private BarColor getBarColor(Double percent){
        if (percent>=0.75){
            return BarColor.BLUE;
        }else if (percent>=0.5) {
            return BarColor.GREEN;
        }else if (percent>=0.25){
            return BarColor.YELLOW;
        }else{
            return BarColor.RED;
        }
    }
    private Color getColor(Double percent){
        percent = 1.0 - percent;
        double spaceWidth = 1.0 / (ColorBar.size()-1);
        int spaceIndex = (int)(percent / spaceWidth);
        spaceIndex = Math.min(spaceIndex, ColorBar.size() - 2);
        int leftIndex = spaceIndex;
        int rightIndex = spaceIndex + 1;
        double leftPercent = (percent - spaceWidth * spaceIndex) / spaceWidth;
        double rightPercent = 1.0 - leftPercent;
        return Color.fromRGB(
                (int)(ColorBar.get(rightIndex).getRed() * leftPercent + ColorBar.get(leftIndex).getRed() * rightPercent),
                (int)(ColorBar.get(rightIndex).getGreen() * leftPercent + ColorBar.get(leftIndex).getGreen() * rightPercent),
                (int)(ColorBar.get(rightIndex).getBlue() * leftPercent + ColorBar.get(leftIndex).getBlue() * rightPercent)
        );
    }
    public void update(String nextEventName, Integer currentTick, Integer totalTick){
        CurrentTick += 1;
        if (CurrentTick >= WCSConfigManager.getCountdownNotifierInterval()){
            CurrentTick = 0;
            switch (NoticeMode){
                case "none":
                    return;
                case "bossbar":
                    updateBossbar(currentTick, totalTick);
                    break;
                case "broadcast":
                case "title":
                    checkBroadcastAndTitleModeCountdownIndex(nextEventName, currentTick, totalTick);
                    break;
                case "all":
                    updateBossbar(currentTick, totalTick);
                    checkBroadcastAndTitleModeCountdownIndex(nextEventName, currentTick, totalTick);
                    break;
            }
        }
    }
    public void checkBroadcastAndTitleModeCountdownIndex(String nextEventName, Integer currentTick, Integer totalTick){
        double seconds = (double)(totalTick - currentTick) / 20;
        if (seconds > BroadcastAndTitleModeTextKeys.getFirst()){
            LastTextIndex = 0;
            return;
        }
        if (LastTextIndex>=BroadcastAndTitleModeTextKeys.size()){
            return;
        }
        if (seconds<=BroadcastAndTitleModeTextKeys.get(LastTextIndex)){
            int index = BroadcastAndTitleModeTextKeys.get(LastTextIndex);
            String msg = WCSUtils.getMCColorString(getColor(1.0-(double)currentTick / totalTick)) +
                    ParentCountdown.applyPlaceHolder(BroadcastAndTitleModeText.get(index));
            if ("broadcast".equals(NoticeMode)){
                for (Player player : AvailablePlayers){
                    WCSInteractExecutor.sendPrefixMessage(player, ParentCountdown.applyPlaceHolder(msg));
                }
            }else if ("title".equals(NoticeMode)){
                for (Player player : AvailablePlayers){
                    WCSInteractExecutor.displayTitle(player, nextEventName , ParentCountdown.applyPlaceHolder(msg), 20, 20, 5);
                }
            }else if ("all".equals(NoticeMode)){
                for (Player player : AvailablePlayers){
                    WCSInteractExecutor.sendPrefixMessage(player, ParentCountdown.applyPlaceHolder(msg));
                    WCSInteractExecutor.displayTitle(player, nextEventName , ParentCountdown.applyPlaceHolder(msg), 20, 20, 5);
                }
            }
            LastTextIndex += 1;
        }
    }
    public void updateBossbar(Integer currentTick, Integer totalTick){
        if (bossBar == null){
            return;
        }
        bossBar.setProgress((double)currentTick / totalTick);
        bossBar.setColor(getBarColor(1.0-(double)currentTick / totalTick));
        String msg = WCSUtils.getMCColorString(getColor(1.0-(double)currentTick / totalTick)) +
                        ParentCountdown.applyPlaceHolder(BossbarModeText);
        bossBar.setTitle(msg);
    }
}