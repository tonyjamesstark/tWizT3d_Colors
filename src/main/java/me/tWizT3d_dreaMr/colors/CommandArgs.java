package me.tWizT3d_dreaMr.colors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Pure argument validation for {@code /colorme} and {@code /grad}, kept free of Bukkit types so
 * it can be unit tested directly.
 */
public final class CommandArgs {
    private static final Pattern HEX6 = Pattern.compile("^[a-fA-F0-9]{6}$");
    private static final Pattern HASH_HEX6 = Pattern.compile("^#[a-fA-F0-9]{6}$");
    private static final String VANILLA_CODES = "0123456789abcdefklmnor";

    private CommandArgs() {}

    public static boolean isVanillaCode(String code) {
        return code.length() == 1
                && VANILLA_CODES.indexOf(Character.toLowerCase(code.charAt(0))) >= 0;
    }

    public enum ColormeErrorType {
        ARG_COUNT,
        EMPTY_CODE,
        VANILLA_CODE,
        BAD_HEX
    }

    public record ColormeError(ColormeErrorType type, String arg) {}

    /** {@code null} means the args are valid. */
    public static ColormeError validateColorme(String[] args) {
        if (args.length != 2) {
            return new ColormeError(ColormeErrorType.ARG_COUNT, null);
        }
        String code = args[0];
        String hex = args[1];
        if (code.isEmpty()) {
            return new ColormeError(ColormeErrorType.EMPTY_CODE, code);
        }
        if (isVanillaCode(code)) {
            return new ColormeError(ColormeErrorType.VANILLA_CODE, code);
        }
        if (!HEX6.matcher(hex).matches()) {
            return new ColormeError(ColormeErrorType.BAD_HEX, hex);
        }
        return null;
    }

    public enum GradErrorType {
        ARG_COUNT,
        BAD_HEX,
        BAD_NUMBER,
        LINE_OUT_OF_RANGE,
        RANGE_TOO_SMALL
    }

    public record GradError(GradErrorType type, String arg) {}

    public record GradParsed(List<String> hexes, int start, int end) {}

    public static final class GradResult {
        private final GradParsed parsed;
        private final GradError error;

        private GradResult(GradParsed parsed, GradError error) {
            this.parsed = parsed;
            this.error = error;
        }

        public boolean ok() {
            return error == null;
        }

        public GradParsed parsed() {
            return parsed;
        }

        public GradError error() {
            return error;
        }
    }

    /**
     * Validates {@code /grad} arguments: 2 or 3 leading {@code #rrggbb} hexes followed by a start
     * and end lore line (1-indexed, inclusive, either order). {@code loreSize} bounds the lines.
     */
    public static GradResult validateGrad(String[] args, int loreSize) {
        if (args.length != 4 && args.length != 5) {
            return new GradResult(null, new GradError(GradErrorType.ARG_COUNT, null));
        }
        int hexCount = args.length - 2;
        List<String> hexes = new ArrayList<>();
        for (int i = 0; i < hexCount; i++) {
            if (!HASH_HEX6.matcher(args[i]).matches()) {
                return new GradResult(null, new GradError(GradErrorType.BAD_HEX, args[i]));
            }
            hexes.add(args[i]);
        }
        int a;
        int b;
        try {
            a = Integer.parseInt(args[hexCount]);
        } catch (NumberFormatException ex) {
            return new GradResult(null, new GradError(GradErrorType.BAD_NUMBER, args[hexCount]));
        }
        try {
            b = Integer.parseInt(args[hexCount + 1]);
        } catch (NumberFormatException ex) {
            return new GradResult(
                    null, new GradError(GradErrorType.BAD_NUMBER, args[hexCount + 1]));
        }
        int lo = Math.min(a, b);
        int hi = Math.max(a, b);
        if (lo < 1 || hi > loreSize) {
            return new GradResult(
                    null,
                    new GradError(
                            GradErrorType.LINE_OUT_OF_RANGE, String.valueOf(lo < 1 ? lo : hi)));
        }
        if (hexCount == 3 && hi == lo) {
            return new GradResult(
                    null, new GradError(GradErrorType.RANGE_TOO_SMALL, String.valueOf(lo)));
        }
        return new GradResult(new GradParsed(hexes, a, b), null);
    }
}
