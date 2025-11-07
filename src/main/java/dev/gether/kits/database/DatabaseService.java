package dev.gether.kits.database;

import dev.gether.database.config.DatabaseConfig;
import dev.gether.database.factory.DatabaseFactory;
import dev.gether.database.iinterface.DatabaseEntity;
import dev.gether.database.iinterface.IDatabase;
import dev.gether.database.manager.DatabaseManager;
import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.kits.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private final List<IDatabase<?>> databases = new ArrayList<>();
    private final DatabaseConfig config;
    private String prefix;

    // manager
    @Getter private DatabaseManager<User> userManager;

    public DatabaseService(DatabaseConfig config) {
        this.config = config;
        this.prefix = "kits";
    }

    public void initialize() {
        MessageUtil.logMessage(ConsoleColor.YELLOW, "["+prefix+"] Initializing databases...");
        // User
        userManager = createManager(
                "kits_users",
                User.class,
                id -> User.builder().id(id).build()
        );
        MessageUtil.logMessage(ConsoleColor.GREEN, "["+prefix+"] All databases initialized!");
    }

    private <T extends DatabaseEntity> DatabaseManager<T> createManager(
            String collectionName,
            Class<T> entityClass,
            java.util.function.Function<String, T> factory) {

        // create database
        IDatabase<T> database = DatabaseFactory.createDatabase(
                config,
                collectionName,
                entityClass
        );

        database.connect();

        databases.add(database);

        DatabaseManager<T> manager = new DatabaseManager<>(
                database,
                factory,
                config.getAutoSaveInterval()
        );

        return manager;
    }

    public void saveAll() {
        MessageUtil.logMessage(ConsoleColor.GREEN, "["+prefix+"] Saving all data...");

        int totalSaved = 0;

        if (userManager != null) {
            int saved = userManager.saveAllSync();
            totalSaved += saved;
        }

        MessageUtil.logMessage(ConsoleColor.GREEN, "["+prefix+"] Total saved: " + totalSaved);
    }

    public void clearAllCaches() {
        MessageUtil.logMessage(ConsoleColor.GREEN, "["+prefix+"] Clearing all caches...");

        if (userManager != null) userManager.clearCache();

        MessageUtil.logMessage(ConsoleColor.GREEN, "["+prefix+"] All caches cleared");
    }

    public void shutdown() {
        MessageUtil.logMessage(ConsoleColor.YELLOW, "["+prefix+"] Shutdown...");
        saveAll();

        clearAllCaches();

        MessageUtil.logMessage(ConsoleColor.YELLOW, "["+prefix+"] Disconnecting " + databases.size() + " databases...");
        for (IDatabase<?> database : databases) {
            if (database.isConnected()) {
                database.disconnect();
            }
        }

        MessageUtil.logMessage(ConsoleColor.YELLOW, "["+prefix+"] All databases disconnected");

        databases.clear();
    }
}