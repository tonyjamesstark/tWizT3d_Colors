package me.tWizT3d_dreaMr.colors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class colorFile {
    private static File Config;
    private static YamlConfiguration config;
    private static File ColorCodeFile;
    private static YamlConfiguration ColorCodeFileconfig;

    public static void enable() {

        color.init();
        if (!(new File(main.plugin.getDataFolder(), "Color.yml").exists())) {
            System.out.println("Lang File doesn't exist. Attempting to create the file");
            try {
                new File(main.plugin.getDataFolder(), "Color.yml").createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Could not create Color file");
            }
        }
        Config = new File(main.plugin.getDataFolder(), "Color.yml");
        config = YamlConfiguration.loadConfiguration(Config);
        try {
            config.save(Config);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!config.contains("Config")) {
            try {
                createDef();
                config.save(Config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!(new File(main.plugin.getDataFolder(), "ColorsCommand.yml").exists())) {
            System.out.println("ColorsCommand File doesn't exist. Attempting to create the file");
            try {
                new File(main.plugin.getDataFolder(), "ColorsCommand.yml").createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Could not create Color file");
            }
        }
        ColorCodeFile = new File(main.plugin.getDataFolder(), "ColorsCommand.yml");
        ColorCodeFileconfig = YamlConfiguration.loadConfiguration(ColorCodeFile);
        try {
            ColorCodeFileconfig.save(ColorCodeFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!ColorCodeFileconfig.contains("identifier")) {
            try {
                createDef2();
                ColorCodeFileconfig.save(ColorCodeFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String s : config.getConfigurationSection("ColorCodes").getKeys(false)) {
            color.addColor(s, config.getString("ColorCodes." + s));
        }
        for (String s : config.getConfigurationSection("GradientCodes").getKeys(false)) {
            color.addGrad(s, config.getString("GradientCodes." + s));
        }
    }

    private static void createDef() {
        config.set("Config", "1");
        config.set("ColorCodes.j", "ff6600");
        config.set("GradientCodes.gr", "ff00ff ffff00");
    }

    private static void createDef2() {
        ColorCodeFileconfig.set("identifier", "&");
        ArrayList<String> ret = new ArrayList<>();
        ret.add("!z!m                                                 ");
        ret.add(
                "        !c!lM!e!li!a!ln!b!le!9!lc!d!lr!c!la!e!lf!a!lt!b!l"
                        + " !9!lC!d!lo!c!ll!e!lo!a!lr!b!l !9!lC!d!lo!c!ld!e!le!a!ls");
        ret.add("  &f " + "&1 " + "&2 " + "&3 " + "&4 " + "&5 " + "&6 " + "&7 ");
        ret.add(" &8 " + "&9 " + "&e " + "&d " + "&c " + "&b " + "&a " + "&0* ");
        ret.add("        &l" + " BOLD " + "&o " + "ITALIC");
        ret.add("   &m " + "STRIKE" + " &n " + "ULINE" + " &r");
        ret.add("!z!m                                                 ");
        ret.add(
                "         "
                        + "!zC"
                        + "!zu"
                        + "!zs"
                        + "!zt"
                        + "!zo"
                        + "!zm"
                        + " "
                        + "!zC"
                        + "!zo"
                        + "!zl"
                        + "!zo"
                        + "!zr"
                        + "!zs");
        ColorCodeFileconfig.set("Start", ret);
        ColorCodeFileconfig.set("Middle", new ArrayList<String>());

        ArrayList<String> ret2 = new ArrayList<>();
        ret2.add("!z!m                                                 ");
        ret2.add(ChatColor.translateAlternateColorCodes('&', "&3* &f&lCustoms with ** is random."));
        ret2.add("!z!m                                                 ");

        ColorCodeFileconfig.set("End", ret2);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<String> colorcommand(boolean custom) {

        ArrayList<String> ret = new ArrayList<>();
        for (String s :
                (List<String>) ColorCodeFileconfig.getList("Start", Collections.emptyList()))
            ret.add(setT(s));
        if (!custom) {
            return ret;
        }
        for (String s :
                (List<String>) ColorCodeFileconfig.getList("Middle", Collections.emptyList()))
            ret.add(setT(s));
        int i = 1;
        StringBuilder temp = new StringBuilder("  ");

        for (String s : color.getColorsCodes()) {

            if (i == 8) {
                if (color.isRandomColor(s)) temp.append(r()).append("&").append(s).append("** ");
                else temp.append(color.getColor(s)).append("&").append(s);
                ret.add(temp.toString());
                temp = new StringBuilder("  ");
                i = 0;
            } else {
                if (color.isRandomColor(s)) temp.append(r()).append("&").append(s).append("** ");
                else temp.append(color.getColor(s)).append("&").append(s).append(" ");
                i++;
            }
        }
        if (!temp.toString().equals("  ")) {
            ret.add(temp.toString());
        }
        temp = new StringBuilder("  ");

        if (!temp.toString().equals("  ")) {
            ret.add(temp.toString());
        }

        for (String s : (List<String>) ColorCodeFileconfig.getList("End", Collections.emptyList()))
            ret.add(setT(s));

        //		&c&e&a&b&9&d

        return ret;
    }

    public static ChatColor r() {
        return color.randomColor();
    }

    public static String setT(String s) {
        String identifier =
                ColorCodeFileconfig.getString("identifier"); // get the identifier from config
        s = s.replace(identifier + "1", ChatColor.DARK_BLUE + "&1" + ChatColor.WHITE);
        s = s.replace(identifier + "2", ChatColor.DARK_GREEN + "&2" + ChatColor.WHITE);
        s = s.replace(identifier + "3", ChatColor.DARK_AQUA + "&3" + ChatColor.WHITE);
        s = s.replace(identifier + "4", ChatColor.DARK_RED + "&4" + ChatColor.WHITE);
        s = s.replace(identifier + "5", ChatColor.DARK_PURPLE + "&5" + ChatColor.WHITE);
        s = s.replace(identifier + "6", ChatColor.GOLD + "&6" + ChatColor.WHITE);
        s = s.replace(identifier + "7", ChatColor.GRAY + "&7" + ChatColor.WHITE);
        s = s.replace(identifier + "8", ChatColor.DARK_GRAY + "&8" + ChatColor.WHITE);
        s = s.replace(identifier + "9", ChatColor.BLUE + "&9" + ChatColor.WHITE);
        s = s.replace(identifier + "0", ChatColor.BLACK + "&0" + ChatColor.WHITE);

        s = s.replace(identifier + "a", ChatColor.GREEN + "&a" + ChatColor.WHITE);
        s = s.replace(identifier + "b", ChatColor.AQUA + "&b" + ChatColor.WHITE);
        s = s.replace(identifier + "c", ChatColor.RED + "&c" + ChatColor.WHITE);
        s = s.replace(identifier + "d", ChatColor.LIGHT_PURPLE + "&d" + ChatColor.WHITE);
        s = s.replace(identifier + "e", ChatColor.YELLOW + "&e" + ChatColor.WHITE);
        s = s.replace(identifier + "f", ChatColor.WHITE + "&f" + ChatColor.WHITE);

        s = s.replace(identifier + "l", ChatColor.BOLD + "&l" + ChatColor.WHITE);
        s = s.replace(identifier + "m", ChatColor.STRIKETHROUGH + "&m" + ChatColor.WHITE);
        s = s.replace(identifier + "n", ChatColor.UNDERLINE + "&n" + ChatColor.WHITE);
        s = s.replace(identifier + "o", ChatColor.ITALIC + "&o" + ChatColor.WHITE);
        s = s.replace(identifier + "r", ChatColor.WHITE + "&r" + ChatColor.WHITE);

        return s;
    }

    public static void addColor(String code, String Hex) {
        config.set("ColorCodes." + code, Hex);
        try {
            config.save(Config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        color.addColor(code, Hex);
    }

    public static boolean isColorCode(String s) {
        if (s.length() == 1
                && "0123456789abcdeflominkr".indexOf(Character.toLowerCase(s.charAt(0))) >= 0)
            return true;
        return color.isColor(s);
    }
}
