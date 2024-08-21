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
        if (args.length == 0) {
            sender.sendMessage("You need to specify arguments like start or pause.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (args.length == 1) {
                    timer.start(true);
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "forward":
                        timer.start(true);
                        break;
                    case "backward":
                        timer.start(false);
                        break;
                    default:
                        sender.sendMessage("Wrong mode. Only forward or backward supported.");
                        break;
                }
                break;
            case "pause":
                timer.pause();
                break;
            case "stop":
                timer.stop();
                break;
            case "reset":
                timer.reset();
                break;
            case "style":
                switch (args[1].toLowerCase()) {
                    case "color":
                        if (!timer.setColor(args[2])) {
                            sender.sendMessage("Unknown color. Use suggested colors or hex code like #FFFFFF.");
                        }
                        break;
                    case "bold":
                        if (!timer.setDecoration("bold", args[2])) {
                            sender.sendMessage("Specify boldness with true or false.");
                        }
                        ;
                        break;
                    case "italic":
                        if (!timer.setDecoration("italic", args[2])) {
                            sender.sendMessage("Specify italicness with true or false.");
                        }
                        ;
                        break;
                    case "underline":
                        if (!timer.setDecoration("underline", args[2])) {
                            sender.sendMessage("Specify whether to underline with true or false.");
                        }
                        break;
                    default:
                        sender.sendMessage("Unknown style setting.");
                        break;
                }
                break;
            case "set":
                if (!timer.set(Arrays.copyOfRange(args, 1, args.length))) {
                    sender.sendMessage("Wrong argument(s). Enter desired time in timer format and keep it small enough.");
                }
                break;
            case "add":
                if (!timer.add(Arrays.copyOfRange(args, 1, args.length))) {
                    sender.sendMessage("Wrong argument(s). Enter desired time in timer format and keep it small enough.");
                }
                break;
            case "subtract":
                if (!timer.subtract(Arrays.copyOfRange(args, 1, args.length))) {
                    sender.sendMessage("Wrong argument(s). Enter desired time in timer format and keep it small enough.");
                }
                break;
            default:
                sender.sendMessage("Unknown argument.");
                break;

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
                        "black", "dark_grey", "gray", "white", "red", "gold", "yellow", "green",
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
