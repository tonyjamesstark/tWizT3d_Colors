package me.tWizT3d_dreaMr.colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.tWizT3d_dreaMr.colors.store.ChatColorStore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** All {@code /setcolor} handling: no args opens the GUI, {@code chat} opens the old clickable menu. */
public class SetColorCommand {
    private static final List<String> SUBCOMMANDS =
            List.of("set", "setg", "remove", "chat", "help");

    private SetColorCommand() {}

    public static boolean handle(CommandSender sender, String[] args) {
        if (args.length >= 1 && SetColorSubcommands.isHelp(args[0])) {
            sendHelp(sender);
            return true;
        }
        if (!(sender instanceof Player p)) {
            sender.sendMessage(LangHandler.get("General", "PlayersOnly", null, null));
            return true;
        }
        if (args.length == 0) {
            ColorMenu.open(p);
            return true;
        }
        Optional<SetColorSubcommands.Spec> spec = SetColorSubcommands.find(args[0]);
        if (spec.isEmpty()) {
            p.sendMessage(LangHandler.get("Setcolor", "UnknownSub", "arg", args[0]));
            return true;
        }
        SetColorSubcommands.Spec s = spec.get();
        if (s.permission() != null && !p.hasPermission(s.permission())) {
            p.sendMessage(LangHandler.get("General", "NoPerms", null, null));
            return true;
        }
        if (args.length != s.argCount()) {
            p.sendMessage(usageMessage(s.name()));
            return true;
        }
        switch (s.name()) {
            case CHAT -> ChatGui.Command(p);
            case REMOVE ->
                    removeColor(
                            p, () -> p.sendMessage(LangHandler.get("Gui", "Removed", null, null)));
            case SET -> set(p, args[1]);
            case SETG -> setg(p, args[1]);
        }
        return true;
    }

    private static String usageMessage(SetColorSubcommands.Name name) {
        return switch (name) {
            case SET ->
                    LangHandler.get(
                            "Setcolor",
                            "UsageSet",
                            "example",
                            "<code|&#hex>  e.g. /setcolor set c  or /setcolor set &#ff0000");
            case SETG ->
                    LangHandler.get(
                            "Setcolor",
                            "UsageSetg",
                            "example",
                            "<gradient>  e.g. /setcolor setg gr");
            case REMOVE -> LangHandler.get("Setcolor", "UsageRemove", null, null);
            case CHAT -> LangHandler.get("Setcolor", "UsageChat", null, null);
        };
    }

    private static void sendHelp(CommandSender sender) {
        sender.sendMessage(LangHandler.get("Setcolor", "HelpHeader", null, null));
        for (SetColorSubcommands.Spec s : SetColorSubcommands.visibleTo(sender::hasPermission)) {
            sender.sendMessage(LangHandler.get("Setcolor", descKey(s.name()), null, null));
        }
        sender.sendMessage(LangHandler.get("Setcolor", "DescHelp", null, null));
    }

    private static String descKey(SetColorSubcommands.Name name) {
        return switch (name) {
            case SET -> "DescSet";
            case SETG -> "DescSetg";
            case REMOVE -> "DescRemove";
            case CHAT -> "DescChat";
        };
    }

    private static void set(Player p, String arg) {
        Optional<ColorOption> option = ColorCatalog.findVanillaOrCustom(arg);
        if (option.isPresent()) {
            ColorOption o = option.get();
            if (!ColorCatalog.hasPermission(o, p::hasPermission)) {
                p.sendMessage(
                        color.ColorfyString(
                                "!cYou cannot set your color to !" + arg + "this color",
                                null,
                                null,
                                "!"));
                return;
            }
            applyColor(
                    p,
                    o.storedValue(),
                    () ->
                            p.sendMessage(
                                    color.ColorfyString(
                                            "!bYou have set your chatcolor to !"
                                                    + arg
                                                    + "this color",
                                            null,
                                            null,
                                            "!")));
            return;
        }
        if (Formatter.hasHex(arg)) {
            if (!p.hasPermission("tc2.color." + arg)) {
                p.sendMessage(
                        color.ColorfyString(
                                "!cYou cannot set your color to !" + arg + "this color",
                                null,
                                null,
                                "!"));
                return;
            }
            applyColor(
                    p,
                    arg,
                    () ->
                            p.sendMessage(
                                    Formatter.formatnp(
                                            color.ColorfyString(
                                                    "!bYou have set your chatcolor to "
                                                            + arg
                                                            + "this color",
                                                    null,
                                                    null,
                                                    "!"))));
            return;
        }
        p.sendMessage(ChatColor.RED + "That is not a color");
    }

