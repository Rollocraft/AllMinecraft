package de.rollocraft.allminecraft;

import de.rollocraft.allminecraft.Discord.Manager.DiscordBotManager;
import de.rollocraft.allminecraft.Minecraft.Commands.*;
import de.rollocraft.allminecraft.Minecraft.Database.*;
import de.rollocraft.allminecraft.Minecraft.Listener.*;
import de.rollocraft.allminecraft.Minecraft.Manager.*;
import de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop.*;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main instance;
    private TimerManager timerManager;
    private ItemDatabaseManager itemDatabaseManager;
    private BossBarManager bossBarManager;
    private TimerDatabaseManager timerDatabaseManager;
    private BackpackDatabaseManager backpackDatabaseManager;
    private Inventory sharedBackpack;
    private PositionDatabaseManager positionDatabaseManager;
    private TabListManager tabListManager;
    private AchievementDatabaseManager achievementDatabaseManager;
    private DiscordBotManager botManager;
    private MobViewerManager mobViewerManager;
    private MobDatabaseManager mobDatabaseManager;
    private BackpackManager backpackManager;
    private ItemViewerManager itemViewerManager;


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

        //Config values
        String botToken = getConfig().getString("botToken");
        String channelId = getConfig().getString("channelId");
        boolean startBot = getConfig().getBoolean("start-bot");

        //Create the directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //Start the bot
        if (startBot) {
            if (botToken == null || botToken.isEmpty()) {
                Bukkit.getLogger().warning("Bitte setze ein gültigen Bot-Token in der config.yml");
                return;
            }

            if (channelId == null || channelId.isEmpty()) {
                Bukkit.getLogger().warning("Bitte setze eine gültige Channel-ID in der config.yml");
                return;
            }
            try {
                botManager = new DiscordBotManager(botToken, channelId);
                botManager.start();
            } catch (LoginException | InterruptedException e) {
                Bukkit.getLogger().warning("Es gab ein Problem beim Starten des Bots: " + e.getMessage());
            }
        }
        //Item Database Connection
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
            Bukkit.getLogger().info("Failed to connect to database or save items: " + e.getMessage());
        }

        //Position Database Connection
        positionDatabaseManager = new PositionDatabaseManager();
        try {
            positionDatabaseManager.connectToDatabase();
            if (positionDatabaseManager.isConnected()) {
                positionDatabaseManager.createTableIfNotExists();
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to position database or create table: " + e.getMessage());
        }

        //Achievement Database Connection
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

        //Backpack Database Connection
        backpackDatabaseManager = new BackpackDatabaseManager();
        try {
            backpackDatabaseManager.connectToDatabase();
            if (backpackDatabaseManager.isConnected()) {
                backpackDatabaseManager.createBackpackTableIfNotExists();
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to backpack database or create table: " + e.getMessage());
        }

        //TimerDatabase Connection
        timerDatabaseManager = new TimerDatabaseManager(itemDatabaseManager.getConnection());
        try {
            timerDatabaseManager.createTimerTableIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            timerManager = new TimerManager(timerDatabaseManager.loadTimer());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Mob Database Connection
        mobDatabaseManager = new MobDatabaseManager();
        try {
            mobDatabaseManager.connectToDatabase();
            if (mobDatabaseManager.isConnected()) {
                mobDatabaseManager.createTableIfNotExists();
                mobDatabaseManager.saveAllMobsToDatabase();
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to achievement database or create table: " + e.getMessage());
        }

        //Managers
        backpackManager = new BackpackManager(backpackDatabaseManager);
        mobViewerManager = new MobViewerManager(mobDatabaseManager);
        bossBarManager = new BossBarManager(this, itemDatabaseManager);
        tabListManager = new TabListManager(this, itemDatabaseManager, achievementDatabaseManager,mobDatabaseManager);
        itemViewerManager = new ItemViewerManager(itemDatabaseManager);

        // Events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(bossBarManager, tabListManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupListener(this, itemDatabaseManager, bossBarManager, tabListManager, timerManager), this);
        getServer().getPluginManager().registerEvents(new InventoryInteractListener(this,itemDatabaseManager, bossBarManager, tabListManager, timerManager, mobViewerManager, itemViewerManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerAdvancementDoneListener(this, achievementDatabaseManager, tabListManager), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(mobDatabaseManager,tabListManager), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(timerManager), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(timerManager), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(timerManager), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(timerManager), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(timerManager), this);
        getServer().getPluginManager().registerEvents(new WeatherChangeListener(timerManager), this);
        getServer().getPluginManager().registerEvents(new SingChangeListener(itemDatabaseManager, itemViewerManager), this);

        // Commands
        TimerCommand timerCommand = new TimerCommand(timerDatabaseManager);
        this.getCommand("timer").setExecutor(timerCommand);
        this.getCommand("timer").setTabCompleter(timerCommand);
        this.getCommand("skipitem").setExecutor(new SkipItemCommand(bossBarManager, itemDatabaseManager, tabListManager));
        this.getCommand("backpack").setExecutor(new BackpackCommand());
        this.getCommand("position").setExecutor(new PositionCommand(positionDatabaseManager));
        this.getCommand("position").setTabCompleter(new PositionCommand(positionDatabaseManager));
        this.getCommand("mobviewer").setExecutor(new MobViewerCommand(mobViewerManager));
        this.getCommand("save-allminecraft").setExecutor(new SaveAllCommand(backpackManager));
        this.getCommand("itemviewer").setExecutor(new ItemViewerCommand(itemViewerManager));


        // Load the shared backpack
        if (backpackManager.loadBackpack() != null) {
            sharedBackpack = backpackManager.loadBackpack();
        }

        getLogger().info("AllMinecraft Plugin has been enabled!");

    }

    @Override
    public void onDisable() {
        getLogger().info("Saving all items to database...");
        if (timerDatabaseManager != null) {
            try {
                timerDatabaseManager.saveTimer(timerManager);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

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

        if (mobDatabaseManager != null) {
            try {
                mobDatabaseManager.disconnectFromDatabase();
            } catch (SQLException e) {
                getLogger().severe("Failed to disconnect from mob database: " + e.getMessage());
            }
        }

        if (backpackDatabaseManager != null) {
            try {
                backpackManager.saveBackpack(sharedBackpack);
                backpackDatabaseManager.disconnectFromDatabase();
            } catch (SQLException e) {
                getLogger().severe("Failed to disconnect from backpack database: " + e.getMessage());
            }
        }

        if (bossBarManager != null) {
            bossBarManager.removeBossBar();
        }

        if (botManager != null) {
            botManager.stop();
        }
        getLogger().info("Everything is saved and the plugin has been disabled!");
    }

    public static Main getInstance() {
        return instance;
    }


    public TimerManager getTimer() {
        return timerManager;
    }


    public Inventory getSharedBackpack() {
        return sharedBackpack;
    }
}
