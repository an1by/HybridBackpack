package net.aniby.hybrid.backpack;

import net.aniby.hybrid.backpack.gui.BackpackGUI;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Because that is experimental thing */
@ApiStatus.Experimental
public class HybridBackpackPlugin extends JavaPlugin implements Listener {
    private static final Logger log = LoggerFactory.getLogger(HybridBackpackPlugin.class);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onClickEvent(InventoryClickEvent event) {
        if (event.isShiftClick()
                && event.getWhoClicked() instanceof Player player
                && event.getInventory() instanceof CraftInventoryCrafting) {
            ItemStack itemStack = event.getCurrentItem();
            Backpack backpack = BackpackCheck.getBackpack(itemStack);

            if (backpack != null) {
                EntityEquipment equipment = player.getEquipment();
                if (equipment.getChestplate() == null) {
                    equipment.setChestplate(itemStack.clone());
                    event.setCurrentItem(null);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            ItemStack itemStack = event.getItem();
            Backpack backpack = BackpackCheck.getBackpack(itemStack);

            if (backpack != null) {
                event.setCancelled(true);
                Player player = event.getPlayer();

                if (player.isSneaking()) {
                    // Open backpack
                    BackpackGUI gui = new BackpackGUI(backpack, this);
                    try {
                        gui.open(player);
                    } catch (Exception e) {
                    }
                    return;
                }

                // Try to fast-equip backpack
                ItemStack temporaryItemStack = itemStack.clone();
                EntityEquipment equipment = player.getEquipment();
                ItemStack itemStackToReplace = equipment.getChestplate() == null ? null : equipment.getChestplate().clone();
                if (event.getHand() == EquipmentSlot.HAND) {
                    equipment.setItemInMainHand(itemStackToReplace);
                } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                    equipment.setItemInOffHand(itemStackToReplace);
                }
                equipment.setChestplate(temporaryItemStack);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ShulkerBox) {
            if (event.isShiftClick()) {
                ItemStack current = event.getCurrentItem();
                if (current != null && BackpackCheck.isBackpack(current)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getHolder() instanceof ShulkerBox) {
            if (BackpackCheck.isBackpack(event.getCursor())) {
                event.setCancelled(true);
            }
        }
    }

    // Backpack
    // TODO: Delete backpack file after explosion
    @EventHandler
    private void onItemDeath(EntityCombustEvent event) {
        if (event.getEntity() instanceof Item item) {
            Backpack backpack = BackpackCheck.getBackpack(item.getItemStack());
            if (backpack != null) {
                backpack.delete();
            }
        }
    }

    @EventHandler
    private void onItemDespawn(ItemDespawnEvent event) {
        Backpack backpack = BackpackCheck.getBackpack(event.getEntity().getItemStack());
        if (backpack != null) {
            backpack.delete();
        }
    }
}
