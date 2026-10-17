package me.tWizT3d_dreaMr.colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import me.tWizT3d_dreaMr.colors.store.ChatColorStore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/** The chest inventory opened by {@code /setcolor} with no arguments. */
public class ColorMenu implements InventoryHolder {
    private static final int SIZE = 54;
    private static final int PAGE_SIZE = 36;
    private static final int SLOT_PREVIEW = 4;
    private static final int SLOT_REMOVE = 7;
    private static final int SLOT_CLOSE = 8;
    private static final int SLOT_PREV = 45;
    private static final int SLOT_PAGE = 49;
    private static final int SLOT_NEXT = 53;

    private static final Map<String, Material> VANILLA_MATERIAL =
            Map.ofEntries(
                    Map.entry("0", Material.BLACK_DYE),
                    Map.entry("1", Material.BLUE_DYE),
                    Map.entry("2", Material.GREEN_DYE),
                    Map.entry("3", Material.CYAN_DYE),
                    Map.entry("4", Material.RED_DYE),
                    Map.entry("5", Material.PURPLE_DYE),
                    Map.entry("6", Material.ORANGE_DYE),
                    Map.entry("7", Material.LIGHT_GRAY_DYE),
                    Map.entry("8", Material.GRAY_DYE),
                    Map.entry("9", Material.LIGHT_BLUE_DYE),
                    Map.entry("a", Material.LIME_DYE),
                    Map.entry("b", Material.LIGHT_BLUE_DYE),
                    Map.entry("c", Material.RED_DYE),
                    Map.entry("d", Material.MAGENTA_DYE),
                    Map.entry("e", Material.YELLOW_DYE),
                    Map.entry("f", Material.WHITE_DYE));

    private record Entry(ColorOption option, boolean adminOnly) {}

    private final Player player;
    private final List<Entry> entries;
    private int page;
    private Inventory inventory;

    private ColorMenu(Player player, List<Entry> entries, int page) {
        this.player = player;
        this.entries = entries;
        this.page = page;
    }

    public static void open(Player player) {
        new ColorMenu(player, buildEntries(player), 0).render();
    }

