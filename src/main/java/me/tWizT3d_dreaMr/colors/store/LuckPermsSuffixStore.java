package me.tWizT3d_dreaMr.colors.store;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.entity.Player;

public class LuckPermsSuffixStore implements ChatColorStore {
    private final int priority;

    public LuckPermsSuffixStore(int priority) {
        this.priority = priority;
    }

    private UserManager userManager() {
        return LuckPermsProvider.get().getUserManager();
    }

    @Override
    public CompletableFuture<Optional<String>> get(UUID player) {
        return userManager()
                .loadUser(player)
                .thenApply(
                        user ->
                                user.data().toCollection().stream()
                                        .filter(NodeType.SUFFIX::matches)
                                        .map(NodeType.SUFFIX::cast)
                                        .max(Comparator.comparingInt(SuffixNode::getPriority))
                                        .map(SuffixNode::getMetaValue));
    }

    @Override
    public CompletableFuture<Void> set(UUID player, String code) {
        return userManager()
                .modifyUser(
                        player,
                        user -> {
                            user.data().clear(NodeType.SUFFIX::matches);
                            user.data().add(SuffixNode.builder(code, priority).build());
                        });
    }

    @Override
    public CompletableFuture<Void> clear(UUID player) {
        return userManager()
                .modifyUser(player, user -> user.data().clear(NodeType.SUFFIX::matches));
    }

    @Override
    public String chatSuffix(Player player) {
        String suffix =
                LuckPermsProvider.get()
                        .getPlayerAdapter(Player.class)
                        .getMetaData(player)
                        .getSuffix();
        return suffix == null ? "" : suffix;
    }
}
