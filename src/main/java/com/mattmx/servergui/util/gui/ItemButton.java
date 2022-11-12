package com.mattmx.servergui.util.gui;

import com.mattmx.servergui.util.ItemBuilder;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
}
