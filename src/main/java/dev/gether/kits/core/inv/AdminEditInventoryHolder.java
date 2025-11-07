
package dev.gether.kits.core.inv;

import dev.gether.getutils.inventory.AbstractInventoryHolder;
import dev.gether.getutils.inventory.InventoryConfig;
import dev.gether.getutils.utils.TimeUtil;
import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdminEditInventoryHolder extends AbstractInventoryHolder<KitsPlugin> {

    private final Kit kit;
    public AdminEditInventoryHolder(KitsPlugin plugin, Player player, Kit kit) {
        super(plugin, player, InventoryConfig.builder()
                .size(kit.getInventoryConfig().getSize())
                .cancelClicks(false)
                .title("Edit - "+kit.getKey())
                .refreshInterval(-1)
                .build());
        this.kit = kit;
    }

    @Override
    protected void initializeItems() {
        super.initializeItems();

        initItems();

    }

    private void initItems() {
        kit.getItems().forEach((slot, itemstack) -> {
            inventory.setItem(slot, itemstack);
        });
    }

    @Override
    public void close() {
        kit.getItems().clear();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if(item==null || item.getType()== Material.AIR) continue;

            kit.getItems().put(i, item.clone());
        }
        kit.save();
        super.close();
    }
}
