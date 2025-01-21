package net.aniby.hybrid.backpack;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.aniby.hybrid.backpack.gui.BackpackGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HybridBackpackPlugin extends JavaPlugin implements Listener {
    private static final Logger log = LoggerFactory.getLogger(HybridBackpackPlugin.class);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if (itemStack != null && player.isSneaking() && event.getAction().isRightClick()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                CustomModelData customModelData = itemStack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
                if (customModelData != null && customModelData.strings().contains("backpack")) {
                    Component name = itemMeta.displayName() != null
                            ? itemMeta.displayName()
                            : Component.text("Backpack");
                    Backpack backpack = new Backpack(name, customModelData.strings().getLast());
                    BackpackGUI gui = new BackpackGUI(backpack, this);
                    try {
                        gui.open(player);
                    } catch (Exception e) {}
                }
            }
        }
    }
}
