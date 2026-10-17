package me.tWizT3d_dreaMr.colors.Listeners;

import me.tWizT3d_dreaMr.colors.ColorMenu;
import me.tWizT3d_dreaMr.colors.main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/** Cancels every click and drag whose top inventory is a {@link ColorMenu}. */
public class ColorMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof ColorMenu menu)) {
            return;
        }
        e.setCancelled(true);
        if (e.getClickedInventory() == e.getView().getTopInventory()) {
            int slot = e.getSlot();
            Bukkit.getScheduler().runTask(main.plugin, () -> menu.handleClick(slot));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof ColorMenu) {
            e.setCancelled(true);
        }
    }
}
