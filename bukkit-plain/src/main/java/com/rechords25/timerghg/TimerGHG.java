package com.rechords25.timerghg;

import org.bukkit.Bukkit;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;



public final class TimerGHG extends JavaPlugin implements Listener {
    private Timer timer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        ConfigUtil mainConfig = new ConfigUtil(this, "timer.yml");

        if (!mainConfig.getFile().exists()) {
            saveResource("timer.yml", false);
        }

        timer = new Timer(this);
        PluginCommand timerCommand = getCommand("timer");
        assert timerCommand != null;
        timerCommand.setExecutor(new TimerCommand(timer));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        timer.saveConfig(getConfig());
        saveConfig();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

}
