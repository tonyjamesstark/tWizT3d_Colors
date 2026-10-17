package me.tWizT3d_dreaMr.colors.store;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

/** Persists the chat colour code a player has chosen, e.g. {@code !b}, {@code !gr} or {@code &#ff00ff}. */
public interface ChatColorStore {
    CompletableFuture<Optional<String>> get(UUID player);

    CompletableFuture<Void> set(UUID player, String code);

    CompletableFuture<Void> clear(UUID player);

    /** The suffix chat shows for the player, inherited ones included; callable off the main thread. */
    String chatSuffix(Player player);
}
