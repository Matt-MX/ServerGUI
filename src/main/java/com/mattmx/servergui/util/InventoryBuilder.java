package com.mattmx.servergui.util;

import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class InventoryBuilder {
    private InventoryType type;
    private Player player;
    private TextComponent title;
    private HashMap<Integer, ItemStack> items = new HashMap<>();
    private List<ItemStack> emptyItems = new ArrayList<>();

    public void type(InventoryType type) {
        this.type = type;
    }

    public InventoryType type() {
        return this.type;
    }

    public void setTitle(TextComponent title) {
        this.title = title;
    }

    public void define(Player p) {
        this.player = p;
        // Define the GUI
        // Add and remove elements
        // Set the player, type and title
    }

    protected Inventory build() {
        // Build the inventory we want to display
        Inventory inv = new Inventory(type);
        inv.title(title);
        if (!emptyItems.isEmpty()) {
            inv.items(emptyItems);
        }
        for (Integer index : items.keySet()) {
            ItemStack item = items.get(index);
            inv.item(index, item);
        }
        return inv;
    }

    public void open() {
        // Open the inventory to the player
        Inventory i = build();
        i.onClick(this::onClickCancel);
        i.onClose(this::onClose);
        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());
        protocolizePlayer.openInventory(i);
    }

    public void updateItems() {
        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());
        PlayerInventory inv = protocolizePlayer.proxyInventory();
        inv.clear();
        for (int i = 0; i < this.type.getTypicalSize(protocolizePlayer.protocolVersion()); i++) {
            // TODO FIX THIS
            inv.item(i, getSlot(i));
        }
    }

    public InventoryType getInventoryType(int value) {
        switch(value) {
            case 1:
                return InventoryType.GENERIC_9X1;
            case 2:
                return InventoryType.GENERIC_9X2;
            case 3:
                return InventoryType.GENERIC_9X3;
            case 4:
                return InventoryType.GENERIC_9X4;
            case 5:
                return InventoryType.GENERIC_9X5;
            default:
                return InventoryType.GENERIC_9X6;
        }
    }

    public ItemStack getSlot(int slot) {
        return items.get(slot);
    }

    public void onClose(InventoryClose close) {

    }

    public void onClick(InventoryClick click) {

    }

    private void onClickCancel(InventoryClick click) {
        click.cancelled(true);
        onClick(click);
    }

    protected void setEmpty(ItemType itemType) {
        ItemStack item = new ItemBuilder(itemType)
                .amount(1).name(Component.text("")).build();
        int totalSlots = type.getTypicalSize(player.getProtocolVersion().getProtocol());
        for (int i = 0; i < totalSlots; i++) {
            emptyItems.add(item);
        }
    }

    protected void setItem(int index, ItemStack item) {
        if (item != null) {
            items.put(index, item);
        }
    }

    protected void removeItem(int index) {
        items.remove(index);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void clear() {
        this.items.clear();
    }
}