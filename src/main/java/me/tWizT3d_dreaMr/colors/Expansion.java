package me.tWizT3d_dreaMr.colors;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class Expansion extends PlaceholderExpansion {

    private final Plugin plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public Expansion(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is an internal class, this check is not needed,
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convenience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "tc";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convenience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.10.8 you must OfflinePlayer in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        if (color.isColor(identifier)) {
            return color.getColorString(identifier);
        }
        if (color.isColor(identifier.replace("color", ""))) {
            return color.getColor(identifier.replace("color", "")) + "";
        }

        // %someplugin_placeholder1%
        if (identifier.equals("prefix")) {
            return color.ColorfyString(
                    Formatter.formatnp(PlaceholderAPI.setPlaceholders(player, "%vault_prefix%")),
                    null,
                    null,
                    "!");
        }
        if (identifier.equals("nick")) {
            return color.ColorfyString(
                    Formatter.formatnp(
                            PlaceholderAPI.setPlaceholders(player, "%essentials_nickname%")),
                    null,
                    null,
                    "!");
        }

        // %someplugin_placeholder2%
        if (identifier.equals("suffix")) {
            return color.ColorfyString(
                    color.replaceAllGrad(
                            Formatter.formatnp(
                                    PlaceholderAPI.setPlaceholders(player, "%vault_suffix%"))),
                    null,
                    null,
                    "!");
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }
}
