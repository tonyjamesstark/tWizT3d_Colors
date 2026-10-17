package me.tWizT3d_dreaMr.colors.store;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.clip.placeholderapi.PlaceholderAPI;
import me.tWizT3d_dreaMr.colors.main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/** The 1.x storage: PermissionsEx user suffix, written with console commands and read through PlaceholderAPI. */
public class PexSuffixStore implements ChatColorStore {
    @Override
    public CompletableFuture<Optional<String>> get(UUID player) {
        String suffix = vaultSuffix(Bukkit.getOfflinePlayer(player));
        return CompletableFuture.completedFuture(
                suffix.isEmpty() ? Optional.empty() : Optional.of(suffix));
    }

    @Override
    public CompletableFuture<Void> set(UUID player, String code) {
        return pexSuffix(player, code);
    }

    @Override
    public CompletableFuture<Void> clear(UUID player) {
        return pexSuffix(player, "\"\"");
    }

    @Override
    public String chatSuffix(Player player) {
        return vaultSuffix(player);
    }

    private static CompletableFuture<Void> pexSuffix(UUID player, String value) {
        CompletableFuture<Void> done = new CompletableFuture<>();
        Runnable run =
                () -> {
                    String name = Bukkit.getOfflinePlayer(player).getName();
                    if (name == null) {
                        done.completeExceptionally(
                                new IllegalStateException("unknown player " + player));
                        return;
                    }
                    if (Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(), "pex user " + name + " suffix " + value)) {
                        done.complete(null);
                    } else {
                        done.completeExceptionally(new IllegalStateException("pex command failed"));
                    }
                };
        if (Bukkit.isPrimaryThread()) run.run();
        else Bukkit.getScheduler().runTask(main.plugin, run);
        return done;
    }

    private static String vaultSuffix(OfflinePlayer player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return "";
        String suffix = PlaceholderAPI.setPlaceholders(player, "%vault_suffix%");
        return suffix == null || suffix.equals("%vault_suffix%") ? "" : suffix;
    }
}
