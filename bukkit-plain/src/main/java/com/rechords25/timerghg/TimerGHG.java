package com.rechords25.timerghg;

import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
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
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
