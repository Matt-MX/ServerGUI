package com.mattmx.servergui.util.gui;

import com.mattmx.servergui.Servergui;
import com.mattmx.servergui.util.ItemBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static co.pvphub.velocity.util.FormattingKt.color;
import static com.mattmx.servergui.util.ServerConnection.connectPlayer;

public class ItemButton {
    private BiConsumer<InventoryClick, ItemButton> clickEvent;
    private ItemStack item;
    private InventoryGui parent;

    public ItemButton item(Consumer<ItemBuilder> builder) {
        ItemBuilder b = new ItemBuilder(ItemType.STONE);
        builder.accept(b);
        item = b.build();
        return this;
    }

    public ItemButton slots(List<Integer> slots) {
        for (int i : slots) {
            this.parent.slots.put(i, this);
        }
        return this;
    }

    public ItemButton slots(int[] slots) {
        for (int i : slots) {
            this.parent.slots.put(i, this);
        }
        return this;
    }

    public ItemButton slot(int slot) {
        this.parent.slots.put(slot, this);
        return this;
    }

    public ItemButton childOf(InventoryGui parent) {
        this.parent = parent;
        return this;
    }

    public ItemButton onClick(BiConsumer<InventoryClick, ItemButton> clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public void click(InventoryClick click) {
        if (clickEvent != null) clickEvent.accept(click, this);
    }

    public ItemStack item() {
        return item;
    }

    public static ItemButton itemButton(Consumer<ItemButton> button) {
        ItemButton b = new ItemButton();
        button.accept(b);
        return b;
    }

    public static ItemButton buttonFromConfig(YamlConfiguration config, String key, RegisteredServer server) {
        ItemButton button = new ItemButton();
        button.item(ib -> {
            ib.type(ItemType.valueOf(config.getString(key + ".material").toUpperCase().replace(" ", "_")));
            ib.name(color(config.getString(key + ".name"), null, server));
            ib.lore(config.getStringList(key + ".lore")
                    .stream()
                    .map(s -> color(s, null, server))
                    .collect(Collectors.toList()));
            // todo enchantment parsing
        });
        button.onClick((click, b) -> {
            Player player = Servergui.get()
                    .getServer()
                    .getPlayer(click.player().uniqueId())
                    .orElse(null);
            if (server != null && player != null) {
                connectPlayer(player, server);
            }
        });
        return button;
    }
}
