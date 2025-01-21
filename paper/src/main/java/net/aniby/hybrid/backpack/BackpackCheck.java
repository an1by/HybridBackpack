package net.aniby.hybrid.backpack;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackpackCheck {
    public static @Nullable Backpack getBackpack(ItemStack itemStack) {
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

    public static boolean isBackpack(@NotNull ItemStack itemStack) {
        if (itemStack.getType() == Material.STRUCTURE_BLOCK) {
            CustomModelData customModelData = itemStack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
            return customModelData != null && customModelData.strings().contains("backpack");
        }
        return false;
    }

    public static boolean isSameBackpack(@NotNull ItemStack itemStack, Backpack backpack) {
        if (itemStack.getType() == Material.STRUCTURE_BLOCK) {
            CustomModelData customModelData = itemStack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
            return customModelData != null
                    && customModelData.strings().contains(backpack.identifier())
                    && customModelData.strings().contains("backpack");
        }
        return false;
    }
}
