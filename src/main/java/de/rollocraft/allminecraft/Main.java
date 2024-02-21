package de.rollocraft.allminecraft;

import de.rollocraft.allminecraft.Discord.Manager.DiscordBotManager;
import de.rollocraft.allminecraft.Minecraft.Commands.*;
import de.rollocraft.allminecraft.Minecraft.Database.*;
import de.rollocraft.allminecraft.Minecraft.Listener.*;
import de.rollocraft.allminecraft.Minecraft.Backpack;
import de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop.*;
import de.rollocraft.allminecraft.Minecraft.Manager.BackpackManager;
import de.rollocraft.allminecraft.Minecraft.Manager.MobViewerManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;

import de.rollocraft.allminecraft.Minecraft.Timer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
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
    private DiscordBotManager botManager;
    private MobViewerManager mobViewerManager;
    private MobDatabaseManager mobDatabaseManager;

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
        try {
            backpackDatabaseManager = new BackpackDatabaseManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sharedBackpack = loadBackpack();

        //TimerDatabase Connection
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
        backpackManager = new BackpackManager();
        mobViewerManager = new MobViewerManager(mobDatabaseManager);
        bossBarManager = new BossBarManager(this, itemDatabaseManager);
        tabListManager = new TabListManager(this, itemDatabaseManager, achievementDatabaseManager,mobDatabaseManager);

        // Events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(bossBarManager, tabListManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupListener(this, itemDatabaseManager, bossBarManager, tabListManager, timer), this);
        getServer().getPluginManager().registerEvents(new InventoryInteractListener(this,itemDatabaseManager, bossBarManager, tabListManager, timer, mobViewerManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerAdvancementDoneListener(this, achievementDatabaseManager, tabListManager), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(timer), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(timer), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(timer), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(timer), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(timer), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(mobDatabaseManager,tabListManager), this);

        // Commands
        TimerCommand timerCommand = new TimerCommand(timerDatabaseManager);
        this.getCommand("timer").setExecutor(timerCommand);
        this.getCommand("timer").setTabCompleter(timerCommand);
        this.getCommand("skipitem").setExecutor(new SkipItemCommand(bossBarManager, itemDatabaseManager, tabListManager));
        this.getCommand("backpack").setExecutor(new BackpackCommand());
        this.getCommand("position").setExecutor(new PositionCommand(positionDatabaseManager));
        this.getCommand("position").setTabCompleter(new PositionCommand(positionDatabaseManager));
        this.getCommand("mobviewer").setExecutor(new MobViewerCommand(mobViewerManager));



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

        if (botManager != null) {
            botManager.stop();
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
