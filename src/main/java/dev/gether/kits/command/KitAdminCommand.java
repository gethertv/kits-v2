package dev.gether.kits.command;

import dev.gether.getutils.utils.MessageUtil;
import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.Kit;
import dev.gether.kits.user.User;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "akit", aliases = "adminkit")
@Permission("kit.admin")
public class KitAdminCommand {

    private final KitsPlugin plugin;


    public KitAdminCommand(KitsPlugin plugin) {
        this.plugin = plugin;
    }

    @Execute(name = "create")
    public void open(@Context CommandSender sender, @Arg("kit-name") String kitName, @Arg("slot") int slot) {
        plugin.getKitManager().createKit(kitName, slot);
        MessageUtil.sendMessage(sender, "&aSuccessfully created kit!");
    }

    @Execute(name = "delete")
    public void open(@Context CommandSender sender, @Arg("kit-name") Kit kit) {
        plugin.getKitManager().delete(kit);
        MessageUtil.sendMessage(sender, "&aSuccessfully deleted kit!");
    }

    @Execute(name = "edit")
    public void edit(@Context Player player, @Arg("kit-name") Kit kit) {
        plugin.getKitManager().edit(player, kit);
    }

    @Execute(name = "reload")
    public void reload(@Context CommandSender sender) {
        plugin.getFileManager().reload();
        MessageUtil.sendMessage(sender, "&aReload complete.");
    }

    @Execute(name = "reset")
    public void reset(@Context CommandSender sender, @Arg Player target) {
        plugin.getDatabaseService().getUserManager().get(target.getUniqueId().toString()).ifPresent(User::reset);
    }
    @Execute(name = "debug")
    public void debug(@Context CommandSender sender, @Arg Player target) {
        String uuid = target.getUniqueId().toString();

        plugin.getDatabaseService().getUserManager().get(uuid).ifPresentOrElse(user -> {
            MessageUtil.sendMessage(sender, "&e================================");
            MessageUtil.sendMessage(sender, "&fDebug &a" + target.getName());
            MessageUtil.sendMessage(sender, "&7Kits:");
            user.getReceivedKits().forEach((kit, ms) -> {
                MessageUtil.sendMessage(sender, "&7> "+kit + " -> "+ms);
            });
        }, () -> {
            MessageUtil.sendMessage(sender, "&cUser not found!");
        });
    }

    @Execute(name = "enable *")
    public void enableAll(@Context CommandSender sender) {
        plugin.getKitManager().enableAll();
        MessageUtil.sendMessage(sender, "&aEnabled all kits.");
    }
    @Execute(name = "disable *")
    public void disableAll(@Context CommandSender sender) {
        plugin.getKitManager().enableAll();
        MessageUtil.sendMessage(sender, "&cDisabled all kits.");
    }
    @Execute(name = "enable")
    public void enable(@Context CommandSender sender, @Arg("kit-name") Kit kit) {
        plugin.getKitManager().enable(kit);
        MessageUtil.sendMessage(sender, "&aEnabled the kit.");
    }
    @Execute(name = "disable")
    public void disable(@Context CommandSender sender, @Arg("kit-name") Kit kit) {
        plugin.getKitManager().enable(kit);
        MessageUtil.sendMessage(sender, "&cDisabled the kit.");
    }

}
