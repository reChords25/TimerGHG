package com.rechords25.timerghg;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimerCommand implements CommandExecutor, TabCompleter {
    private final Timer timer;

    public TimerCommand(Timer timer) {
        this.timer = timer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 10) {
            sender.sendMessage("Too many arguments.");
            return false;
        }
        String arg1 = args.length > 0 ? args[0].toLowerCase() : "";
        String arg2 = args.length > 1 ? args[1].toLowerCase() : "";
        String arg3 = args.length > 2 ? args[2].toLowerCase() : "";
        if (arg1.isEmpty()) {
            sender.sendMessage("You need to specify arguments like start or pause.");
            return false;
        } else if (!Arrays.asList("start", "pause", "stop", "reset", "style", "set", "add", "subtract").contains(arg1)) {
            sender.sendMessage("Unknown argument. Specify arguments like start or pause.");
            return false;
        }
        if (arg1.equals("start")) {
            if (arg2.isEmpty()) {
                timer.start(true);
            } else {
                switch (arg2) {
                    case "forward":
                        timer.start(true);
                        break;
                    case "backward":
                        timer.start(false);
                        break;
                    default:
                        sender.sendMessage("Unknown argument. Enter forward or backward.");
                }
            }
        } else if (arg1.equals("pause")) {
            timer.pause();
        } else if (arg1.equals("stop")) {
            timer.stop();
        } else if (arg1.equals("reset")) {
            timer.reset();
        } else if (arg1.equals("style")) {
            if (arg2.isEmpty()) {
                sender.sendMessage("You need to specify which style to edit and name a value.");
                return false;
            } else if (arg2.equals("color")) {
                if (arg3.isEmpty()) {
                    sender.sendMessage("You need to specify a color as hex code or Minecraft color name.");
                    return false;
                } else if (!Arrays.asList(
                        "black", "dark_gray", "gray",
                        "white", "red", "gold",
                        "yellow", "aqua", "blue",
                        "dark_red", "dark_green", "dark_blue",
                        "dark_aqua", "dark_purple", "light_purple"
                ).contains(arg3) && arg3.matches("^#([0-9A-Fa-f]{6})$")) {
                    sender.sendMessage(String.format("\"%s\" is not a known color.", arg3));
                    return false;
                } else {
                    timer.setColor(arg3);
                }

            } else if (Arrays.asList("bold", "underline", "italic").contains(arg2)) {
                if (arg3.isEmpty()) {
                    sender.sendMessage("You need to specify which style to edit and name a value.");
                    return false;
                } else if (arg3.equals("false")) {
                    timer.setDecoration(arg3, false);
                } else if (arg3.equals("true")) {
                    timer.setDecoration(arg3, true);
                } else {
                    sender.sendMessage("You have to set a value (true or false).");
                    return false;
                }
            } else {
                sender.sendMessage("Unknown style setting.");
            }
        } else if (Arrays.asList("set", "add", "subtract").contains(arg1)) {
            if (arg2.isEmpty()) {
                sender.sendMessage("You have to enter a time like \"1h 30m\".");
                return false;
            }
            for (String timeArg : Arrays.copyOfRange(args, 1, args.length)) {
                if (!timeArg.matches("^[0-9]+[smhdy]$")) {
                    sender.sendMessage(String.format("\"%s\" is not a valid time setting.", timeArg));
                    return false;
                }
            }
            timer.editTime(arg1, Arrays.copyOfRange(args, 1, args.length));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            List<String> timerArgs = Arrays.asList("start", "pause", "stop", "reset", "style", "set", "add", "subtract");
            for (String timerArg : timerArgs) {
                if (timerArg.startsWith(args[0].toLowerCase())) {
                    list.add(timerArg);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            List<String> formatArgs = Arrays.asList("backward", "forward");
            for (String formatArg : formatArgs) {
                if (formatArg.startsWith(args[1].toLowerCase())) {
                    list.add(formatArg);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("style")) {
            List<String> formatArgs = Arrays.asList("color", "italic", "bold", "underline");
            for (String formatArg : formatArgs) {
                if (formatArg.startsWith(args[1].toLowerCase())) {
                    list.add(formatArg);
                }
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("color")) {
                List<String> colorArgs = Arrays.asList(
                        "black", "dark_gray", "gray", "white", "red", "gold", "yellow", "green",
                        "aqua", "blue", "dark_red", "dark_green", "dark_blue", "dark_aqua",
                        "dark_purple", "light_purple", "#"
                );
                for (String colorArg : colorArgs) {
                    if (colorArg.startsWith(args[2].toLowerCase())) {
                        list.add(colorArg);
                    }
                }
            } else if (args[1].equalsIgnoreCase("italic") || args[1].equalsIgnoreCase("bold") || args[1].equalsIgnoreCase("underline")) {
                List<String> formatOptions = Arrays.asList("true", "false");
                for (String formatOption : formatOptions) {
                    if (formatOption.startsWith(args[2].toLowerCase())) {
                        list.add(formatOption);
                    }
                }
            }
        }
        return list;
    }
}
