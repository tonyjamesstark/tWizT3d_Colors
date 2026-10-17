package me.tWizT3d_dreaMr.colors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

public class ChatGui {
    public static void Command(Player p) {
        if (p.hasPermission("tc2.can")) {
            p.spigot().sendMessage(fin());
        }
        p.sendMessage(ChatColor.DARK_AQUA + "Click on a color below to set your chat color.");
        p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");

        if (!(p.hasPermission("tc2.can"))) {
            p.spigot().sendMessage(fin());
            p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
            return;
        }
        TextComponent Builder = new TextComponent("");
        int i = 0;
        List<String> colors = new ArrayList<>();
        colors.add("a");
        colors.add("b");
        colors.add("c");
        colors.add("d");
        colors.add("e");
        colors.add("f");
        colors.add("1");
        colors.add("2");
        colors.add("3");
        colors.add("4");
        colors.add("5");
        colors.add("6");
        colors.add("7");
        colors.add("8");
        colors.add("9");
        colors.add("0");
        for (String col : colors) {
            if (i >= 7) {
                p.spigot().sendMessage(Builder);
                Builder = new TextComponent("");
                i = 0;
            }
            if (ColorCatalog.hasPermission(ColorCatalog.vanillaOption(col), p::hasPermission)) {
                if (i != 0) {
                    Builder.addExtra(new TextComponent(ChatColor.GRAY + ", "));
                }
                Builder.addExtra(
                        MessageBuilder2(
                                ChatColor.translateAlternateColorCodes('&', "&" + col) + col, col));
                i++;
            }
        }
        if (i != 0) {
            p.spigot().sendMessage(Builder);
        }
        Builder = new TextComponent("");
        i = 0;
        Set<String> colol = color.getColorsCodes();
        Map<String, ColorOption> customPerms = new HashMap<>();
        for (ColorOption o : ColorCatalog.customOptions()) customPerms.put(o.id(), o);
        for (String col : colol) {
            if (i >= 7) {
                p.spigot().sendMessage(Builder);
                Builder = new TextComponent("");
                i = 0;
            }
            ColorOption custom = customPerms.get(col);
            if (custom != null && ColorCatalog.hasPermission(custom, p::hasPermission)) {
                if (i != 0) Builder.addExtra(new TextComponent(ChatColor.GRAY + ", "));
                if (color.isRandomColor(col)) {
                    Builder.addExtra(MessageBuilder("random", color.randomHexString(), col));
                } else Builder.addExtra(MessageBuilder("&" + col, color.getColorString(col), col));
                i++;
            }
        }
        if (i != 0) {
            p.spigot().sendMessage(Builder);
        }
        Builder = new TextComponent("");
        i = 0;
        p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
        colol = color.getGradCodes();
        Map<String, ColorOption> gradPerms = new HashMap<>();
        for (ColorOption o : ColorCatalog.gradientOptions()) gradPerms.put(o.id(), o);
        for (String col : colol) {
            if (i >= 3) {
                p.spigot().sendMessage(Builder);
                Builder = new TextComponent("");
                i = 0;
            }
            ColorOption grad = gradPerms.get(col);
            if (grad != null && ColorCatalog.hasPermission(grad, p::hasPermission)) {
                if (i != 0) Builder.addExtra(new TextComponent(ChatColor.GRAY + ", "));
                Builder.addExtra(MessageBuilderGrad1("&" + col + " -", col));
                Builder.addExtra(MessageBuilderGrad2("> " + "&" + col, col));
                i++;
            }
        }
        if (i != 0) {
            p.spigot().sendMessage(Builder);
        }
        p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
        p.spigot().sendMessage(fin());
        p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
    }

    public static TextComponent MessageBuilder(String Message, String hex, String col) {
        TextComponent message = new TextComponent(Message);
        if (!hex.isEmpty()) message.setColor(color.getColor(col));
        message.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setcolor set " + col));
        message.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(ChatColor.AQUA + "Set your chat to this color")));
        return message;
    }

    public static TextComponent MessageBuilder2(String Message, String col) {
        TextComponent message = new TextComponent(Message);
        message.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setcolor set " + col));
        message.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(color.getColor(col) + "Set your chat to this color")));
        return message;
    }

    public static TextComponent MessageBuilderGrad1(String Message, String col) {
        TextComponent message = new TextComponent(Message);
        message.setColor(ChatColor.of(color.getGrads(col).get(0)));
        message.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setcolor setg " + col));
        message.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(ChatColor.AQUA + "Set your chat to this color")));
        return message;
    }

    public static TextComponent MessageBuilderGrad2(String Message, String col) {
        TextComponent message = new TextComponent(Message);
        message.setColor(ChatColor.of(color.getGrads(col).get(1)));
        message.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setcolor setg " + col));
        message.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(ChatColor.AQUA + "Set your chat to this color")));
        return message;
    }

    public static TextComponent fin() {
        TextComponent message = new TextComponent(ChatColor.RED + "Remove");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setcolor remove"));
        message.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Remove your chat color")));
        return message;
    }
}
