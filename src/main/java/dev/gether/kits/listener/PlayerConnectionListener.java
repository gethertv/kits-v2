package dev.gether.kits.listener;

import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.kits.KitsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerConnectionListener implements Listener {

    private final KitsPlugin plugin;

    private final Map<UUID, BukkitTask> cleanupTasks = new HashMap<>();

    public PlayerConnectionListener(KitsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String uuidStr = uuid.toString();

        BukkitTask existingTask = cleanupTasks.remove(uuid);
        if (existingTask != null) {
            existingTask.cancel();
        }

        plugin.getDatabaseService().getUserManager().loadAsync(uuidStr).thenAccept(user -> {
            user.setName(player.getName());
            MessageUtil.logMessage(ConsoleColor.GREEN, "[kits] loaded user: " + player.getName());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String uuidStr = uuid.toString();

        plugin.getDatabaseService().getUserManager().saveAsync(uuidStr).thenRun(() -> {
            MessageUtil.logMessage(ConsoleColor.GREEN, "[kits] saved user: " + player.getName());
        });

        BukkitTask oldTask = cleanupTasks.remove(uuid);
        if (oldTask != null) {
            oldTask.cancel();
        }

        BukkitTask cleanupTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (!player.isOnline()) {
                plugin.getDatabaseService().getUserManager().removeFromCache(uuidStr);
                cleanupTasks.remove(uuid);
                MessageUtil.logMessage(ConsoleColor.GREEN, "[kits] removed user from cache: " + player.getName());
            }
        }, 6000L);

        cleanupTasks.put(uuid, cleanupTask);
    }


    public void cleanup() {
        for (BukkitTask task : cleanupTasks.values()) {
            task.cancel();
        }
        cleanupTasks.clear();
        plugin.getLogger().info("Cancelled " + cleanupTasks.size() + " cleanup tasks");
    }
}