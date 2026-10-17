package me.tWizT3d_dreaMr.colors;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Static metadata for {@code /setcolor} subcommands (name, expected arg count, gating
 * permission), kept free of Bukkit types so matching and visibility can be unit tested directly.
 */
public final class SetColorSubcommands {
    public enum Name {
        SET,
        SETG,
        REMOVE,
        CHAT
    }

    public record Spec(Name name, String id, int argCount, String permission) {}

    private static final List<Spec> ALL =
            List.of(
                    new Spec(Name.SET, "set", 2, null),
                    new Spec(Name.SETG, "setg", 2, null),
                    new Spec(Name.REMOVE, "remove", 1, null),
                    new Spec(Name.CHAT, "chat", 1, "tc2.can"));

    private SetColorSubcommands() {}

    public static boolean isHelp(String arg) {
        return arg.equalsIgnoreCase("help") || arg.equals("?");
    }

    public static Optional<Spec> find(String id) {
        return ALL.stream().filter(s -> s.id().equalsIgnoreCase(id)).findFirst();
    }

    public static List<Spec> visibleTo(Predicate<String> hasPermission) {
        return ALL.stream()
                .filter(s -> s.permission() == null || hasPermission.test(s.permission()))
                .toList();
    }

    public static List<Spec> all() {
        return ALL;
    }
}
