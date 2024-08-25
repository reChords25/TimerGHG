package com.rechords25.timerghg;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class Timer {
    private final TimerGHG plugin;

    private int seconds, minutes, hours, days, years, statusIndex;
    private String status;
    private Style style;
    private BukkitTask task;
    private boolean running, initialized, forward;

    public Timer(TimerGHG plugin) {
        this.plugin = plugin;
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
        this.days = 0;
        this.years = 0;
        setStatus(0);
        this.running = false;
        this.initialized = false;
        this.style = Style.style(TextDecoration.BOLD, TextColor.color(255, 255, 255));
    }

    public void start(boolean isForward) {
        forward = isForward;
        if (!forward && isZero()) setTime(0, 0, 1, 0, 0, "set");
        if (statusIndex == 0 || statusIndex == 1) setStatus(0);

        if (initialized) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
            if (running && statusIndex != 2 && statusIndex != 3) {
                timerTick();
            }
            updateActionBar();
            }
        }.runTaskTimer(plugin, 20, 20);
        initialized = true;
    }

    public void pause() {
        running = false;
        setStatus(1);
    }

    public void stop() {
        if (isZero()) {
            setStatus(3);
        } else {
            setStatus(2);
        }
    }

    public void reset() {
        setStatus(-1);
        setZero();
        running = false;
        initialized = false;
        style = Style.style(TextDecoration.BOLD, TextColor.color(255, 255, 255));
        if (task != null) {
            task.cancel();
            task = null;
        }
        updateActionBar();
    }

    public void editTime(String mode, String[] times) {
        int y, d, h, m, s;
        y = d = h = m = s = 0;
        for (String time : times) {
            int number = Integer.parseInt(time.substring(0, time.length() - 1));
            switch (time.charAt(time.length() - 1)) {
                case 'y':
                    y += number;
                    break;
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
        setTime(y, d, h, m, s, mode);
    }

    private void setTime(int y, int d, int h, int m, int s, String mode) {
        long totalSeconds = seconds + 60 * (minutes + 60 * (hours + 24 * (days + 365L * years)));
        long tempSeconds = s + 60 * (m + 60 * (h + 24 * (d + 365L * y)));
        switch (mode) {
            case "set":
                totalSeconds = tempSeconds;
                break;
            case "add":
                totalSeconds += tempSeconds;
                break;
            case "subtract":
                totalSeconds -= tempSeconds;
                break;
        }
        if (totalSeconds <= 0) {
            setZero();
        } else {
            years = (int) (totalSeconds / 31536000);
            totalSeconds %= 31536000;
            days = (int) (totalSeconds / 86400);
            totalSeconds %= 86400;
            hours = (int) (totalSeconds / 3600);
            totalSeconds %= 3600;
            minutes = (int) (totalSeconds / 60);
            totalSeconds %= 60;
            seconds = (int) totalSeconds;
        }
        if(running) updateActionBar();
    }

    public void setColor(String colorString) {
        int red, blue, green;
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
            case "green":
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
        style = style.toBuilder().color(TextColor.color(red, green, blue)).build();
        updateActionBar();
    }

    public void setDecoration(String formatType, boolean value) {
        switch (formatType) {
            case "bold":
                style = style.toBuilder().decoration(TextDecoration.BOLD, value).build();
                break;
            case "italic":
                style = style.toBuilder().decoration(TextDecoration.ITALIC, value).build();
                break;
            case "underline":
                style = style.toBuilder().decoration(TextDecoration.UNDERLINED, value).build();
                break;
            default:
                return;
        }
        updateActionBar();
    }

    /* ------------------------------------ */

    private void timerTick() {
        if (forward) {
            if (seconds < 59) {
                seconds++;
                return;
            } else {
                seconds = 0;
                minutes++;
            }
            if (minutes == 60) {
                minutes = 0;
                hours++;
            } else {
                return;
            }
            if (hours == 24) {
                hours = 0;
                days++;
            } else {
                return;
            }
            if (days == 365) {
                days = 0;
                years++;
            }
        } else {
            seconds--;
            if (isZero()) {
                setZero();
                stop();
                return;
            }
            seconds++;
            if (seconds > 0) {
                seconds--;
            } else {
                seconds = 59;
                if (minutes > 0) {
                    minutes--;
                } else {
                    minutes = 59;
                    if (hours > 0) {
                        hours--;
                    } else {
                        hours = 23;
                        if (days > 0) {
                            days--;
                        } else {
                            days = 364;
                            if (years > 0) {
                                years--;
                            } else {
                                setZero();
                                stop();
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isZero() {
        return seconds == 0 && minutes == 0 && hours == 0 && days == 0 && years == 0;
    }
    
    private void setZero() {
        seconds = minutes = hours = days = years = 0;
    }

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
                return;
        }
        statusIndex = index;
        updateActionBar();
    }

    private void updateActionBar() {
        String message = getTime();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Audience audience = plugin.getServer().getPlayer(player.getUniqueId());
            assert audience != null;
            audience.sendActionBar(Component.text(message, style));
        }
    }

    @NotNull
    private String getTime() {
        String time = "";
        if (years > 0) {
            time += String.valueOf(years);
            time += "y ";
        }
        if (days > 0) {
            time += String.valueOf(days);
            time += "d ";
        }
        if (hours > 0) {
            time += String.valueOf(hours);
            time += "h ";
        }
        if (minutes > 0) {
            time += String.valueOf(minutes);
            time += "m ";
        }
        if (running || seconds > 0) {
            time += String.valueOf(seconds);
            time += "s ";
        }
        time += status;
        return time;
    }

    /* ------------------------------------- */

    public void loadConfig(FileConfiguration config) {
        years = config.getInt("time.years", 0);
        days = config.getInt("time.days", 0);
        hours = config.getInt("time.hours", 0);
        minutes = config.getInt("time.minutes", 0);
        seconds = config.getInt("time.seconds", 0);
        initialized = config.getBoolean("config.initialized", false);
        forward = config.getBoolean("config.forward", true);
        statusIndex = config.getInt("config.statusIndex", 0);
        setStatus(statusIndex);
        setDecoration("bold", config.getBoolean("config.style.bold", true));
        setDecoration("italic", config.getBoolean("config.style.italic", false));
        setDecoration("underline", config.getBoolean("config.style.underline", false));
        style = style.toBuilder().color(TextColor.color(
                config.getInt("config.style.color.red", 255),
                config.getInt("config.style.color.green", 255),
                config.getInt("config.style.color.blue", 255)
        )).build();
        if(initialized) start(forward);
    }

    public void saveConfig(FileConfiguration config) {
        config.set("time.years",years);
        config.set("time.days", days);
        config.set("time.hours", hours);
        config.set("time.minutes", minutes);
        config.set("time.seconds", seconds);
        config.set("config.initialized", initialized);
        config.set("config.forward", forward);
        config.set("config.statusIndex", statusIndex);
        config.set("config.style.color.red", style.color().red());
        config.set("config.style.color.green", style.color().green());
        config.set("config.style.color.blue", style.color().blue());
        config.set("config.style.bold", style.decorations().get(TextDecoration.BOLD).toString().equalsIgnoreCase("true"));
        config.set("config.style.italic", style.decorations().get(TextDecoration.ITALIC).toString().equalsIgnoreCase("true"));
        config.set("config.style.underlined", style.decorations().get(TextDecoration.UNDERLINED).toString().equalsIgnoreCase("true"));
    }
}
