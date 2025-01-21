package net.aniby.hybrid.backpack.command;


import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BackpackCommand implements BasicCommand {
    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack commandSourceStack, String[] args) {
        Player player = (Player) commandSourceStack.getSender();
        ItemStack itemStack = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("backpack").addString(UUID.randomUUID().toString()).build());
        player.getInventory().addItem(itemStack);
    }
}
