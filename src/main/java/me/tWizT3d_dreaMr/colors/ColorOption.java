package me.tWizT3d_dreaMr.colors;

/** One selectable chat colour: a vanilla code, a Color.yml custom/random code, or a gradient. */
public record ColorOption(String id, Kind kind, String storedValue, String permission) {
    public enum Kind {
        VANILLA,
        CUSTOM,
        RANDOM,
        GRADIENT
    }
}
