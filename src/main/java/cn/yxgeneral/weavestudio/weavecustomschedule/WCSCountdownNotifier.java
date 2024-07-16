package cn.yxgeneral.weavestudio.weavecustomschedule;

import org.bukkit.Color;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class WCSCountdownNotifier {
    private String EventID;
    private List<Color> ColorBar = new ArrayList<>();
    private List<Player> AvailablePlayers = new ArrayList<>();
    private String NoticeMode;
    private String BossbarModeText;
    private Map<Integer, String> BroadcastAndTitleModeText;
    private BossBar bossBar = null;
    public WCSCountdownNotifier(String eventID, List<String> colorBar){
        EventID = eventID;
        for (String color : colorBar){
            if (color.startsWith("#")){
                color = color.substring(1);
            }
            ColorBar.add(Color.fromRGB(Integer.parseInt(color, 16)));
        }
    }
    public void setNoticeMode(String mode){
        NoticeMode = mode;
        if ("bossbar".equals(mode)){
            bossBar = WeaveCustomSchedule.getInstance().getServer().createBossBar(
                    "", BarColor.WHITE, BarStyle.SOLID);
        }
    }
    public void setBossbarModeText(String text){
        BossbarModeText = text;
        if (bossBar != null){
            bossBar.setTitle(text);
        }
    }
    public void setBroadcastAndTitleModeText(Map<Integer, String> text){
        BroadcastAndTitleModeText = text;
    }
    public void updateAvailablePlayer(){
        //get All online player
        AvailablePlayers.clear();
        ArrayList<Player> onlinePlayers = new ArrayList<>();
        Collections.addAll(onlinePlayers, (Player[])
                WeaveCustomSchedule.getInstance().getServer().getOnlinePlayers().toArray());
        for(Player player : onlinePlayers){
            if (WCSPermission.receiveCountdown(player, EventID)){
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

    }
    private Color getColor(Double percent){
        double indexWidth = 1.0 / ColorBar.size();
        int cIndex = ((Double)(percent *ColorBar.size())).intValue();
        int nIndex = cIndex == ColorBar.size() ? cIndex - 1 : cIndex;
        double cColorPercent = (percent - cIndex * indexWidth) / indexWidth;
        double nColorPercent = 1 - cColorPercent;
        return Color.fromRGB(
                (int)(ColorBar.get(cIndex).getRed() * cColorPercent + ColorBar.get(nIndex).getRed() * nColorPercent),
                (int)(ColorBar.get(cIndex).getGreen() * cColorPercent + ColorBar.get(nIndex).getGreen() * nColorPercent),
                (int)(ColorBar.get(cIndex).getBlue() * cColorPercent + ColorBar.get(nIndex).getBlue() * nColorPercent)
        );
    }
    public void updateBossbar(Integer currentTick, Integer totalTick){
        if (bossBar == null){
            return;
        }
        bossBar.setProgress((double)currentTick / totalTick);
        bossBar.setColor(getColor((double)currentTick / totalTick));
    }
}
