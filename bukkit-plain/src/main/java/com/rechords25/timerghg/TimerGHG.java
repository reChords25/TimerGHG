package com.rechords25.timerghg;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;



public final class TimerGHG extends JavaPlugin implements Listener {
    private Timer timer;
    private ConfigUtil mainConfig;

    @Override
    public void onEnable() {
        // Runs on plugin load (server start or reload)
        timer = new Timer(this);

        // Prepare config file
        mainConfig = new ConfigUtil(this, "timer.yml");
        if (!mainConfig.getFile().exists()) {
            saveResource("timer.yml", false);
        }

        // Load timer state from timer.yml config file
        timer.loadConfig(mainConfig.getConfig());

        // Connect the timer command with the TimerCommand class / object
        getCommand("timer").setExecutor(new TimerCommand(timer));
    }

    @Override
    // Runs on plugin unload (server crash / stop / restart / reload)
    public void onDisable() {
        // Save current timer state to the timer.yml config file
        timer.saveConfig(mainConfig.getConfig());
        getLogger().info(mainConfig.save() ? "Saved main config." : "Could not save main config.");
    }

    @EventHandler
    // Runs when a player leaves the server
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    // Runs when a player joins the server
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

}
