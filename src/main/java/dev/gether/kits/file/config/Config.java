package dev.gether.kits.file.config;

import dev.gether.getutils.GetConfig;
import dev.gether.getutils.inventory.InventoryConfig;
import dev.gether.getutils.inventory.item.DynamicItem;
import dev.gether.getutils.inventory.item.StaticItem;
import dev.gether.getutils.models.Item;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Config extends GetConfig {

    private String hasAccessStatus = "&aTAK";
    private String noAccessStatus = "&cNIE";

    private String noPermission = "&cNie masz uprawnień do tego zestawu!";
    private String cooldownMessage = "&cMusisz poczekać jeszcze: &f{time}";
    private String kitDisabled = "&cAktualnie zestaw jest wyłączony!";

    private StaticItem closeInv = StaticItem.builder()
            .enabled(true)
            .item(Item.builder()
                    .material(Material.BARRIER)
                    .name("<#6ee7ff>Wyjdź</#00829c>")
                    .lore(new ArrayList<>(List.of(
                    )))
                    .glow(true)
                    .build())
            .slot(40)
            .build();

    private Item previousMenu = Item.builder()
            .material(Material.BARRIER)
            .name("<#6ee7ff>Powrót</#00829c>")
            .lore(new ArrayList<>(List.of(
            )))
            .glow(true)
            .build();

    private Item noAccessItem = Item.builder()
            .material(Material.RED_DYE)
            .name("<#6ee7ff>Brak dostępu</#00829c>")
            .lore(new ArrayList<>(List.of(
                    "&7Nie posiadasz &cdostępu&7 do tego zestawu!"
            )))
            .glow(true)
            .build();

    private Item cooldownItem = Item.builder()
            .material(Material.ORANGE_DYE)
            .name("<#6ee7ff>Odbierz przedmioty z zestawu!</#00829c>")
            .lore(new ArrayList<>(List.of(
                    "&7Następny raz możesz odebrać za &a{time}!"
            )))
            .glow(true)
            .build();

    private Item hasAccessItem = Item.builder()
            .material(Material.LIME_DYE)
            .name("<#6ee7ff>Odbierz zestaw</#00829c>")
            .lore(new ArrayList<>(List.of(
                    "&7Kliknij &aLPM &7aby odebrać zestawu!"
            )))
            .glow(true)
            .build();

    private InventoryConfig kitListInv = InventoryConfig.builder()
            .size(45)
            .title("&0Zestawy")
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
            .build();

}
