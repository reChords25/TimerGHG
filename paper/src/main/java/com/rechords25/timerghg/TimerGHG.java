package com.rechords25.timerghg;

import org.bukkit.plugin.java.JavaPlugin;



public final class TimerGHG extends JavaPlugin {
    private Timer timer;
    private ConfigUtil mainConfig;

    @Override
    public void onEnable() {
        // Prepare config file
        mainConfig = new ConfigUtil(this, "timer.yml");
        if (!mainConfig.getFile().exists()) {
            saveResource("timer.yml", false);
        }
        timer = new Timer(this, mainConfig.getConfig());

        // Load timer state from timer.yml config file
        timer.loadConfig();

        // Connect the timer command with the TimerCommand class / object
        getCommand("timer").setExecutor(new TimerCommand(timer));
    }

    @Override
    // Runs on plugin unload (server crash / stop / restart / reload)
    public void onDisable() {
        // Save current timer state to the timer.yml config file
        timer.saveConfig();
        getLogger().info(mainConfig.save() ? "Saved main config." : "Could not save main config.");
    }

}
