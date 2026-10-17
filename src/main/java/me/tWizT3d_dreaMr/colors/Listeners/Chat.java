package me.tWizT3d_dreaMr.colors.Listeners;

import me.tWizT3d_dreaMr.colors.color;
import me.tWizT3d_dreaMr.colors.main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void chat(AsyncPlayerChatEvent e) {
        String suffix = main.colorStore().chatSuffix(e.getPlayer());
        if (color.isGrad(suffix)
                && !e.getMessage().startsWith("#")
                && !e.getMessage().startsWith("!")) {
            e.setMessage(color.replaceAllGrad(e.getMessage()));
            e.setMessage(suffix.replace("!", "&") + e.getMessage());
        }
        e.setMessage(color.ColorfyString(e.getMessage(), e.getPlayer(), "chat", "&"));
    }
}
