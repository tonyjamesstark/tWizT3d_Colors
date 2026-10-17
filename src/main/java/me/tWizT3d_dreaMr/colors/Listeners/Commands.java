package me.tWizT3d_dreaMr.colors.Listeners;

import me.tWizT3d_dreaMr.colors.color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Commands implements org.bukkit.event.Listener {

    @EventHandler
    public void Pcommand(PlayerCommandPreprocessEvent e) {
        if (!(e.getMessage().toLowerCase().startsWith("setcolor")
                || e.getMessage().toLowerCase().startsWith("/setcolor")
                || e.getMessage().toLowerCase().startsWith("pex")
                || e.getMessage().toLowerCase().startsWith("/pex")))
            e.setMessage(color.ColorfyString(e.getMessage(), e.getPlayer(), "command", "&"));
    }
}
