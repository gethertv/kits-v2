package dev.gether.kits.file;

import dev.gether.database.config.DatabaseConfig;
import dev.gether.getutils.ConfigManager;
import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.Kit;
import dev.gether.kits.file.config.Config;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import lombok.Getter;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
public class FileManager {
    private File FILE_PATH_ALCHEMY = new File(KitsPlugin.getInstance().getDataFolder() + "/kits/");


    Config config = ConfigManager.create(Config.class, it -> {
        it.setFile(new File(KitsPlugin.getInstance().getDataFolder(), "config.yml"));
        it.load();
    });

    DatabaseConfig databaseConfig = ConfigManager.create(DatabaseConfig.class, it -> {
        it.setFile(new File(KitsPlugin.getInstance().getDataFolder(), "database.yml"));
        it.getSqlite().setPath(KitsPlugin.getInstance().getDataFolder() + "/database.yml");
        it.load();
    });

    private Set<Kit> kits = new HashSet<>();

    public FileManager() {
        initialize();
    }

    public void initialize() {
        kits.clear();
        File[] files = FILE_PATH_ALCHEMY.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            String kitFileName = file.getName();
            Kit kit = new Kit();
            kit.setFile(file);
            kit.load();

            kits.add(kit);

            MessageUtil.logMessage(ConsoleColor.GREEN, "[kits] Loaded kit " + kitFileName);
        }
    }

    public void addKit(Kit kit) {
        kit.setFile(new File(FILE_PATH_ALCHEMY.getPath() + "/", kit.getKey().toLowerCase() + ".yml"));
        kit.load();

        kits.add(kit);
    }

    public Optional<Kit> findKit(String argument) {
        return kits.stream().filter(kit -> kit.getKey().equalsIgnoreCase(argument)).findFirst();
    }

    public SuggestionResult getAllNameSuggestionOfKit() {
        return kits.stream().map(Kit::getKey).collect(SuggestionResult.collector());
    }

    public void reload() {
        config.load();
        initialize();
    }
}
