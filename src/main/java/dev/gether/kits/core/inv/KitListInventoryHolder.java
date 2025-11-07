
package dev.gether.kits.core.inv;

import dev.gether.getutils.inventory.AbstractInventoryHolder;
import dev.gether.getutils.inventory.InventoryConfig;
import dev.gether.getutils.utils.ColorFixer;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.getutils.utils.TimeUtil;
import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KitListInventoryHolder extends AbstractInventoryHolder<KitsPlugin> {

    public KitListInventoryHolder(KitsPlugin plugin, Player player, InventoryConfig inventoryConfig) {
        super(plugin, player, inventoryConfig);
    }

    @Override
    protected void initializeItems() {
        super.initializeItems();

        initItems();

    }

    private void initItems() {
        UUID uniqueId = player.getUniqueId();

        plugin.getDatabaseService().getUserManager().get(uniqueId.toString()).ifPresent(user -> {
            plugin.getFileManager().getKits().forEach(kit -> {

                long remainingCooldown = user.getRemainingCooldown(kit.getKey(), kit.getCooldown());
                ItemStack itemStack = remainingCooldown > 0 ? kit.getIconCooldown().getItem().getItemStack() : kit.getIcon().getItem().getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();

                List<Component> lore = itemMeta.lore();
                if(lore == null) {
                    lore = new ArrayList<>();
                }

                List<Component> updatedLore = new ArrayList<>();
                for (Component line : lore) {
                    String legacyText = LegacyComponentSerializer.legacySection().serialize(line);
                    String replaced = legacyText
                            .replace("{status}", getStatus(kit, remainingCooldown))
                            .replace("{time}", TimeUtil.formatTimeShort(kit.getCooldown()));
                    String colored = ColorFixer.addColors(replaced);
                    Component component = LegacyComponentSerializer.legacySection().deserialize(colored);
                    updatedLore.add(component);
                }

                itemMeta.lore(updatedLore);
                itemStack.setItemMeta(itemMeta);

                setItem(kit.getIcon().getSlot(), itemStack, event -> {
                    PreviewInventoryHolder previewInv = new PreviewInventoryHolder(plugin, player, kit);
                    previewInv.open();
                });

                setItem(plugin.getFileManager().getConfig().getCloseInv(), event -> {
                    player.closeInventory();
                });
            });
        });
    }

    private String getStatus(Kit kit, long remainingCooldown) {
        if(remainingCooldown > 0) {
          return TimeUtil.formatTimeShort(remainingCooldown/1000);
        }
        return player.hasPermission(kit.getPermission()) ? plugin.getFileManager().getConfig().getHasAccessStatus(): plugin.getFileManager().getConfig().getNoAccessStatus();
    }

}
