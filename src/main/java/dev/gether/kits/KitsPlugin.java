package dev.gether.kits;

import dev.gether.getutils.inventory.GetInventory;
import dev.gether.kits.command.KitAdminCommand;
import dev.gether.kits.command.KitCommand;
import dev.gether.kits.command.arg.KitArg;
import dev.gether.kits.core.Kit;
import dev.gether.kits.core.KitManager;
import dev.gether.kits.database.DatabaseService;
import dev.gether.kits.file.FileManager;
import dev.gether.kits.listener.PlayerConnectionListener;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class KitsPlugin extends JavaPlugin {

    @Getter
    private static KitsPlugin instance;

    @Getter private FileManager fileManager;
    private LiteCommands<CommandSender> liteCommands;

    @Getter private DatabaseService databaseService;

    private PlayerConnectionListener connectionListener;

    @Getter private KitManager kitManager;

    @Override
    public void onLoad() {
        instance = this;
        fileManager = new FileManager();
    }

    @Override
    public void onEnable() {

        databaseService = new DatabaseService(fileManager.getDatabaseConfig());
        databaseService.initialize();

        // manager
        kitManager = new KitManager(this);

        // getutils - inv
        GetInventory.initialize(this);

        // listeners
        connectionListener = new PlayerConnectionListener(this);
        getServer().getPluginManager().registerEvents(connectionListener, this);

        // load online users
        Bukkit.getOnlinePlayers().forEach(player -> {
            databaseService.getUserManager().loadAsync(player.getUniqueId().toString()).thenAccept(user -> {
                user.setName(player.getName());
            });
        });

        // command
        registerCommand();

    }

    @Override
    public void onDisable() {
        if (connectionListener != null) {
            connectionListener.cleanup();
        }

        if (databaseService != null) {
            databaseService.shutdown();
        }

        GetInventory.cleanup();

        if(liteCommands != null) {
            liteCommands.unregister();
        }

        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        databaseService = null;
        fileManager = null;
        connectionListener = null;
        instance = null;
        liteCommands = null;
    }


    private void registerCommand() {
        this.liteCommands = LiteBukkitFactory.builder("kits", this)
                .commands(
                        new KitCommand(this),
                        new KitAdminCommand(this)
                )
                .argument(Kit.class, new KitArg(this))
                .build();
    }
}
