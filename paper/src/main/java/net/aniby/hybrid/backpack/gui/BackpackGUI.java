package net.aniby.hybrid.backpack.gui;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.aniby.hybrid.backpack.Backpack;
import net.aniby.hybrid.backpack.HybridBackpackPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BackpackGUI implements Listener {
    static final ItemStack LOCKER_ITEM;
    static {
        LOCKER_ITEM = new ItemStack(Material.BARRIER);
        ItemMeta meta = LOCKER_ITEM.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        LOCKER_ITEM.setItemMeta(meta);
    }

    Backpack backpack;
    Inventory inventory;

    public BackpackGUI(Backpack backpack, HybridBackpackPlugin plugin) {
        this.backpack = backpack;
        int inventorySize = Math.ceilDiv(this.backpack.size(), 9) * 9;
        this.inventory = Bukkit.createInventory(null, inventorySize, this.backpack.name());

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void fillInventory() {
        log.info("1");
        for (int i = 0; i < this.backpack.size(); i++) {
            this.inventory.setItem(i, this.backpack.items().get(i));
        }
        log.info("2");
        for (int i = this.backpack.size(); i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, LOCKER_ITEM);
        }
    }

    public void open(Player player) throws IOException {
        this.backpack.load();
        this.fillInventory();

        player.openInventory(this.inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        if (event.getInventory().equals(this.inventory)) {
            if (event.getCursor() != null && event.getCursor().isSimilar(LOCKER_ITEM)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        try {
            this.backpack.items(Arrays.asList(this.inventory.getContents()));
            this.backpack.save();
        } catch (IOException ioException) {
            log.error("Can't to save backpack inventory: {}", ioException.getMessage());
        }
        HandlerList.unregisterAll(this);
    }
}
