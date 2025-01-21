package net.aniby.hybrid.backpack;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.aniby.hybrid.backpack.gui.BackpackGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
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

    private @Nullable Backpack getBackpack(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() == Material.STRUCTURE_BLOCK) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                CustomModelData customModelData = itemStack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
                if (customModelData != null && customModelData.strings().size() >= 2 && customModelData.strings().contains("backpack")) {
                    Component name = itemMeta.displayName() != null ? itemMeta.displayName() : Component.text("Backpack");
                    return new Backpack(name, customModelData.strings().get(1));
                }
            }
        }
        return null;
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (event.isShiftClick() && event.getWhoClicked() instanceof Player player) {
            ItemStack itemStack = event.getCurrentItem();
            Backpack backpack = getBackpack(itemStack);

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
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            ItemStack itemStack = event.getItem();
            Backpack backpack = getBackpack(itemStack);

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
}
