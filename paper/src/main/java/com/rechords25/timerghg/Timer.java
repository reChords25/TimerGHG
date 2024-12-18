package com.rechords25.timerghg;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.audience.Audience;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.Duration;


public class Timer {
    // Plugin reference
    private final TimerGHG plugin;
    // Config reference
    private final FileConfiguration config;

    // Time and status for the action bar
    private Duration time;
    private int statusIndex;
    private int red, green, blue;
    private boolean bold, italic, underlined;
    private String status;

    // Style for the action bar, containing color and formatting like boldness
    private Style style;

    // Timer task which runs every 20 ticks, so one second; currently synchronized
    private BukkitTask task;

    // Timer settings determining its behavior
    private boolean running, initialized, upward;

    /**
     * Constructor for a Timer object
     *
     * @param plugin the parent plugin of type {@link TimerGHG}
     * @param config the configuration for the timer
     */
    public Timer(TimerGHG plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        this.style = Style.empty();
        this.time = Duration.ZERO;
    }

    /**
     * Starts the timer both technically and visually
     *
     * @param isUpward whether the timer should run 
     */
    public void start(boolean isUpward) {
        upward = isUpward;
        if (!upward && isZero()) setTime(0, 1, 0, 0, "set");

        if (statusIndex == 0 || statusIndex == 1) setStatus(0);
        startTimerTask();
        sendActionBar(Component.text(isZero() ? "Timer started!" : "Timer resumed!").style(style));
    }

    /**
     * Pauses the timer
     */
    public void pause() {
        running = false;
        setStatus(1);
        sendActionBar(Component.text("Timer paused!").style(style));
    }

    /**
     * Stops the timer
     * Like that, the timer is locked and can only be reset by calling the reset function
     */
    public void stop() {
        if (isZero()) {
            setStatus(3);
        } else {
            setStatus(2);
            sendActionBar(Component.text("Timer stopped!").style(style));
        }
    }

    /**
     * Resets the timer
     * @param what what should be reset, defaults-confirm resetting the defaults, others loading them
     */
    public void reset(String what) {
        switch (what) {
            case "time":
                loadDefaultTime();
                break;
            case "style":
                loadDefaultStyle();
                break;
            case "settings":
                loadDefaultSettings();
                break;
            case "all":
                loadDefaultTime();
                loadDefaultStyle();
                loadDefaultSettings();
                break;
            case "defaults-confirm":
                resetDefaults();
                break;
        }
        sendTime();
    }

    /**
     * Edits the currently shown time
     *
     * @param mode the mode, available options are "set", "add" and "subtract"
     * @param times the given arguments, such as "1h" or "4m" or (together) "1h 4m 34s"
     */
    public void editTime(String mode, String[] times) {
        int d, h, m, s;
        d = h = m = s = 0;
        for (String timeStr : times) {
            int number = Integer.parseInt(timeStr.substring(0, timeStr.length() - 1));
            switch (timeStr.charAt(timeStr.length() - 1)) {
                case 'd':
                    d += number;
                    break;
                case 'h':
                    h += number;
                    break;
                case 'm':
                    m += number;
                    break;
                case 's':
                    s += number;
                    break;
            }
        }
        setTime(d, h, m, s, mode);
    }

    /**
     * Sets the currently shown time
     * Used by editTime() to set the time
     *
     * @param d days
     * @param h hours
     * @param m minutes
     * @param s seconds
     * @param mode add, subtract, set
     */
    private void setTime(int d, int h, int m, int s, String mode) {
        switch (mode) {
            case "set":
                time = Duration.ZERO.plusDays(d).plusHours(h).plusMinutes(m).plusSeconds(s);
                break;
            case "add":
                time = time.plusDays(d).plusHours(h).plusMinutes(m).plusSeconds(s);
                break;
            case "subtract":
                time = time.minusDays(d).minusHours(h).minusMinutes(m).minusSeconds(s);
                break;
        }
        if (running) sendTime();
    }

