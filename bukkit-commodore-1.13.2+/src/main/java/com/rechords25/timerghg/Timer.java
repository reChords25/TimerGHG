package com.rechords25.timerghg;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class Timer {
    private final TimerGHG plugin;
    private int seconds, minutes, hours, days, years;
    private String status;
    private Style style;
    private BukkitTask task;
    private boolean running;
    private boolean initialized;
    public Timer(TimerGHG plugin) {
        this.plugin = plugin;
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
        this.days = 0;
        this.years = 0;
        this.status = "";
        this.running = false;
        this.initialized = false;
        this.style = Style.style(TextDecoration.BOLD, TextColor.color(255, 255, 255));
    }

    public void start() {
        running = true;
        status = "";
        updateActionBar();

        if (initialized) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (running) {
                    timerTick();
                }
                updateActionBar();
                //Bukkit.getLogger().info("Running.");
            }
        }.runTaskTimer(plugin, 0, 20);
        initialized = true;
    }

    public void pause() {
        running = false;
        status = "(paused)";
        updateActionBar();
    }

    public void stop() {
        running = false;
        status = "(stopped)";
        updateActionBar();
    }

    public void reset() {
        running = false;
        initialized = false;
        seconds = 0;
        minutes = 0;
        hours = 0;
        days = 0;
        years = 0;
        style = Style.style(TextDecoration.BOLD, TextColor.color(255, 255, 255));
        if (task != null) {
            task.cancel();
            task = null;
        }
        updateActionBar();
    }

    public boolean setColor(String colorString) {
        int red, blue, green;
        switch (colorString) {
            case "black":
                red = 0;
                green = 0;
                blue = 0;
                break;
            case "dark_grey":
                red = 85;
                green = 85;
                blue = 85;
                break;
            case "grey":
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
                if (colorString.length() != 7) return false;
                red = Integer.parseInt(colorString.substring(1,3), 16);
                green = Integer.parseInt(colorString.substring(3,5), 16);
                blue = Integer.parseInt(colorString.substring(5,7), 16);
        }
        style = style.toBuilder().color(TextColor.color(red, green, blue)).build();
        updateActionBar();
        return true;
    }

    public boolean setDecoration(String formatType, String s) {
        boolean value;
        if (s.equalsIgnoreCase("true")) {
            value = true;
        } else if (s.equalsIgnoreCase("false")) {
            value = false;
        } else { return false; }
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
                return false;
        }
        updateActionBar();
        return true;
    }

    public boolean set(String[] times) {
        return evaluateSetCommand(times, 0);
    }

    public boolean add(String[] times) {
        return evaluateSetCommand(times, 1);
    }

    public boolean subtract(String[] times) {
        return evaluateSetCommand(times, 2);
    }

    private boolean evaluateSetCommand(String[] times, int mode) {
        int y, d, h, m, s;
        y = d = h = m = s = 0;
        for (String time : times) {
            int number;
            String numberString = time.substring(0, time.length() - 1);
            if (numberString.matches("[0-9]+")) {
                number = Integer.parseInt(numberString);
            } else { return false; }
            switch (time.charAt(time.length() - 1)) {
                case 'y':
                    y = number;
                    break;
                case 'd':
                    d = number;
                    break;
                case 'h':
                    h = number;
                    break;
                case 'm':
                    m = number;
                    break;
                case 's':
                    s = number;
                    break;
                default:
                    return false;
            }
        }
        return setTimer(y, d, h, m, s, mode);
    }
    private boolean setTimer(int y, int d, int h, int m, int s, int mode) {
        try {
            int totalSeconds = seconds + 60 * (minutes + 60 * (hours + 24 * (days + 365 * years)));
            int tempSeconds = s + 60 * (m + 60 * (h + 24 * (d + 365 * y)));
            switch (mode) {
                case 0:
                    totalSeconds = tempSeconds;
                    break;
                case 1:
                    totalSeconds += tempSeconds;
                    break;
                case 2:
                    totalSeconds -= tempSeconds;
                    break;
            }
            if (totalSeconds <= 0) {
                years = days = hours = minutes = seconds = 0;
            } else {
                years = totalSeconds / 31536000;
                totalSeconds %= 31536000;
                days = totalSeconds / 86400;
                totalSeconds %= 86400;
                hours = totalSeconds / 3600;
                totalSeconds %= 3600;
                minutes = totalSeconds / 60;
                totalSeconds %= 60;
                seconds = totalSeconds;
            }
        } catch(NumberFormatException e) {
            return false;
        }
        updateActionBar();
        return true;
    }

    public void timerTick() {
        if (seconds < 59) {
            seconds++;
        } else {
            seconds = 0;
            minutes ++;
        }
        if (minutes == 60) {
            minutes = 0;
            hours++;
        }
        if (hours == 24) {
            hours = 0;
            days++;
        }
        if (days == 365) {
            days = 0;
            years++;
        }
    }

    private void updateActionBar() {
        String message = getTimeString();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Audience audience = plugin.getServer().getPlayer(player.getUniqueId());
            assert audience != null;
            audience.sendActionBar(Component.text(message, style));
        }
    }

    @NotNull
    private String getTimeString() {
        String timeString = "";
        if (years > 0) {
            timeString += String.valueOf(years);
            timeString += "y ";
        }
        if (days > 0) {
            timeString += String.valueOf(days);
            timeString += "d ";
        }
        if (hours > 0) {
            timeString += String.valueOf(hours);
            timeString += "h ";
        }
        if (minutes > 0) {
            timeString += String.valueOf(minutes);
            timeString += "m ";
        }
        if (seconds > 0) {
            timeString += String.valueOf(seconds);
            timeString += "s ";
        }
        timeString += status;
        return timeString;
    }
}