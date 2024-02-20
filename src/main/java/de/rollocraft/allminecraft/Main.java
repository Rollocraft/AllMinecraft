package de.rollocraft.allminecraft;

import de.rollocraft.allminecraft.Minecraft.Commands.BackpackCommand;
import de.rollocraft.allminecraft.Minecraft.Commands.PositionCommand;
import de.rollocraft.allminecraft.Minecraft.Commands.SkipItemCommand;
import de.rollocraft.allminecraft.Minecraft.Commands.TimerCommand;
import de.rollocraft.allminecraft.Minecraft.Database.*;
import de.rollocraft.allminecraft.Minecraft.Listener.*;
import de.rollocraft.allminecraft.Minecraft.Backpack;
import de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop.*;
import de.rollocraft.allminecraft.Minecraft.Manager.BackpackManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;

import de.rollocraft.allminecraft.Minecraft.Timer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main instance;
    private Timer timer;
    private BackpackManager backpackManager;
    private ItemDatabaseManager itemDatabaseManager;
    private BossBarManager bossBarManager;
    private TimerDatabaseManager timerDatabaseManager;
    private BackpackDatabaseManager backpackDatabaseManager;
    private Backpack sharedBackpack;
    private PositionDatabaseManager positionDatabaseManager;
    private TabListManager tabListManager;
    private AchievementDatabaseManager achievementDatabaseManager;

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
        /*
        String botToken = getConfig().getString("botToken");
        String channelId = getConfig().getString("channelId");
         */
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Database
        itemDatabaseManager = new ItemDatabaseManager();
        try {
            itemDatabaseManager.connectToDatabase();
            if (itemDatabaseManager.isConnected()) {
                itemDatabaseManager.createTableIfNotExists();
                itemDatabaseManager.saveAllItemsToDatabase();
                if (itemDatabaseManager.getCurrentItem() == null) {
                    itemDatabaseManager.setCurrentItem(itemDatabaseManager.getRandomItem());
                }
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

        achievementDatabaseManager = new AchievementDatabaseManager();
        try {
            achievementDatabaseManager.connectToDatabase();
            if (achievementDatabaseManager.isConnected()) {
                achievementDatabaseManager.createTableIfNotExists();
                achievementDatabaseManager.saveAllAchievementsToDatabase();

            }
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to achievement database or create table: " + e.getMessage());
        }

        try {
            backpackDatabaseManager = new BackpackDatabaseManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sharedBackpack = loadBackpack();

        // Register BossBar
        bossBarManager = new BossBarManager(this, itemDatabaseManager);

        timerDatabaseManager = new TimerDatabaseManager(itemDatabaseManager.getConnection());
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
        tabListManager = new TabListManager(this, itemDatabaseManager, achievementDatabaseManager);

        // Events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(bossBarManager, tabListManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupListener(this, itemDatabaseManager, bossBarManager, tabListManager, timer), this);
        getServer().getPluginManager().registerEvents(new InventoryInteractListener(this, itemDatabaseManager, bossBarManager, tabListManager, timer), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerAdvancementDoneListener(this, achievementDatabaseManager, tabListManager), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(timer), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(timer), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(timer), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(timer), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(timer), this);

        // Commands
        TimerCommand timerCommand = new TimerCommand(timerDatabaseManager);
        this.getCommand("timer").setExecutor(timerCommand);
        this.getCommand("timer").setTabCompleter(timerCommand);
        this.getCommand("skipitem").setExecutor(new SkipItemCommand(bossBarManager, itemDatabaseManager, tabListManager));
        this.getCommand("backpack").setExecutor(new BackpackCommand());
        this.getCommand("position").setExecutor(new PositionCommand(positionDatabaseManager));
        this.getCommand("position").setTabCompleter(new PositionCommand(positionDatabaseManager));

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

        if (itemDatabaseManager != null) {
            try {
                itemDatabaseManager.disconnectFromDatabase();
            } catch (SQLException e) {
                getLogger().severe("Failed to disconnect from database: " + e.getMessage());
            }
        }

        if (achievementDatabaseManager != null) {
            try {
                achievementDatabaseManager.disconnectFromDatabase();
            } catch (SQLException e) {
                getLogger().severe("Failed to disconnect from achievement database: " + e.getMessage());
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

        getLogger().info("Everything is saved and the plugin has been disabled!");
    }

    public static Main getInstance() {
        return instance;
    }


    public Timer getTimer() {
        return timer;
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