    /**
     * Makes the color of the time the color in the given String
     * Colors can be given as hex code or Minecraft color name
     *
     * @param colorString the String the color is in
     */
    public void setColor(String colorString) {
        switch (colorString) {
            case "black":
                red = 0;
                green = 0;
                blue = 0;
                break;
            case "dark_gray":
                red = 85;
                green = 85;
                blue = 85;
                break;
            case "gray":
                red = 170;
                green = 170;
                blue = 170;
                break;
            case "white":
                red = 255;
                green = 255;
                blue = 255;
                break;
            case "red":
                red = 255;
                green = 85;
                blue = 85;
                break;
            case "gold":
                red = 255;
                green = 170;
                blue = 0;
                break;
            case "yellow":
                red = 255;
                green = 255;
                blue = 85;
                break;
            case "lime":
                red = 85;
                green = 255;
                blue = 85;
                break;
            case "aqua":
                red = 85;
                green = 255;
                blue = 255;
                break;
            case "blue":
                red = 85;
                green = 85;
                blue = 255;
                break;
            case "dark_red":
                red = 170;
                green = 0;
                blue = 0;
                break;
            case "dark_green":
                red = 0;
                green = 170;
                blue = 0;
                break;
            case "dark_blue":
                red = 0;
                green = 0;
                blue = 170;
                break;
            case "dark_aqua":
                red = 0;
                green = 170;
                blue = 170;
                break;
            case "dark_purple":
                red = 170;
                green = 0;
                blue = 170;
                break;
            case "light_purple":
                red = 255;
                green = 85;
                blue = 255;
                break;
            default:
                red = Integer.parseInt(colorString.substring(1,3), 16);
                green = Integer.parseInt(colorString.substring(3,5), 16);
                blue = Integer.parseInt(colorString.substring(5,7), 16);
        }
        makeStyle();
        sendTime();
    }

    /**
     * Applies the given text decoration to the time
     *
     * @param formatType the decoration
     * @param value the value to set
     */
    public void setDecoration(String formatType, boolean value) {
        switch (formatType) {
            case "bold":
                bold = value;
                break;
            case "italic":
                italic = value;
                break;
            case "underline":
                underlined = value;
                break;
            default:
                return;
        }
        makeStyle();
        sendTime();
    }

    /* ------------------------------------ */

    /**
     * Creates a {@link Style} from the style settings of the timer
     */
    private void makeStyle() {
        style = style.toBuilder()
                .color(TextColor.color(red, green, blue))
                .decoration(TextDecoration.BOLD, bold)
                .decoration(TextDecoration.ITALIC, italic)
                .decoration(TextDecoration.UNDERLINED, underlined)
                .build();
    }

