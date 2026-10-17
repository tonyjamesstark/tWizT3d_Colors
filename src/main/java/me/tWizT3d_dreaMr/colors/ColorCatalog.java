package me.tWizT3d_dreaMr.colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Builds the full list of selectable {@link ColorOption}s and checks permission for them in one
 * place, shared by the GUI, {@code /setcolor} and {@code ChatGui}.
 */
public final class ColorCatalog {
    private static final String VANILLA_IDS = "0123456789abcdef";
    private static final Set<String> WOO_IDS = Set.of("a", "b", "0", "z", "l", "k", "m", "n", "o");

    private ColorCatalog() {}

    public static ColorOption vanillaOption(String id) {
        return new ColorOption(id, ColorOption.Kind.VANILLA, "!" + id, "tc2.color." + id);
    }

    public static List<ColorOption> vanillaOptions() {
        List<ColorOption> out = new ArrayList<>();
        for (char c : VANILLA_IDS.toCharArray()) {
            out.add(vanillaOption(String.valueOf(c)));
        }
        return out;
    }

    public static List<ColorOption> customOptions() {
        List<ColorOption> out = new ArrayList<>();
        for (String id : color.getColorsCodes()) {
            ColorOption.Kind kind =
                    color.isRandomColor(id) ? ColorOption.Kind.RANDOM : ColorOption.Kind.CUSTOM;
            out.add(new ColorOption(id, kind, "!" + id, "tc2.color." + id));
        }
        return out;
    }

    public static List<ColorOption> gradientOptions() {
        List<ColorOption> out = new ArrayList<>();
        for (String id : color.getGradCodes()) {
            out.add(new ColorOption(id, ColorOption.Kind.GRADIENT, "!" + id, "tcg2.color." + id));
        }
        return out;
    }

    public static List<ColorOption> allOptions() {
        List<ColorOption> out = new ArrayList<>(vanillaOptions());
        out.addAll(customOptions());
        out.addAll(gradientOptions());
        return out;
    }

    /** The {@code tco.woo} rule applies to both vanilla and custom ids, on top of the option's own node. */
    public static boolean hasPermission(ColorOption option, Predicate<String> hasPerm) {
        if (WOO_IDS.contains(option.id()) && !hasPerm.test("tco.woo")) {
            return false;
        }
        return hasPerm.test(option.permission());
    }

    public static List<ColorOption> visible(Predicate<String> hasPerm) {
        return allOptions().stream().filter(o -> hasPermission(o, hasPerm)).toList();
    }

    public static List<ColorOption> locked(Predicate<String> hasPerm) {
        return allOptions().stream().filter(o -> !hasPermission(o, hasPerm)).toList();
    }

    public static Optional<ColorOption> find(String id) {
        return allOptions().stream().filter(o -> o.id().equals(id)).findFirst();
    }

    public static Optional<ColorOption> findVanillaOrCustom(String id) {
        return find(id).filter(o -> o.kind() != ColorOption.Kind.GRADIENT);
    }

    public static Optional<ColorOption> findGradient(String id) {
        return find(id).filter(o -> o.kind() == ColorOption.Kind.GRADIENT);
    }

    public static <T> List<List<T>> paginate(List<T> items, int perPage) {
        List<List<T>> pages = new ArrayList<>();
        if (items.isEmpty()) {
            pages.add(List.of());
            return pages;
        }
        for (int i = 0; i < items.size(); i += perPage) {
            pages.add(items.subList(i, Math.min(items.size(), i + perPage)));
        }
        return pages;
    }
}
