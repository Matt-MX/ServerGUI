package com.mattmx.servergui.gui;

import com.mattmx.servergui.util.gui.InventoryGui;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.kyori.adventure.text.Component;

import static co.pvphub.velocity.util.FormattingKt.color;
import static com.mattmx.servergui.util.gui.InventoryGui.gui;
import static com.mattmx.servergui.util.gui.ItemButton.itemButton;

public class ServerSelectorGui {
    private static InventoryGui ssgui = gui(Component.text("Server Selector"), InventoryType.GENERIC_9X6, (gui) -> {
        itemButton((button) -> {
            button.item(ib -> {
                ib.type(ItemType.NETHER_STAR)
                        .name(color("&dVelocity GUI", null, null));
            });
            button.onClick((click, b) -> {

            });
        }).childOf(gui).slot(10);
    });

    public static InventoryGui get() {
        return ssgui;
    }
}
