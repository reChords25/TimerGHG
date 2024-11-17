package com.rechords25.timerghg;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Configuration utility class
 * Can be used for different plugins that require additional configuration files
 */
public class ConfigUtil {
    private File file;
    private FileConfiguration config;

    /**
     * Constructor for the ConfigUtil object
     *
     * @param plugin the plugin the configuration is made for
     * @param path the path (mostly filename) of the configuration file
     */
    public ConfigUtil(Plugin plugin, String path) {
        this(plugin.getDataFolder().getAbsolutePath() + "/" + path);
    }

    /**
     * The private constructor run by the public constructor
     * Initializes objects
     *
     * @param path the path of the configuration file, including the file name
     */
    private ConfigUtil(String path) {
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Saves the current configuration to the YML file
     *
     * @return successful whether the file was successfully saved; if not, the stacktrace is logged
     */
    public boolean save() {
        try {
            this.config.save(this.file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Getter for the configuration's File object
     *
     * @return configFile the configuration file object
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Getter for the FileConfiguration (the config used in the code)
     *
     * @return config the configuration object
     */
    public FileConfiguration getConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
        return this.config;
    }
}