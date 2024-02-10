package de.rollocraft.allminecraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Manager.Timer;
import de.rollocraft.allminecraft.Manager.Database.TimerDatabaseManager;
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
        if(args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "resume": {
                Timer timer = Main.getInstance().getTimer();

                if (timer.isRunning()) {
                    sender.sendMessage(ChatColor.RED + "Der Timer läuft bereits.");
                    break;
                }

                timer.setRunning(true);
                try {
                    dbManager.saveTimer(timer); // Save the timer value
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.GRAY + "Der Timer wurde gestartet.");
                break;
            }
            case "pause": {
                Timer timer = Main.getInstance().getTimer();

                if (!timer.isRunning()) {
                    sender.sendMessage(ChatColor.RED + "Der Timer läuft nicht.");
                    break;
                }

                timer.setRunning(false);
                try {
                    dbManager.saveTimer(timer); // Save the timer value
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.GRAY + "Der Timer wurde gestoppt.");
                break;
            }
            case "set": {
                if(args.length != 2) {
                    sender.sendMessage(ChatColor.GRAY + "Verwendung" + ChatColor.DARK_GRAY + ": " + ChatColor.BLUE +
                            "/timer set <Zeit>");
                    return true;
                }

                try {
                    Timer timer = Main.getInstance().getTimer();

                    timer.setRunning(false);
                    timer.setTime(Integer.parseInt(args[1]));
                    dbManager.saveTimer(timer); // Save the timer value
                    sender.sendMessage(ChatColor.GRAY + "Die Zeit wurde auf " + args[1] + " gesetzt.");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Dein Parameter 2 muss eine Zahl sein.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "reset": {
                Timer timer = Main.getInstance().getTimer();

                timer.setRunning(false);
                timer.setTime(0);
                try {
                    dbManager.saveTimer(timer); // Save the timer value
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.GRAY + "Der Timer wurde zurückgesetzt.");
                break;
            }
            default:
                sendUsage(sender);
                break;
        }
        return false;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Verwendung" + ChatColor.DARK_GRAY + ": " + ChatColor.BLUE +
                "/timer resume, /timer pause, /timer set <Zeit>, /timer reset");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("timer")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();

                arguments.add("resume");
                arguments.add("pause");
                arguments.add("set");
                arguments.add("reset");

                return arguments;
            }
        }

        return null;
    }
}