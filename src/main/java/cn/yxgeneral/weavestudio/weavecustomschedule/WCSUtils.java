package cn.yxgeneral.weavestudio.weavecustomschedule;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.regex.*;

public class WCSUtils {
    public static String getMCColorString(Color color){
        StringBuilder rawColorString = new StringBuilder(Integer.toHexString(color.asRGB()));
        while(rawColorString.length()<6){
            rawColorString.insert(0, "0");
        }
        StringBuilder mcColorString = new StringBuilder("§x");
        for (int i = 0; i < rawColorString.length(); i++){
            mcColorString.append("§").append(rawColorString.charAt(i));
        }
        return mcColorString.toString();
    }
    public static String getConsoleColorString(Color color){
        return "\033[38;2;"+color.getRed()+";"+color.getGreen()+";"+color.getBlue()+"m";
    }
    public static String applyConsoleColorCode(String str){
        str = str.replace("§", "&");
        Pattern pattern;
        //match &x&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]
        //eg. &x&6&6&C&C&F&F
        pattern = Pattern.compile("&x&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()){
            String colorCode = matcher.group();
            String hexCode = colorCode.replace("&x", "").replace("&", "");
            int red = Integer.parseInt(hexCode.substring(0, 2), 16);
            int green = Integer.parseInt(hexCode.substring(2, 4), 16);
            int blue = Integer.parseInt(hexCode.substring(4, 6), 16);
            Color color = Color.fromRGB(red, green, blue);
            str = str.replace(colorCode, getConsoleColorString(color));
        }
        //match &[0-9a-fA-F]{2}
        pattern = Pattern.compile("&[0-9a-fA-F]");
        matcher = pattern.matcher(str);
        while (matcher.find()){
            String colorCode = matcher.group();
            switch (colorCode){
                case "&0":
                    str = str.replace(colorCode, "\033[0;30m");
                    break;
                case "&1":
                    str = str.replace(colorCode, "\033[0;34m");
                    break;
                case "&2":
                    str = str.replace(colorCode, "\033[0;32m");
                    break;
                case "&3":
                    str = str.replace(colorCode, "\033[0;36m");
                    break;
                case "&4":
                    str = str.replace(colorCode, "\033[0;31m");
                    break;
                case "&5":
                    str = str.replace(colorCode, "\033[0;35m");
                    break;
                case "&6":
                    str = str.replace(colorCode, "\033[0;33m");
                    break;
                case "&7":
                    str = str.replace(colorCode, "\033[0;37m");
                    break;
                case "&8":
                    str = str.replace(colorCode, "\033[1;30m");
                    break;
                case "&9":
                    str = str.replace(colorCode, "\033[1;34m");
                    break;
                case "&a":
                    str = str.replace(colorCode, "\033[1;32m");
                    break;
                case "&b":
                    str = str.replace(colorCode, "\033[1;36m");
                    break;
                case "&c":
                    str = str.replace(colorCode, "\033[1;31m");
                    break;
                case "&d":
                    str = str.replace(colorCode, "\033[1;35m");
                    break;
                case "&e":
                    str = str.replace(colorCode, "\033[1;33m");
                    break;
                case "&f":
                    str = str.replace(colorCode, "\033[1;37m");
                    break;
                case "&r":
                    str = str.replace(colorCode, "\033[0m");
                    break;
            }
        }
        return str.replace("&&", "&")+ "\033[0m";
    }

    public static String applyColorCode(String str){
        return str.replace("&", "§").replace("§§", "&");
    }

    public static String applyPlaceHolder(String str, @Nullable Player player){
        if (WeaveCustomSchedule.PAPI){
            return PlaceholderAPI.setPlaceholders(player, str);
        }else{
            return str;
        }
    }

    public static String applyAll(String str, @Nullable Player player){
        return applyColorCode(applyPlaceHolder(str, player));
    }
}
