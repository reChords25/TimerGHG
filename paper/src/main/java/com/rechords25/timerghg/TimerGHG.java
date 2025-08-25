package com.rechords25.timerghg;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public final class TimerGHG extends JavaPlugin implements Listener {
    private Timer allPlayerTimer;
    private ConfigUtil allPlayerConfig;

    @Override
    public void onEnable() {
        // Prepare config file
        allPlayerConfig = new ConfigUtil(this, "timer.yml");
        if (!allPlayerConfig.getFile().exists()) {
            saveResource("timer.yml", false);
        }
        allPlayerTimer = new Timer(this, allPlayerConfig.getConfig(), Bukkit.getOnlinePlayers());

        // Load allPlayerTimer state from timer.yml config file
        allPlayerTimer.loadConfig();

        // Connect the allPlayerTimer command with the TimerCommand class / object
        getCommand("timer").setExecutor(new TimerCommand(allPlayerTimer));

        Bukkit.getPluginManager().registerEvents(this, this);

        BukkitTask configSaver = new BukkitRunnable() {
            @Override
            public void run() {
                allPlayerTimer.saveConfig();
                allPlayerConfig.save();
            }
        }.runTaskTimer(this, 1200, 6000);
    }

    @Override
    // Runs on plugin unload (server crash / stop / restart / reload)
    public void onDisable() {
        // Save current allPlayerTimer state to the timer.yml config file
        allPlayerTimer.saveConfig();
        getLogger().info(allPlayerConfig.save() ? "Saved main config." : "Could not save main config.");
    }

//    @EventHandler
//    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
//        if (event.getAdvancement().getKey().toString().equals("minecraft:end/kill_dragon")) {
//            allPlayerTimer.stop();
//        }
//    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Bukkit.getOnlinePlayers().size() == 1) {
            if (!allPlayerTimer.mayRun()) return;
            Bukkit.getScheduler().runTaskLater(this, () -> {
                allPlayerTimer.start(allPlayerConfig.getConfig().getBoolean("upward", true));
            }, 20L);

        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Bukkit.getOnlinePlayers().isEmpty() && allPlayerTimer.isRunning()) {
                allPlayerTimer.pause();
            }
        }, 1L);
    }
}
