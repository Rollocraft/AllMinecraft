package de.rollocraft.allminecraft.Minecraft.Commands;


import de.rollocraft.allminecraft.Minecraft.Manager.Database.PositionDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.Position;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PositionCommand implements CommandExecutor, TabCompleter {

    private PositionDatabaseManager dbManager;

    public PositionCommand(PositionDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE +"Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE +"Usage: /position <create|delete|get> [name]");
            return true;
        }

        String action = args[0];
        if ("create".equals(action)) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE +"Usage: /position create <name>");
                return true;
            }

            String name = args[1];
            // Create a new position at the player's current location
            Position position = new Position(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            try {
                // Save the position to the database
                dbManager.savePositionToDatabase(name, position.getX(), position.getY(), position.getZ());
                player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE + "Position " + name + " created.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else if ("delete".equals(action)) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE +"Usage: /position delete <name>");
                return true;
            }

            String name = args[1];
            try {
                // Delete the position from the database
                dbManager.deletePositionFromDatabase(name);
                player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE + "Position " + name + " deleted.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if ("show".equals(action)) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE + "Usage: /position get <name>");
                return true;
            }

            String name = args[1];
            try {
                // Get the position from the database
                Position position = dbManager.getPositionFromDatabase(name);
                if (position == null) {
                    player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE + "No position found with the name " + name);
                } else {
                    // Display the position to the player
                    player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE + name + ": " + printPosition(position));
                    spawnParticleBeam(player, position, Particle.VILLAGER_HAPPY);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            // If no action is provided, assume the argument is a position name and get the position
            String name = action;
            try {
                // Get the position from the database
                Position position = dbManager.getPositionFromDatabase(name);
                if (position == null) {
                    player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE + "No position found with the name " + name);
                } else {
                    // Display the position to the player
                    player.sendMessage(ChatColor.AQUA + "[Position] " + ChatColor.WHITE + name + ": " + printPosition(position));
                    spawnParticleBeam(player, position, Particle.VILLAGER_HAPPY);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
    public String printPosition(Position position) {
        String x = formatFloat(position.getX());
        String y = formatFloat(position.getY());
        String z = formatFloat(position.getZ());
        return x + " " + y + " " + z;
    }

    public String formatFloat(double value) {
        String str = Double.toString(value);
        if (str.endsWith(".0")) {
            return str.substring(0, str.length() - 2);
        } else {
            return str;
        }
    }

    public void spawnParticleBeam(Player player, Position position, Particle particle) {
        Vector direction = position.toVector().subtract(player.getLocation().toVector()).normalize();
        Vector current = player.getLocation().toVector();

        while (current.distance(position.toVector()) > 1) {
            player.getWorld().spawnParticle(particle, current.toLocation(player.getWorld()), 1);
            current.add(direction);
        }
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("position")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();

                arguments.add("create");
                arguments.add("delete");
                arguments.add("show");

                return arguments;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
                List<String> positionNames = new ArrayList<>();
                try {
                    // Get all position names from the database
                    String sql = "SELECT name FROM positions";
                    PreparedStatement statement = dbManager.getConnection().prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        positionNames.add(resultSet.getString("name"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return positionNames;
            }
        }

        return null;
    }
}