    private static void setg(Player p, String arg) {
        Optional<ColorOption> option = ColorCatalog.findGradient(arg);
        if (option.isEmpty()) {
            p.sendMessage(ChatColor.RED + "That is not a gradient");
            return;
        }
        ColorOption o = option.get();
        if (!ColorCatalog.hasPermission(o, p::hasPermission)) {
            p.sendMessage(
                    color.ColorfyString(
                            "!cYou cannot set your color to !" + arg + "this color",
                            null,
                            null,
                            "!"));
            return;
        }
        applyColor(
                p,
                o.storedValue(),
                () ->
                        p.sendMessage(
                                color.ColorfyString(
                                        "!bYou have set your chatcolor to !" + arg + "this color",
                                        null,
                                        null,
                                        "!")));
    }

    public static void applyColor(Player p, String storedValue, Runnable onSuccess) {
        ChatColorStore store = main.colorStore();
        if (store == null) {
            p.sendMessage(LangHandler.get("Gui", "StoreUnavailable", null, null));
            return;
        }
        store.set(p.getUniqueId(), storedValue)
                .whenComplete(
                        (v, ex) ->
                                Bukkit.getScheduler()
                                        .runTask(
                                                main.plugin,
                                                () -> {
                                                    if (ex != null) {
                                                        p.sendMessage(
                                                                LangHandler.get(
                                                                        "Gui",
                                                                        "StoreError",
                                                                        null,
                                                                        null));
                                                    } else {
                                                        onSuccess.run();
                                                    }
                                                }));
    }

    public static void removeColor(Player p, Runnable onSuccess) {
        ChatColorStore store = main.colorStore();
        if (store == null) {
            p.sendMessage(LangHandler.get("Gui", "StoreUnavailable", null, null));
            return;
        }
        store.clear(p.getUniqueId())
                .whenComplete(
                        (v, ex) ->
                                Bukkit.getScheduler()
                                        .runTask(
                                                main.plugin,
                                                () -> {
                                                    if (ex != null) {
                                                        p.sendMessage(
                                                                LangHandler.get(
                                                                        "Gui",
                                                                        "StoreError",
                                                                        null,
                                                                        null));
                                                    } else {
                                                        onSuccess.run();
                                                    }
                                                }));
    }

    public static List<String> tabComplete(Player p, String[] args) {
        if (args.length == 1) {
            List<String> out = new ArrayList<>();
            for (String s : SUBCOMMANDS) {
                if (s.startsWith(args[0].toLowerCase())) {
                    out.add(s);
                }
            }
            return out;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                return idsStartingWith(p, args[1], false);
            }
            if (args[0].equalsIgnoreCase("setg")) {
                return idsStartingWith(p, args[1], true);
            }
        }
        return List.of();
    }

    private static List<String> idsStartingWith(Player p, String prefix, boolean gradients) {
        List<String> out = new ArrayList<>();
        for (ColorOption o : ColorCatalog.visible(p::hasPermission)) {
            boolean isGrad = o.kind() == ColorOption.Kind.GRADIENT;
            if (isGrad == gradients && o.id().startsWith(prefix.toLowerCase())) {
                out.add(o.id());
            }
        }
        return out;
    }
}
