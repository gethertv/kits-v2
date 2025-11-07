package dev.gether.kits.core;

import dev.gether.getutils.GetConfig;
import dev.gether.getutils.inventory.InventoryConfig;
import dev.gether.getutils.inventory.item.StaticItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Kit extends GetConfig {
    private String key;
    private boolean enabled;
    private String name;
    private StaticItem icon;
    private StaticItem iconCooldown;

    private int cooldown;

    private InventoryConfig inventoryConfig;

    private int slotPreviousPage;
    private int slotReceivedKit;

    private Map<Integer, ItemStack> items = new HashMap<>();

    public String getPermission() {
        return "kit."+key.toLowerCase();
    }
}
