package de.rollocraft.allminecraft;

import de.rollocraft.allminecraft.Discord.Manager.DiscordBotManager;
import de.rollocraft.allminecraft.Minecraft.Commands.BackpackCommand;
import de.rollocraft.allminecraft.Minecraft.Commands.PositionCommand;
import de.rollocraft.allminecraft.Minecraft.Commands.SkipItemCommand;
import de.rollocraft.allminecraft.Minecraft.Commands.TimerCommand;
import de.rollocraft.allminecraft.Minecraft.Listener.InventoryInteractListener;
import de.rollocraft.allminecraft.Minecraft.Listener.PlayerJoinListener;
import de.rollocraft.allminecraft.Minecraft.Listener.PlayerPickupListener;
import de.rollocraft.allminecraft.Minecraft.Listener.PlayerQuitListener;
import de.rollocraft.allminecraft.Minecraft.Manager.Backpack;
import de.rollocraft.allminecraft.Minecraft.Manager.BackpackManager;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Minecraft.Manager.Database.BackpackDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.Database.ItemDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.Database.PositionDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.Database.TimerDatabaseManager;

import de.rollocraft.allminecraft.Minecraft.Manager.Timer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main instance;
    private Timer timer;
    private BackpackManager backpackManager;
    private ItemDatabaseManager databaseManager;
    private BossBarManager bossBarManager;
    private TimerDatabaseManager timerDatabaseManager;
    private BackpackDatabaseManager backpackDatabaseManager;
    private Backpack sharedBackpack;
    private PositionDatabaseManager positionDatabaseManager;
    private DiscordBotManager discordBotManager;

    @Override
    public void onLoad() {
        getLogger().info("Loading AllMinecraft Plugin...");
        instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabling AllMinecraft Plugin this may take a a few seconds...");

        //Create the database directory if it doesn't exist
        File directory = new File("./plugins/AllMinecraft/Database");
        saveDefaultConfig();
        String botToken = getConfig().getString("botToken");
        String channelId = getConfig().getString("channelId");
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

        positionDatabaseManager = new PositionDatabaseManager();
        try {
            positionDatabaseManager.connectToDatabase();
            if (positionDatabaseManager.isConnected()) {
                positionDatabaseManager.createTableIfNotExists();
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to position database or create table: " + e.getMessage());
        }

        try {
            backpackDatabaseManager = new BackpackDatabaseManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        getServer().getPluginManager().registerEvents(new PlayerPickupListener(this, databaseManager, bossBarManager), this);
        getServer().getPluginManager().registerEvents(new InventoryInteractListener(this, databaseManager, bossBarManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);

        // Commands
        TimerCommand timerCommand = new TimerCommand(timerDatabaseManager);
        this.getCommand("timer").setExecutor(timerCommand);
        this.getCommand("timer").setTabCompleter(timerCommand);
        this.getCommand("skipitem").setExecutor(new SkipItemCommand(bossBarManager, databaseManager));
        this.getCommand("backpack").setExecutor(new BackpackCommand());
        this.getCommand("position").setExecutor(new PositionCommand(positionDatabaseManager));
        this.getCommand("position").setTabCompleter(new PositionCommand(positionDatabaseManager));

        discordBotManager = DiscordBotManager.start(botToken, channelId, "on");

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

        if (positionDatabaseManager != null) {
            try {
                positionDatabaseManager.disconnectFromDatabase();
            } catch (SQLException e) {
                getLogger().severe("Failed to disconnect from position database: " + e.getMessage());
            }
        }

        if (bossBarManager != null) {
            bossBarManager.removeBossBar();
        }

        if (discordBotManager != null) {
            discordBotManager.shutdown();
        }
        getLogger().info("Everything is saved and the plugin has been disabled!");
    }

    public static Main getInstance() {
        return instance;
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
