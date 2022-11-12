package com.mattmx.servergui.util.gui;

import com.mattmx.servergui.Servergui;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.kyori.adventure.text.TextComponent;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static co.pvphub.velocity.scheduling.SchedulingKt.sync;

public class InventoryGui {
    public final TextComponent title;
    public final InventoryType type;
    public final HashMap<Integer, ItemButton> slots = new HashMap<Integer, ItemButton>();
    private BiConsumer<InventoryGui, InventoryClick> clickEvent;
    private BiConsumer<InventoryGui, InventoryClose> closeEvent;

    public InventoryGui(TextComponent title, InventoryType type) {
        this.title = title;
        this.type = type;
    }

    public InventoryGui click(BiConsumer<InventoryGui, InventoryClick> click) {
        this.clickEvent = click;
        return this;
    }

    public InventoryGui close(BiConsumer<InventoryGui, InventoryClose> close) {
        this.closeEvent = close;
        return this;
    }

    public ItemButton slot(int slot) {
        return slots.get(slot);
    }

    public void onClose(InventoryClose close) {
        if (closeEvent != null) closeEvent.accept(this, close);
    }

    public void onClick(InventoryClick click) {
        if (clickEvent != null) clickEvent.accept(this, click);
        ItemButton button = slot(click.slot());
        if (button != null) button.click(click);
    }

    private void onClickCancel(InventoryClick click) {
        click.cancelled(true);
        onClick(click);
    }

    public Inventory build() {
        Inventory inv = new Inventory(type);
        inv.title(title);
        for (Integer i : slots.keySet()) {
            inv.item(i, slots.get(i).item());
        }
        return inv;
    }

    public void open(Player player) {
        Inventory i = build();
        i.onClick(this::onClickCancel);
        i.onClose(this::onClose);
        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());
        protocolizePlayer.openInventory(i);
    }

    public void openSync(Player player) {
        Inventory i = build();
        i.onClick(this::onClickCancel);
        i.onClose(this::onClose);
        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());
        sync(Servergui.get(), task -> {
            protocolizePlayer.openInventory(i);
            return null;
        });
    }

    public static InventoryGui gui(TextComponent title, InventoryType type, Consumer<InventoryGui> gui) {
        InventoryGui g = new InventoryGui(title, type);
        gui.accept(g);
        return g;
    }

    public static InventoryType rowsOf(int rows) {
        return switch (rows) {
            case 1 -> InventoryType.GENERIC_9X1;
            case 2 -> InventoryType.GENERIC_9X2;
            case 3 -> InventoryType.GENERIC_9X3;
            case 4 -> InventoryType.GENERIC_9X4;
            case 5 -> InventoryType.GENERIC_9X5;
            default -> InventoryType.GENERIC_9X6;
        };
    }

}
