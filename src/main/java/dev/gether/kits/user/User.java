package dev.gether.kits.user;

import dev.gether.database.iinterface.DatabaseEntity;
import dev.gether.getutils.annotation.YamlIgnore;
import dev.gether.getutils.utils.ColorFixer;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.getutils.utils.PlayerUtil;
import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.Kit;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements DatabaseEntity {
    private String id;
    private String name;
    private Map<String, Long> receivedKits;

    @YamlIgnore
    public boolean canClaimKit(String kitId, int cooldown) {
        if (receivedKits == null || !receivedKits.containsKey(kitId)) {
            return true;
        }
        long lastClaim = receivedKits.get(kitId);
        long diff = System.currentTimeMillis() - 1000L * cooldown - lastClaim;
        return diff >= 0;
    }

    public void claimKit(String kitId) {
        if (receivedKits == null) {
            receivedKits = new HashMap<>();
        }
        receivedKits.put(kitId, System.currentTimeMillis());
    }

    public long getRemainingCooldown(String kitId, int cooldown) {
        if (receivedKits == null || !receivedKits.containsKey(kitId)) {
            return 0;
        }

        long lastClaim = receivedKits.get(kitId);
        long next = System.currentTimeMillis() - (1000L * cooldown);
        return lastClaim - next;
    }

    public void claimKit(Player player, Kit kit) {
        if(!kit.isEnabled()) {
            MessageUtil.sendMessage(player, KitsPlugin.getInstance().getFileManager().getConfig().getKitDisabled());
            player.closeInventory();
            return;
        }
        claimKit(kit.getKey());
        if(KitsPlugin.getInstance().getFileManager().getConfig().isTakeItemFromInv()) {
            Inventory inventory = Bukkit.createInventory(player, getSizeInv(kit.getItems().size()), ColorFixer.addColors(kit.getInventoryConfig().getTitle()));
            kit.getItems().values().forEach(itemStack -> {
                inventory.addItem(itemStack.clone());
            });
            player.openInventory(inventory);
        } else {
            kit.getItems().values().forEach(itemStack -> {
                PlayerUtil.addItems(player, false, itemStack.clone());
            });
            player.closeInventory();
        }

    }

    private int getSizeInv(int size) {
        if(size < 9) {
            return 9;
        }
        if(size < 18) {
            return 18;
        }
        if(size < 27) {
            return 27;
        }
        if(size < 36) {
            return 36;
        }
        if(size < 45) {
            return 45;
        }
        return 54;
    }

    public void reset() {
        receivedKits.clear();
    }
}
