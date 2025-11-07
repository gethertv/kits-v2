package dev.gether.kits.core;

import dev.gether.getutils.inventory.InventoryConfig;
import dev.gether.getutils.inventory.item.DynamicItem;
import dev.gether.getutils.inventory.item.StaticItem;
import dev.gether.getutils.models.Item;
import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.inv.AdminEditInventoryHolder;
import dev.gether.kits.file.FileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KitManager {

    private final KitsPlugin plugin;

    public KitManager(KitsPlugin plugin) {
        this.plugin = plugin;
    }
    public void createKit(String kitName, int slot) {
        Kit kit = new Kit(
                kitName,
                false,
                "&fkit " + kitName,
                StaticItem.builder()
                        .enabled(true)
                        .item(Item.builder()
                                .material(Material.IRON_HELMET)
                                .name("&fKit " + kitName)
                                .lore(new ArrayList<>(List.of(
                                        " ",
                                        "&7Zestaw możesz odebrać co: {time}",
                                        "&7Możesz odebrać: {status}",
                                        " ",
                                        "&7Kliknij &ftutaj, &7aby przejść do zestawien",
                                        " "
                                )))
                                .build())
                        .slot(slot)
                        .build(),
                StaticItem.builder()
                        .enabled(true)
                        .item(Item.builder()
                                .material(Material.IRON_HELMET)
                                .name("&fKit " + kitName)
                                .lore(new ArrayList<>(List.of(
                                        " ",
                                        "&7Zestaw możesz odebrać co: {time}",
                                        "&7Możesz odebrać za: &a{status}",
                                        " ",
                                        "&7Kliknij &ftutaj, &7aby przejść do zestawu",
                                        " "
                                )))
                                .build())
                        .slot(slot)
                        .build(),
                3600,
                InventoryConfig.builder()
                        .size(45)
                        .title("&0Zestawy "+kitName)
                        .cancelClicks(true)
                        .refreshInterval(-1)
                        .decorations(new ArrayList<>(List.of(
                                DynamicItem.builder()
                                        .enabled(true)
                                        .item(Item.builder()
                                                .material(Material.BLACK_STAINED_GLASS_PANE)
                                                .name(" ")
                                                .build())
                                        .slots(new ArrayList<>(List.of(
                                                0,1,2,3,4,5,6,7,8,
                                                9,10,11,12,13,14,15,16,17,
                                                18,19,20,21,22,23,24,25,26,
                                                27,28,29,30,31,32,33,34,35,
                                                36,37,38,39,40,41,42,43,44
                                        )))
                                        .build()
                        )))
                        .build(),
                44,
                43,
                new HashMap<>()
        );
        plugin.getFileManager().addKit(kit);
    }

    public void delete(Kit kit) {
        plugin.getFileManager().getKits().remove(kit);
        kit.getFile().delete();
    }

    public void enableAll() {
        plugin.getFileManager().getKits().forEach(this::enable);
    }
    public void enable(Kit kit) {
        kit.setEnabled(true);
        kit.save();
    }
    public void disableAll() {
        plugin.getFileManager().getKits().forEach(this::disable);
    }
    public void disable(Kit kit) {
        kit.setEnabled(false);
        kit.save();
    }

    public void edit(Player player, Kit kit) {
        AdminEditInventoryHolder adminInv = new AdminEditInventoryHolder(plugin, player, kit);
        adminInv.open();
    }
}
