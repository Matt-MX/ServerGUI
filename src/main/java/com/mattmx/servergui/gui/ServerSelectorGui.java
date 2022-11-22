package com.mattmx.servergui.gui;

import com.mattmx.servergui.Servergui;
import com.mattmx.servergui.util.gui.InventoryGui;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.kyori.adventure.text.Component;
import org.simpleyaml.configuration.file.YamlConfiguration;

import static co.pvphub.velocity.util.FormattingKt.color;
import static com.mattmx.servergui.util.gui.InventoryGui.gui;
import static com.mattmx.servergui.util.gui.ItemButton.buttonFromConfig;
import static com.mattmx.servergui.util.gui.ItemButton.itemButton;

public class ServerSelectorGui {
    private static InventoryGui ssgui = build();

    public static InventoryGui build() {
        YamlConfiguration config = Servergui.get().getConfig();
        return gui(
                color(config.getString("server-selector.title", "Server Selector"), null, null),
                InventoryGui.rowsOf(Servergui.get().getConfig().getInt("server-selector.rows")),
                (gui) -> {
                    for (String key : config.getConfigurationSection("server-selector.items").getKeys(false)) {
                        String serverName = config.getString("server-selector.items." + key + ".server");
                        RegisteredServer server = serverName == null ? null : Servergui.get()
                                .getServer().getAllServers()
                                .stream()
                                .filter(s -> s.getServerInfo().getName().equalsIgnoreCase(serverName))
                                .findFirst()
                                .orElse(null);
                        buttonFromConfig(config, "server-selector.items." + key, server)
                                .childOf(gui)
                                .slot(Integer.parseInt(key));
                    }
                });
    }

    public static InventoryGui get() {
        return ssgui;
    }

    public static void refresh() {
        ssgui = build();
    }
}
