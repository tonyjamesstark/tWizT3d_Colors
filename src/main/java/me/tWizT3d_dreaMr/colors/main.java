package me.tWizT3d_dreaMr.colors;

import java.util.ArrayList;
import java.util.List;
import me.tWizT3d_dreaMr.colors.Listeners.Chat;
import me.tWizT3d_dreaMr.colors.Listeners.ColorMenuListener;
import me.tWizT3d_dreaMr.colors.Listeners.Commands;
import me.tWizT3d_dreaMr.colors.Listeners.Stations.Anvil;
import me.tWizT3d_dreaMr.colors.Listeners.Stations.Signs;
import me.tWizT3d_dreaMr.colors.store.ChatColorStore;
import me.tWizT3d_dreaMr.colors.store.LuckPermsSuffixStore;
import me.tWizT3d_dreaMr.colors.store.PexSuffixStore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class main extends org.bukkit.plugin.java.JavaPlugin {
    public static FileConfiguration config;
    public static Plugin plugin;
    private static boolean bFFilter;
    private static boolean bOFilter;
    private static ArrayList<String> OFilter;
    private static ArrayList<String> FFilter;
    private static ChatColorStore colorStore;

    public void onEnable() {
        plugin = this;
        getDataFolder().mkdirs();
        LangHandler.enable();
        colorFile.enable();
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        config = getConfig();

        Bukkit.getPluginManager().registerEvents(new Anvil(), this);
        Bukkit.getPluginManager().registerEvents(new Commands(), this);
        Bukkit.getPluginManager().registerEvents(new Signs(), this);
        Bukkit.getPluginManager().registerEvents(new ColorMenuListener(), this);
        OFilter = new ArrayList<>();
        FFilter = new ArrayList<>();
        OFilter.add("Staff");
        FFilter.add("l");
        config.addDefault("FormatFilter.Enable", true);
        config.addDefault("FormatFilter.Filtered", FFilter);
        config.addDefault("OtherFilter.Enable", true);
        config.addDefault("OtherFilter.Filtered", OFilter);
        config.addDefault("permissions-plugin", "luckperms");
        config.addDefault("suffix-priority", 11);
        OFilter.clear();
        FFilter.clear();

        config.options().copyDefaults(true);
        saveConfig();
        String perms = config.getString("permissions-plugin", "luckperms").toLowerCase();
        String required =
                switch (perms) {
                    case "luckperms" -> "LuckPerms";
                    case "pex" -> "PermissionsEx";
                    default -> null;
                };
        if (required == null || !Bukkit.getPluginManager().isPluginEnabled(required)) {
            getLogger()
                    .severe(
                            required == null
                                    ? "permissions-plugin must be luckperms or pex, not " + perms
                                    : "permissions-plugin is "
                                            + perms
                                            + " but "
                                            + required
                                            + " is not enabled");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        colorStore =
                perms.equals("pex")
                        ? new PexSuffixStore()
                        : new LuckPermsSuffixStore(config.getInt("suffix-priority", 11));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Expansion(this).register();
        }
        Bukkit.getPluginManager().registerEvents(new Chat(), this);
        OFilter.addAll(config.getStringList("OtherFilter.Filtered"));
        FFilter.addAll(config.getStringList("FormatFilter.Filtered"));
        bOFilter = config.getBoolean("OtherFilter.Enable");
        bFFilter = config.getBoolean("FormatFilter.Enable");
    }

    public void onDisable() {}

    public List<String> onTabComplete(
            CommandSender sender, Command cmd, String CommandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setcolor") && sender instanceof Player p) {
            return SetColorCommand.tabComplete(p, args);
        }
        if (cmd.getName().equalsIgnoreCase("colorme") && args.length == 1) {
            return colormeTabComplete(args[0]);
        }
        if (cmd.getName().equalsIgnoreCase("grad") && sender instanceof Player p) {
            return gradTabComplete(p, args);
        }
        return null;
    }

    private static List<String> colormeTabComplete(String prefix) {
        List<String> out = new ArrayList<>();
        for (String id : color.getColorsCodes()) {
            if (id.toLowerCase().startsWith(prefix.toLowerCase())) {
                out.add(id);
            }
        }
        return out;
    }

    private static List<String> gradTabComplete(Player p, String[] args) {
        if (args.length != 3 && args.length != 4) {
            return List.of();
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            return List.of();
        }
        ItemMeta im = item.getItemMeta();
        if (im == null || !im.hasLore()) {
            return List.of();
        }
        int size = im.getLore().size();
        List<String> out = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            out.add(String.valueOf(i));
        }
        return out;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("grad")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(LangHandler.get("General", "PlayersOnly", null, null));
                return true;
            }
            if (!player.hasPermission("Colorme.Grad")) {
                player.sendMessage(LangHandler.get("General", "NoPerms", null, null));
                return true;
            }
            gradientItem.gothrough(args, player);
            return true;
        }
        if (command.getName().equalsIgnoreCase("colorme")) {
            if (sender instanceof Player && !sender.hasPermission("Colorme.Create")) {
                sender.sendMessage(LangHandler.get("General", "NoPerms", null, null));
                return true;
            }
            CommandArgs.ColormeError error = CommandArgs.validateColorme(args);
            if (error != null) {
                sender.sendMessage(colormeErrorMessage(error));
                return true;
            }
            colorFile.addColor(args[0], args[1]);
            sender.sendMessage(
                    ChatColor.of("#" + args[1])
                            + LangHandler.get("Colorme", "Success", "id", args[0]));
            return true;
        }
        if (command.getName().equalsIgnoreCase("colors")) {
            boolean custom = sender.hasPermission("colorcommand.custom");
            for (String s : colorFile.colorcommand(custom)) {
                sender.sendMessage(
                        ChatColor.translateAlternateColorCodes(
                                '!', color.ColorfyString(s, null, null, "!")));
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("setcolor")) {
            return SetColorCommand.handle(sender, args);
        }
        return false;
    }

    private static String colormeErrorMessage(CommandArgs.ColormeError error) {
        return switch (error.type()) {
            case ARG_COUNT ->
                    LangHandler.get(
                            "Colorme",
                            "Usage",
                            "example",
                            "<code> <hex>  e.g. /colorme sunset ff6600");
            case EMPTY_CODE -> LangHandler.get("Colorme", "EmptyCode", null, null);
            case VANILLA_CODE -> LangHandler.get("Colorme", "VanillaCode", "arg", error.arg());
            case BAD_HEX -> LangHandler.get("Colorme", "BadHex", "arg", error.arg());
        };
    }

    public static boolean isFFilterOn() {
        return bFFilter;
    }

    public static ArrayList<String> oFilter() {
        return OFilter;
    }

    public static ArrayList<String> fFilter() {
        return FFilter;
    }

    public static ChatColorStore colorStore() {
        return colorStore;
    }

    public static boolean isOFfilterOn() {
        return bOFilter;
    }
}
