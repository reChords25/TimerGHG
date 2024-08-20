package com.rechords25.timerghg;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TimerGHG extends JavaPlugin {
    private Timer timer;
    @Override
    public void onEnable() {
        // Plugin startup logic
        timer = new Timer(this);
        PluginCommand timerCommand = getCommand("timer");
        timerCommand.setExecutor(new TimerCommand(timer));

        if (CommodoreProvider.isSupported()) {
            Commodore commodore = CommodoreProvider.getCommodore(this);
            registerCompletions(commodore, timerCommand);
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static void registerCompletions(Commodore commodore, PluginCommand timerCommand) {
        commodore.register(timerCommand, LiteralArgumentBuilder.literal("timer")
                .then(RequiredArgumentBuilder.argument("some-argument", StringArgumentType.string()))
                .then(RequiredArgumentBuilder.argument("some-other-argument", BoolArgumentType.bool()))
        );
    }
}