    /**
     * Starts the {@link BukkitTask} for the timer
     * Task runs every 20 ticks (1 second) and starts delayed by the same amount
     * timerTick() is only run when timer is running, else the task prevents the action bar from disappearing
     */
    private void startTimerTask() {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (running && statusIndex != 2 && statusIndex != 3) {
                    timerTick();
                }
                sendTime();
            }
        }.runTaskTimer(plugin, 20, 20);
        initialized = true;
    }

    /**
     * Adds or subtracts one second from the timer, depending on whether it is running upwards or downwards
     */
    private void timerTick() {
        if (upward) {
            time = time.plusSeconds(1);
        } else {
            time = time.minusSeconds(1);
        }
        if (isZero()) {
            stop();
        }
    }

    /**
     * @return isZero whether the time is exactly zero
     */
    private boolean isZero() {
        return time.getSeconds() == 0;
    }

    /**
     * Sets the text status and internal status index
     *
     * @param index determines the status text to display
     */
    private void setStatus(int index) {
        switch (index) {
            case 0:
                status = "";
                running = true;
                break;
            case 1:
                status = "(paused)";
                running = false;
                break;
            case 2:
                status = "(stopped)";
                running = false;
                break;
            case 3:
                status = "Time has run out!";
                running = false;
                break;
            default:
                status = "";
                statusIndex = 0;
                return;
        }
        statusIndex = index;
        sendTime();
    }

    /**
     * Sends the time to the action bar with the current time, status and style
     */
    private void sendTime() {
        sendActionBar(Component.text(getTimeString() + " " + status).style(style));
    }

    /**
     * Sends the action bar to the selected players
     */
    private void sendActionBar(Component content) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Audience audience = plugin.getServer().getPlayer(player.getUniqueId());
            assert audience != null;
            audience.sendActionBar(content);
        }
    }

    /**
     * @return time the time shown in the action bar
     */
    private String getTimeString() {
        String days = time.toDaysPart() == 0 ? "" : time.toDaysPart() + "d ";
        String hours = time.toHoursPart() == 0 ? "" : time.toHoursPart() + "h ";
        String minutes = time.toMinutesPart() == 0 ? "" : time.toMinutesPart() + "m ";
        String seconds = time.toSecondsPart() == 0 ? "" : time.toSecondsPart() + "s";
        return (days + hours + minutes + seconds).strip();
    }

    /* ------------------------------------- */

    /**
     * Loads the configuration
     */
    public void loadConfig() {
        time = Duration.ZERO
                .plusDays(config.getInt("time.days", 0))
                .plusHours(config.getInt("time.hours", 0))
                .plusMinutes(config.getInt("time.minutes", 0))
                .plusSeconds(config.getInt("time.seconds", 0));
        initialized = config.getBoolean("state.initialized", false);
        upward = config.getBoolean("settings.upward", true);
        setStatus(config.getInt("state.statusindex", 0));

        red = config.getInt("state.style.color.red", 255);
        green = config.getInt("state.style.color.green", 255);
        blue = config.getInt("state.style.color.blue", 255);

        bold = config.getBoolean("state.style.bold", true);
        italic = config.getBoolean("state.style.italic", true);
        underlined = config.getBoolean("state.style.underlined", true);

        makeStyle();

        if (initialized) {
            if (statusIndex == 0) {
                start(upward);
            } else {
                startTimerTask();
            }
        }
    }

    /**
     * Loads the default time from the configuration
     * Resets the timer task
     */
    private void loadDefaultTime() {
        time = Duration.ZERO
                .plusDays(config.getInt("defaults.time.days", 0))
                .plusHours(config.getInt("defaults.time.hours", 0))
                .plusMinutes(config.getInt("defaults.time.minutes", 0))
                .plusSeconds(config.getInt("defaults.time.seconds", 0));

        setStatus(-1);
        running = false;
        initialized = false;
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /**
     * Loads the default styling from the configuration
     */
    private void loadDefaultStyle() {
        red = config.getInt("defaults.style.color.red", 255);
        green = config.getInt("defaults.style.color.green", 255);
        blue = config.getInt("defaults.style.color.blue", 255);

        bold = config.getBoolean("defaults.style.bold", true);
        italic = config.getBoolean("defaults.style.italic", false);
        underlined = config.getBoolean("defaults.style.italic", false);

        makeStyle();
    }

    /**
     * Loads the default settings from the configuration
     */
    private void loadDefaultSettings() {
        upward = config.getBoolean("defaults.settings.upward", true);
    }

    /**
     * Resets even the default values set in the configuration to the shipped defaults
     */
    private void resetDefaults() {
        config.set("defaults.time.days", 0);
        config.set("defaults.time.hours", 0);
        config.set("defaults.time.minutes", 0);
        config.set("defaults.time.seconds", 0);

        config.set("defaults.style.color.red", 255);
        config.set("defaults.style.color.green", 255);
        config.set("defaults.style.color.blue", 255);
        config.set("defaults.style.bold", true);
        config.set("defaults.style.italic", false);
        config.set("defaults.style.underlined", false);

        config.set("defaults.settings.upward", true);
    }

    /**
     * Saves current state to the configuration
     */
    public void saveConfig() {
        config.set("time.days", time.toDaysPart());
        config.set("time.hours", time.toHoursPart());
        config.set("time.minutes", time.toMinutesPart());
        config.set("time.seconds", time.toSecondsPart());
        config.set("state.initialized", initialized);
        config.set("settings.upward", upward);
        config.set("state.statusindex", statusIndex);
        if (style.color() != null) {
            config.set("state.style.color.red", style.color().red());
            config.set("state.style.color.green", style.color().green());
            config.set("state.style.color.blue", style.color().blue());
        }
        config.set("state.style.bold", style.hasDecoration(TextDecoration.BOLD));
        config.set("state.style.italic", style.hasDecoration(TextDecoration.ITALIC));
        config.set("state.style.underlined", style.hasDecoration(TextDecoration.UNDERLINED));
    }

    /* ------------------------------------- */

    /**
     * Needed to determine whether the timer is allowed to run
     * If the timer is stopped, it may not be run again
     *
     * @return mayRun whether the timer may run or not
     */
    public boolean mayRun() {
        return statusIndex != 2 && statusIndex != 3;
    }
}

