package dev.gether.kits.command;

import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.inv.KitListInventoryHolder;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

@Command(name = "kit", aliases = {"zestaw", "zestawy"})
@Permission("kit.use")
public class KitCommand {

    private final KitsPlugin plugin;


    public KitCommand(KitsPlugin plugin) {
        this.plugin = plugin;
    }

    @Execute()
    public void open(@Context Player player) {
        KitListInventoryHolder kitsInv = new KitListInventoryHolder(plugin, player, plugin.getFileManager().getConfig().getKitListInv());
        kitsInv.open();
    }

}
