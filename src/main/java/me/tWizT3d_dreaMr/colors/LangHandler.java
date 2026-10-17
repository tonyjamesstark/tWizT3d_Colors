package me.tWizT3d_dreaMr.colors;

import java.io.File;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LangHandler {
    public static FileConfiguration config;
    public static File Config;

    public static void enable() {
        if (!(new File(main.plugin.getDataFolder(), "Lang.yml").exists())) {
            System.out.println("Lang File doesn't exist. Attempting to create the file");
            try {
                new File(main.plugin.getDataFolder(), "Lang.yml").createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Could not create Lang file");
            }
        }
        Config = new File(main.plugin.getDataFolder(), "Lang.yml");
        config = YamlConfiguration.loadConfiguration(Config);
        try {
            config.save(Config);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!config.contains("Config")) {
            config = defaultSet(config);
            try {
                config.save(Config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static FileConfiguration defaultSet(FileConfiguration conf) {
        conf.set("Config", true);
        // General
        conf =
                set(
                        conf,
                        "General",
                        "NoPerms",
                        "&cYou do not have permission to perform this command");
        conf = set(conf, "General", "NonSupportedSender", "&cCommandSender not supported");
        conf = set(conf, "General", "Reload", "&8Reloaded tWizT3dColors Configs");
        conf = set(conf, "General", "ArgsLong", "&cLength too long");
        conf = set(conf, "General", "ArgsShort", "&cLength too short");
        conf = set(conf, "General", "Incorrect", "&cIncorrect Usage");
        conf = set(conf, "General", "PlayersOnly", "&cThis command can only be used by a player");
        conf = set(conf, "Grad", "Success", "&aYour lore has been updated.");
        conf = set(conf, "Grad", "NoItem", "&cGet an item first dummy");
        conf = set(conf, "Grad", "NoLore", "&chas no lore my dude");
        conf = set(conf, "Grad", "h1h2", "&cH1 or H2 null");
        conf = set(conf, "Grad", "Usage", "&cUsage: /grad >example");
        conf =
                set(
                        conf,
                        "Grad",
                        "BadHex",
                        "&c>arg is not a valid hex colour; use #rrggbb, e.g. #ff0000");
        conf = set(conf, "Grad", "BadNumber", "&c>arg is not a number");
        conf =
                set(
                        conf,
                        "Grad",
                        "LineOutOfRange",
                        "&cLine >arg is out of range; your lore has >size lines");
        conf = set(conf, "Grad", "RangeTooSmall", "&cA 3-colour gradient needs at least 2 lines");
        conf = set(conf, "Colorme", "Usage", "&cUsage: /colorme >example");
        conf = set(conf, "Colorme", "EmptyCode", "&cThe colour code cannot be empty");
        conf =
                set(
                        conf,
                        "Colorme",
                        "VanillaCode",
                        "&c>arg is already a vanilla colour or format code (0-9, a-f, k-o, r)");
        conf =
                set(
                        conf,
                        "Colorme",
                        "BadHex",
                        "&c>arg is not a valid hex colour; use 6 hex digits with no #, e.g."
                                + " ff6600");
        conf = set(conf, "Colorme", "Success", "&aYou have set &f>id &ato that colour");
        conf = set(conf, "Setcolor", "UsageSet", "&cUsage: /setcolor set >example");
        conf = set(conf, "Setcolor", "UsageSetg", "&cUsage: /setcolor setg >example");
        conf = set(conf, "Setcolor", "UsageRemove", "&cUsage: /setcolor remove");
        conf = set(conf, "Setcolor", "UsageChat", "&cUsage: /setcolor chat");
        conf =
                set(
                        conf,
                        "Setcolor",
                        "UnknownSub",
                        "&cUnknown subcommand '>arg'. Try /setcolor help");
        conf = set(conf, "Setcolor", "HelpHeader", "&8Setcolor commands:");
        conf =
                set(
                        conf,
                        "Setcolor",
                        "DescSet",
                        "&eset &7- Set your chat colour to a code or hex colour");
        conf = set(conf, "Setcolor", "DescSetg", "&esetg &7- Set your chat colour to a gradient");
        conf = set(conf, "Setcolor", "DescRemove", "&eremove &7- Remove your chat colour");
        conf = set(conf, "Setcolor", "DescChat", "&echat &7- Open the old clickable colour menu");
        conf = set(conf, "Setcolor", "DescHelp", "&ehelp &7- Show this help");
        // Gui
        conf = set(conf, "Gui", "Title", "&8Chat Colour");
        conf = set(conf, "Gui", "Remove", "&cRemove");
        conf = set(conf, "Gui", "Close", "&7Close");
        conf = set(conf, "Gui", "Prev", "&e< Prev");
        conf = set(conf, "Gui", "Next", "&e Next >");
        conf = set(conf, "Gui", "PagePrefix", "&7Page >cur/>max");
        conf = set(conf, "Gui", "PreviewName", "&ePreview");
        conf = set(conf, "Gui", "PreviewLoreNone", "&7No colour set");
        conf = set(conf, "Gui", "SampleText", "Hello");
        conf = set(conf, "Gui", "Applied", "&aYour chat colour is now >id");
        conf = set(conf, "Gui", "Removed", "&aYour chat colour has been removed");
        conf = set(conf, "Gui", "NoPerms", "&cYou do not have permission for that colour");
        conf = set(conf, "Gui", "StoreUnavailable", "&cColour storage is unavailable right now");
        conf = set(conf, "Gui", "StoreError", "&cSomething went wrong saving your colour");
        conf = set(conf, "Gui", "AdminLockedLore", "&7Locked - admin override");

        conf.options().copyDefaults(true);
        return conf;
    }

    private static FileConfiguration set(
            FileConfiguration conf, String Plugin, String part, String rest) {
        conf.addDefault(Plugin + "." + part, rest);
        return conf;
    }

    public static String get(String plugin, String part, String key, String keyRep) {
        if (config.get(plugin + "." + part) == null) {
            System.out.print("Missing config section " + plugin + "." + part);
            config = defaultSet(config);
            try {
                config.save(Config);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String out = config.getString(plugin + "." + part);
        out = out.replaceAll("&", ";");
        out = out.replaceAll("<", "\n");
        if (!(key == null)) {
            out = replaceKey(out, keyRep, key);
        }
        out = ChatColor.translateAlternateColorCodes(';', out);

        return out;
    }

    public static Component componentOf(String rawColoured) {
        return LegacyComponentSerializer.legacySection().deserialize(rawColoured);
    }

    public static Component component(String plugin, String part, String key, String keyRep) {
        return componentOf(get(plugin, part, key, keyRep));
    }

    public static void reloadLang() {
        Config = new File(main.plugin.getDataFolder(), "Lang.yml");
        config = YamlConfiguration.loadConfiguration(Config);
    }

    public static String replaceKey(String str, String with, String key) {
        key = ">" + key;
        str = str.replace(key, with);
        return str;
    }
}