    private static List<Entry> buildEntries(Player player) {
        Predicate<String> perm = player::hasPermission;
        List<Entry> out = new ArrayList<>();
        for (ColorOption o : ColorCatalog.vanillaOptions()) {
            if (ColorCatalog.hasPermission(o, perm)) {
                out.add(new Entry(o, false));
            }
        }
        for (ColorOption o : ColorCatalog.customOptions()) {
            if (ColorCatalog.hasPermission(o, perm)) {
                out.add(new Entry(o, false));
            }
        }
        for (ColorOption o : ColorCatalog.gradientOptions()) {
            if (ColorCatalog.hasPermission(o, perm)) {
                out.add(new Entry(o, false));
            }
        }
        if (player.hasPermission("tcolors.admin")) {
            for (ColorOption o : ColorCatalog.allOptions()) {
                if (!ColorCatalog.hasPermission(o, perm)) {
                    out.add(new Entry(o, true));
                }
            }
        }
        return out;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private List<List<Entry>> pages() {
        return ColorCatalog.paginate(entries, PAGE_SIZE);
    }

    private void render() {
        inventory =
                Bukkit.createInventory(
                        this, SIZE, LangHandler.component("Gui", "Title", null, null));
        List<List<Entry>> pages = pages();
        if (page >= pages.size()) {
            page = pages.size() - 1;
        }
        if (page < 0) {
            page = 0;
        }
        List<Entry> shown = pages.get(page);
        for (int i = 0; i < shown.size(); i++) {
            inventory.setItem(9 + i, buildItem(shown.get(i)));
        }
        inventory.setItem(SLOT_REMOVE, buildButton("Gui", "Remove", Material.BARRIER));
        inventory.setItem(SLOT_CLOSE, buildButton("Gui", "Close", Material.OAK_DOOR));
        inventory.setItem(SLOT_PREVIEW, previewItem(Optional.empty()));
        if (pages.size() > 1) {
            if (page > 0) {
                inventory.setItem(SLOT_PREV, buildButton("Gui", "Prev", Material.ARROW));
            }
            inventory.setItem(SLOT_PAGE, pageItem(page + 1, pages.size()));
            if (page < pages.size() - 1) {
                inventory.setItem(SLOT_NEXT, buildButton("Gui", "Next", Material.ARROW));
            }
        }
        player.openInventory(inventory);
        refreshPreview();
    }

    public void handleClick(int slot) {
        if (slot == SLOT_CLOSE) {
            player.closeInventory();
            return;
        }
        if (slot == SLOT_REMOVE) {
            SetColorCommand.removeColor(
                    player,
                    () -> {
                        player.sendMessage(LangHandler.get("Gui", "Removed", null, null));
                        refreshPreview();
                    });
            return;
        }
        if (slot == SLOT_PREV) {
            if (page > 0) {
                page--;
                render();
            }
            return;
        }
        if (slot == SLOT_NEXT) {
            if (page < pages().size() - 1) {
                page++;
                render();
            }
            return;
        }
        int index = slot - 9;
        if (index < 0 || index >= PAGE_SIZE) {
            return;
        }
        List<List<Entry>> pages = pages();
        if (page >= pages.size()) {
            return;
        }
        List<Entry> shown = pages.get(page);
        if (index >= shown.size()) {
            return;
        }
        applyEntry(shown.get(index));
    }

    private void applyEntry(Entry entry) {
        ColorOption option = entry.option();
        boolean allowed =
                ColorCatalog.hasPermission(option, player::hasPermission)
                        || (entry.adminOnly() && player.hasPermission("tcolors.admin"));
        if (!allowed) {
            player.sendMessage(LangHandler.get("Gui", "NoPerms", null, null));
            return;
        }
        SetColorCommand.applyColor(
                player,
                option.storedValue(),
                () -> {
                    player.sendMessage(LangHandler.get("Gui", "Applied", "id", option.id()));
                    refreshPreview();
                });
    }

    private void refreshPreview() {
        ChatColorStore store = main.colorStore();
        if (store == null) {
            inventory.setItem(SLOT_PREVIEW, previewItem(Optional.empty()));
            return;
        }
        store.get(player.getUniqueId())
                .whenComplete(
                        (val, ex) ->
                                Bukkit.getScheduler()
                                        .runTask(
                                                main.plugin,
                                                () -> {
                                                    if (!player.isOnline()) {
                                                        return;
                                                    }
                                                    if (ex != null) {
                                                        player.sendMessage(
                                                                LangHandler.get(
                                                                        "Gui",
                                                                        "StoreError",
                                                                        null,
                                                                        null));
                                                        return;
                                                    }
                                                    if (player.getOpenInventory()
                                                                    .getTopInventory()
                                                                    .getHolder()
                                                            == this) {
                                                        inventory.setItem(
                                                                SLOT_PREVIEW, previewItem(val));
                                                    }
                                                }));
    }

    private ItemStack previewItem(Optional<String> stored) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                LangHandler.component("Gui", "PreviewName", null, null)
                        .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        String sample = LangHandler.get("Gui", "SampleText", null, null);
        if (stored.isEmpty()) {
            lore.add(
                    LangHandler.component("Gui", "PreviewLoreNone", null, null)
                            .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(
                    sampleComponent(stored.get(), sample).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private Component sampleComponent(String storedValue, String sample) {
        if (storedValue.startsWith("!")) {
            String id = storedValue.substring(1);
            return ColorCatalog.find(id)
                    .map(o -> colouredText(o, sample))
                    .orElse(Component.text(sample));
        }
        String hex = storedValue.replace("&#", "#");
        TextColor tc = TextColor.fromHexString(hex);
        return tc != null ? Component.text(sample, tc) : Component.text(sample);
    }

    private static Component colouredText(ColorOption option, String text) {
        if (option.kind() == ColorOption.Kind.GRADIENT) {
            List<String> hexes = color.getGrads(option.id());
            String mm =
                    "<gradient:"
                            + hexes.get(0)
                            + ":"
                            + hexes.get(1)
                            + ">"
                            + MiniMessage.miniMessage().escapeTags(text)
                            + "</gradient>";
            return MiniMessage.miniMessage().deserialize(mm);
        }
        ChatColor bungee =
                option.kind() == ColorOption.Kind.VANILLA
                        ? ChatColor.getByChar(option.id().charAt(0))
                        : color.getColor(option.id());
        if (bungee == null || bungee.getColor() == null) {
            return Component.text(text);
        }
        return Component.text(text, TextColor.color(bungee.getColor().getRGB()));
    }

    private ItemStack buildItem(Entry entry) {
        ColorOption option = entry.option();
        ItemStack item = new ItemStack(materialFor(option));
        ItemMeta meta = item.getItemMeta();
        String label =
                option.kind() == ColorOption.Kind.GRADIENT
                        ? "Gradient " + option.id()
                        : option.id();
        meta.displayName(colouredText(option, label).decoration(TextDecoration.ITALIC, false));
        String sample = LangHandler.get("Gui", "SampleText", null, null);
        List<Component> lore = new ArrayList<>();
        lore.add(colouredText(option, sample).decoration(TextDecoration.ITALIC, false));
        if (entry.adminOnly()) {
            lore.add(
                    LangHandler.component("Gui", "AdminLockedLore", null, null)
                            .decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static Material materialFor(ColorOption option) {
        if (option.kind() == ColorOption.Kind.VANILLA) {
            return VANILLA_MATERIAL.getOrDefault(option.id(), Material.WHITE_DYE);
        }
        return Material.PAPER;
    }

    private static ItemStack buildButton(String plugin, String part, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                LangHandler.component(plugin, part, null, null)
                        .decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack pageItem(int current, int total) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        String text =
                LangHandler.get("Gui", "PagePrefix", "cur", String.valueOf(current))
                        .replace(">max", String.valueOf(total));
        meta.displayName(LangHandler.componentOf(text).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }
}
