package net.aniby.hybrid.backpack.gui;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.aniby.hybrid.backpack.Backpack;
import net.aniby.hybrid.backpack.BackpackCheck;
import net.aniby.hybrid.backpack.HybridBackpackPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

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
        // Exception preventing
        int inventorySize = Math.ceilDiv(this.backpack.size(), 9) * 9;
        this.inventory = Bukkit.createInventory(null, inventorySize, this.backpack.name());

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void fillInventory() {
        for (int i = 0; i < this.backpack.size(); i++) {
            this.inventory.setItem(i, this.backpack.items().get(i));
        }
        // IF ( BACKPACK_SLOTS % 9 != 0 )
        for (int i = this.backpack.size(); i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, LOCKER_ITEM);
        }
    }

    public void open(Player player) throws IOException {
        this.backpack.load();
        this.fillInventory();

        player.openInventory(this.inventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().equals(this.inventory)) {
            ItemStack cursor = event.getCursor();
            if (cursor != null && (cursor.isSimilar(LOCKER_ITEM) || BackpackCheck.isBackpack(cursor))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryClick(final InventoryClickEvent event) {
        if (Objects.equals(event.getInventory(), this.inventory)) {
            if (event.isShiftClick()) {
                ItemStack current = event.getCurrentItem();
                if (current != null && BackpackCheck.isBackpack(current)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (Objects.equals(event.getClickedInventory(), this.inventory)) {
            ItemStack current = event.getCurrentItem();
            if (BackpackCheck.isBackpack(event.getCursor()) || (current != null && current.isSimilar(LOCKER_ITEM))) {
                event.setCancelled(true);
            }
        }
    }

    private void close() {
        try {
            this.backpack.items(Arrays.asList(this.inventory.getContents()));
            this.backpack.save();
        } catch (IOException ioException) {
            log.error("Can't to save backpack inventory: {}", ioException.getMessage());
        }
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(this.inventory)) {
            this.close();
        }
    }

    @EventHandler
    private void onDrop(final PlayerDropItemEvent event) {
        if (BackpackCheck.isSameBackpack(event.getItemDrop().getItemStack(), this.backpack)) {
            this.inventory.close();
            this.close();
        }
    }

    @EventHandler
    private void onDeath(final PlayerDeathEvent event) {
        if (event.getDrops().stream().anyMatch(item -> BackpackCheck.isSameBackpack(item, this.backpack))) {
            this.close();
        }
    }
}
