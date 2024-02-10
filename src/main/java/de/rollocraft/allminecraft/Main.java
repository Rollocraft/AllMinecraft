package de.rollocraft.allminecraft;

import de.rollocraft.allminecraft.Commands.BackpackCommand;
import de.rollocraft.allminecraft.Commands.SkipItemCommand;
import de.rollocraft.allminecraft.Commands.TimerCommand;
import de.rollocraft.allminecraft.Events.PlayerJoinListener;
import de.rollocraft.allminecraft.Events.PlayerPickupEvent;
import de.rollocraft.allminecraft.Manager.Backpack;
import de.rollocraft.allminecraft.Manager.BackpackManager;
import de.rollocraft.allminecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Manager.Database.BackpackDatabaseManager;
import de.rollocraft.allminecraft.Manager.Database.ItemDatabaseManager;
import de.rollocraft.allminecraft.Manager.Database.TimerDatabaseManager;

import de.rollocraft.allminecraft.Manager.Timer;
import de.rollocraft.allminecraft.utils.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main instance;
    private Timer timer;
    private Config config;
    private BackpackManager backpackManager;
    private ItemDatabaseManager databaseManager;
    private BossBarManager bossBarManager;
    private TimerDatabaseManager timerDatabaseManager;
    private BackpackDatabaseManager backpackDatabaseManager;
    private Backpack sharedBackpack;

    @Override
    public void onLoad() {
        getLogger().info("Loading AllMinecraft Plugin...");
        instance = this;
        config = new Config();
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabling AllMinecraft Plugin this may take a a few seconds...");

        //Create the database directory if it doesn't exist
        File directory = new File("./plugins/Challenges/Database");
        if (!directory.exists()){
            directory.mkdirs();
        }

        // Database
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

        backpackDatabaseManager = new BackpackDatabaseManager("backpacks.db");
        sharedBackpack = loadBackpack();

        // Register BossBar
        bossBarManager = new BossBarManager(this, databaseManager);

        timerDatabaseManager = new TimerDatabaseManager(databaseManager.getConnection());
        try {
            timerDatabaseManager.createTimerTableIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            timer = new Timer(timerDatabaseManager.loadTimer());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        backpackManager = new BackpackManager();

        // Events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(bossBarManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupEvent(this, databaseManager, bossBarManager), this);

        // Commands
        TimerCommand timerCommand = new TimerCommand(timerDatabaseManager);
        this.getCommand("timer").setExecutor(timerCommand);
        this.getCommand("timer").setTabCompleter(timerCommand);
        this.getCommand("skipitem").setExecutor(new SkipItemCommand(bossBarManager, databaseManager));
        this.getCommand("backpack").setExecutor(new BackpackCommand());

        getLogger().info("AllMinecraft Plugin has been enabled!");

    }

    @Override
    public void onDisable() {
        saveBackpack();
        try {
            timerDatabaseManager.saveTimer(timer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        backpackManager.save();
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

    public static Main getInstance() {
        return instance;
    }

    public Config getConfiguration() {
        return config;
    }

    public Timer getTimer() {
        return timer;
    }

    public BackpackManager getBackpackManager() {
        return backpackManager;
    }
    public void saveBackpack() {
        backpackDatabaseManager.saveBackpack(sharedBackpack);
    }
    public Backpack loadBackpack() {
        Backpack backpack = backpackDatabaseManager.loadBackpack();
        if (backpack == null) {
            backpack = new Backpack(); // Assuming Backpack has a default constructor
        }
        return backpack;
    }
    public Backpack getSharedBackpack() {
        return sharedBackpack;
    }
}
