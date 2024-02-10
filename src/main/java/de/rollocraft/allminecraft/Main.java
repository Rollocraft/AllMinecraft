package de.rollocraft.allminecraft;

import de.rollocraft.allminecraft.Commands.SkipItemCommand;
import de.rollocraft.allminecraft.Events.PlayerJoinListener;
import de.rollocraft.allminecraft.Events.PlayerPickupEvent;
import de.rollocraft.allminecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Manager.Database.ItemDatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {
    private ItemDatabaseManager databaseManager;
    private BossBarManager bossBarManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabeling AllMinecraft PLugin this may take a a few seconds...");

        File directory = new File("./plugins/Challenges/Database");
        if (!directory.exists()){
            directory.mkdirs();
        }

        databaseManager = new ItemDatabaseManager();
        try {
            databaseManager.connectToDatabase();
            if (databaseManager.isConnected()) {
                databaseManager.createTableIfNotExists();
                databaseManager.saveAllItemsToDatabase();
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to database or save items: " + e.getMessage());
        }

        bossBarManager = new BossBarManager(this, databaseManager);

        // Events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(bossBarManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupEvent(this, databaseManager, bossBarManager), this);

        // Commands
        this.getCommand("skipitem").setExecutor(new SkipItemCommand(bossBarManager, databaseManager));

        getLogger().info("AllMinecraft Plugin has been enabled!");

    }

    @Override
    public void onDisable() {
        getLogger().info("Saving all items to database...");
        if (databaseManager != null) {
            try {
                databaseManager.disconnectFromDatabase();
            } catch (SQLException e) {
                getLogger().severe("Failed to disconnect from database: " + e.getMessage());
            }
        }

        if (bossBarManager != null) {
            bossBarManager.removeBossBar();
        }
        getLogger().info("Everything is saved and the plugin has been disabled!");
    }
}
