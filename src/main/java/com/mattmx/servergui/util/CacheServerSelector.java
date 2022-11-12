package com.mattmx.servergui.util;

import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.kyori.adventure.text.Component;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.HashMap;

public class CacheServerSelector {
    private static HashMap<Integer, CustomButton> buttons = new HashMap<>();
    private Component title;
    private InventoryType type;

    public void load(FileConfiguration config) {
        buttons.clear();
        this.title = VelocityChat.color(config.getString("server-selector.title"));
        this.type = getInventoryType(config.getInt("server-selector.rows"));
        if (config.getConfigurationSection("server-selector.items") != null) {
            for (String key : config.getConfigurationSection("server-selector.items").getKeys(false)) {
                String fullKey = "server-selector.items." + key;
                CustomButton button = CustomButton.from(config, fullKey);
                if (button != null) {
                    buttons.put(button.getSlot(), button);
                }
            }
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
}
