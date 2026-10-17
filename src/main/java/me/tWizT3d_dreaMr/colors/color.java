package me.tWizT3d_dreaMr.colors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class color {
    private static HashMap<String, ChatColor> colors;
    private static HashMap<String, String[]> grads;

    public static void addColor(String key, String hex) {
        if (hex.equalsIgnoreCase("random") || hex.equalsIgnoreCase("landom")) {
            colors.put(key, null);
        }
        key = key.replace("&", "");
        hex = hex.replace("#", "");
        hex = hex.replace("&", "");
        hex = "&#" + hex;
        if (Formatter.isHex(hex)) {
            ChatColor end = ChatColor.of(hex.replace("&", ""));
            if (end != null) {
                colors.put(key, end);
            }
        }
    }

    public static void addGrad(String key, String hexs) {
        String hex2;
        key = key.replace("&", "");
        hexs = hexs.replace("#", "");
        hexs = hexs.replace(" ", "");
        hexs = hexs.replace("&", "");
        hex2 = hexs.substring(0, 6);
        hexs = hexs.substring(6);
        hexs = "#" + hexs;
        hex2 = "#" + hex2;
        if ((Formatter.isHex("&" + hexs)
                        || (hexs.equalsIgnoreCase("#random") || hexs.equalsIgnoreCase("#landom")))
                && (Formatter.isHex("&" + hex2)
                        || (hexs.equalsIgnoreCase("#random")
                                || hexs.equalsIgnoreCase("#landom")))) {
            String[] end = new String[] {hex2, hexs};
            grads.put(key, end);
        }
    }

    public static ChatColor getColor(String key) {
        key = key.replace("&", "");
        ChatColor get = colors.get(key);
        if (get == null) {
            get = randomColor();
        }
        return get;
    }

    public static ArrayList<String> getGrads(String key) {
        key = key.replace("&", "");
        String[] get = grads.get(key);
        ArrayList<String> ret = new ArrayList<>();
        for (String s : get) {
            if (s.equalsIgnoreCase("#random")) s = "#" + randomHexString();
            if (s.equalsIgnoreCase("#landom")) s = "#" + randomLightHexString();
            ret.add(s);
        }
        return ret;
    }

    public static String getColorString(String key) {
        key = key.replace("&", "");
        ChatColor get = colors.get(key);
        if (get == null) {
            return "#" + randomHexString();
        }
        Color c = get.getColor();

        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static ChatColor randomColor() {
        String hex = "#" + randomHexString();
        return ChatColor.of(hex);
    }

    public static ChatColor randomLColor() {
        String hex = "#" + randomLightHexString();
        return ChatColor.of(hex);
    }

    public static String randomHexString() {
        String characters = "abcdef1234567890";
        StringBuilder hex = new StringBuilder();
        while (hex.length() != 6) {
            int r = random(characters.length());
            if (r != characters.length()) hex.append(characters.charAt(r));
            else hex.append(characters.substring(r));
        }
        return hex.toString();
    }

    public static String randomLightHexString() {
        String characters = "abcdef6789";
        StringBuilder hex = new StringBuilder();
        while (hex.length() != 6) {
            int r = random(characters.length());
            if (r != characters.length()) hex.append(characters.charAt(r));
            else hex.append(characters.substring(r));
        }
        return hex.toString();
    }

    private static int random(int x) {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt((x + 1));
    }

    public static Set<String> getColorsCodes() {
        return colors.keySet();
    }

    public static Set<String> getGradCodes() {
        return grads.keySet();
    }

    public static Collection<ChatColor> getColors() {
        return colors.values();
    }

    public static void init() {
        colors = new HashMap<>();
        grads = new HashMap<>();
    }

    public static boolean isColor(String s) {
        return colors.containsKey(s)
                || (s.length() == 1 && "0123456789abcdef".indexOf(s.charAt(0)) >= 0);
    }

    public static boolean isGrad(String s) {
        return grads.containsKey(s.replace("&", "")) || grads.containsKey(s.replace("!", ""));
    }

    public static boolean isRandomColor(String s) {
        return colors.get(s) == null;
    }

    public static String replaceAllGrad(String s) {
        for (String key : grads.keySet()) {
            s = s.replace("&" + key, "");
            s = s.replace("!" + key, "");
        }
        return s;
    }

    public static String ColorfyString(String message, Player p, String Action, String Char) {
        message = message.replace("&x", "");
        for (String key : grads.keySet()) {
            boolean pn = p != null;
            if (pn && !(p.hasPermission("tcg." + Action + "." + key))) continue;
            if (pn
                    && main.isOFfilterOn()
                    && Action.equalsIgnoreCase("chat")
                    && !p.hasPermission("coreprotect.inspect")) {
                for (String fi : main.oFilter()) {
                    message = message.replaceAll("(?i)&" + fi, "");
                }
            }
            while (message.contains(Char + key)) {
                String beg = message.substring(0, message.indexOf(Char + key));
                message = message.substring(message.indexOf(Char + key) + (Char + key).length());
                String[] hxs = grads.get(key);
                String[] hxs2 = new String[] {hxs[0], hxs[1]};

                if (hxs2[0].equalsIgnoreCase("#random")) {
                    hxs2[0] = "#" + randomHexString();
                }
                if (hxs2[1].equalsIgnoreCase("#random")) {
                    hxs2[1] = "#" + randomHexString();
                }
                if (hxs2[0].equalsIgnoreCase("#landom")) {
                    hxs2[0] = "#" + randomLightHexString();
                }
                if (hxs2[1].equalsIgnoreCase("#landom")) {
                    hxs2[1] = "#" + randomLightHexString();
                }
                message = beg + gradientItem.gradString(hxs2, message);
            }
        }
        for (String key : colors.keySet()) {
            if (p != null && !(p.hasPermission("tc." + Action + "." + key))) continue;
            if (message.contains(Char + key)) {
                if (isRandomColor(key)) {
                    while (message.contains(Char + key))
                        message =
                                message.replaceFirst(
                                        Pattern.quote(Char + key),
                                        Matcher.quoteReplacement(getColor(key).toString()));
                } else message = message.replace(Char + key, "" + getColor(key));
            }
        }
        if (p != null
                && Action.equalsIgnoreCase("chat")
                && !p.hasPermission("coreprotect.inspect")
                && main.isFFilterOn()) {
            for (String fi : main.fFilter()) {
                message = message.replaceAll("(?i)&" + fi, "");
            }
        }
        if (p != null && (!Action.equalsIgnoreCase("chat") || p.hasPermission("tc.Chat.Defaults")))
            message = ChatColor.translateAlternateColorCodes(Char.toCharArray()[0], message);
        else if (p == null)
            message = ChatColor.translateAlternateColorCodes(Char.toCharArray()[0], message);
        return message;
    }
}
