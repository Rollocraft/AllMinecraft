package de.rollocraft.allminecraft.Minecraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.TimerManager;
import de.rollocraft.allminecraft.Minecraft.Database.TimerDatabaseManager;
import org.bukkit.command.TabCompleter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimerCommand implements CommandExecutor, TabCompleter {
    private TimerDatabaseManager dbManager;

    public TimerCommand(TimerDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "resume": {
                TimerManager timerManager = Main.getInstance().getTimer();

                if (timerManager.isRunning()) {
                    sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.RED + "Der Timer läuft bereits.");
                    break;
                }

                timerManager.setRunning(true);
                try {
                    dbManager.saveTimer(timerManager); // Save the timer value
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.WHITE + "Der Timer wurde gestartet.");
                break;
            }
            case "stop":
            case "pause": {
                TimerManager timerManager = Main.getInstance().getTimer();

                if (!timerManager.isRunning()) {
                    sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.RED + "Der Timer läuft nicht.");
                    break;
                }

                timerManager.setRunning(false);
                try {
                    dbManager.saveTimer(timerManager); // Save the timer value
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.WHITE + "Der Timer wurde gestoppt.");
                break;
            }
            case "set": {
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.WHITE + "Verwendung" + ChatColor.DARK_GRAY + ": " + ChatColor.BLUE +
                            "/timer set <Zeit>");
                    return true;
                }

                try {
                    TimerManager timerManager = Main.getInstance().getTimer();

                    timerManager.setRunning(false);
                    timerManager.setTime(Integer.parseInt(args[1]));
                    dbManager.saveTimer(timerManager); // Save the timer value
                    sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.WHITE + "Die Zeit wurde auf " + args[1] + " gesetzt.");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Dein Parameter 2 muss eine Zahl sein.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "reset": {
                TimerManager timerManager = Main.getInstance().getTimer();

                timerManager.setRunning(false);
                timerManager.setTime(0);
                try {
                    dbManager.saveTimer(timerManager); // Save the timer value
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.WHITE + "Der Timer wurde zurückgesetzt.");
                break;
            }
            default:
                sendUsage(sender);
                break;
        }
        return false;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[Timer] " + ChatColor.WHITE+ "Verwendung" + ChatColor.DARK_GRAY + ": " + ChatColor.BLUE +
                "/timer resume, /timer pause, /timer set <Zeit>, /timer reset");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("timer")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();

                arguments.add("resume");
                arguments.add("pause");
                arguments.add("stop");

                return arguments;
            }
        }
        return null;
    }
}