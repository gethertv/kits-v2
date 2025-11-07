
package dev.gether.kits.core.inv;

import dev.gether.getutils.inventory.AbstractInventoryHolder;
import dev.gether.getutils.utils.ColorFixer;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.getutils.utils.TimeUtil;
import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.Kit;
import dev.gether.kits.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PreviewInventoryHolder extends AbstractInventoryHolder<KitsPlugin> {

    private final Kit kit;

    public PreviewInventoryHolder(KitsPlugin plugin, Player player, Kit kit) {
        super(plugin, player, kit.getInventoryConfig());
        this.kit = kit;
    }

    @Override
    protected void initializeItems() {
        super.initializeItems();

        initItems();
    }


    private void initItems() {
        UUID uniqueId = player.getUniqueId();

        plugin.getDatabaseService().getUserManager().get(uniqueId.toString()).ifPresent(user -> {

            boolean hasPermission = player.hasPermission(kit.getPermission());
            long remainingCooldown = user.getRemainingCooldown(kit.getKey(), kit.getCooldown());

            setItemKits(kit);

            setItem(kit.getSlotPreviousPage(), plugin.getFileManager().getConfig().getPreviousMenu().getItemStack(), event -> {
                KitListInventoryHolder kitsInv = new KitListInventoryHolder(plugin, player, plugin.getFileManager().getConfig().getKitListInv());
                kitsInv.open();
            });

            if (!hasPermission) {
                setItem(kit.getSlotReceivedKit(), plugin.getFileManager().getConfig().getNoAccessItem().getItemStack(), event -> {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(player, plugin.getFileManager().getConfig().getNoPermission());
                    player.closeInventory();
                });
            } else if (remainingCooldown <= 0) {
                ItemStack itemStack = plugin.getFileManager().getConfig().getHasAccessItem().getItemStack();
                setItem(kit.getSlotReceivedKit(), itemStack, event -> {
                    event.setCancelled(true);
                    receivedKit(user, kit);
                });
            } else {
                ItemStack itemStack = plugin.getFileManager().getConfig().getCooldownItem().getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();

                List<Component> lore = itemMeta.lore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                List<Component> updatedLore = new ArrayList<>();
                for (Component line : lore) {
                    String legacyText = LegacyComponentSerializer.legacySection().serialize(line);
                    String replaced = legacyText
                            .replace("{time}", TimeUtil.formatTimeShort(remainingCooldown / 1000));
                    String colored = ColorFixer.addColors(replaced);
                    Component component = LegacyComponentSerializer.legacySection().deserialize(colored);
                    updatedLore.add(component);
                }

                itemMeta.lore(updatedLore);
                itemStack.setItemMeta(itemMeta);

                setItem(kit.getSlotReceivedKit(), itemStack, event -> {
                    event.setCancelled(true);
                    refresh();
                    MessageUtil.sendMessage(player,
                            plugin.getFileManager().getConfig().getCooldownMessage()
                                    .replace("{time}", TimeUtil.formatTimeShort(remainingCooldown / 1000))
                    );
                });
            }
        });
    }

    private void receivedKit(User user, Kit kit) {
        user.claimKit(player, kit);
    }
    private void setItemKits(Kit kit) {
        kit.getItems().forEach(this::setItem);
    }
}
