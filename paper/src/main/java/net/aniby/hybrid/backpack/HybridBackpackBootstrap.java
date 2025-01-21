package net.aniby.hybrid.backpack;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.RootCommandNode;
import io.papermc.paper.command.brigadier.ApiMirrorRootNode;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.extern.slf4j.Slf4j;
import net.aniby.hybrid.backpack.command.BackpackCommand;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;

@Slf4j
public class HybridBackpackBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        LifecycleEventManager<BootstrapContext> lifecycleManager = context.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = event.registrar();
            commands.register("backpack", "Backpack command", new BackpackCommand());
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new HybridBackpackPlugin();
    }
}
