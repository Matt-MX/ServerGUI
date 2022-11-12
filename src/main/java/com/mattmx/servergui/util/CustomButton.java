package com.mattmx.servergui.util;

import com.mattmx.servergui.Servergui;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.protocolize.data.ItemType;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.stream.Collectors;

public class CustomButton {
    private int slot;
    private ItemBuilder builder;

    public ItemBuilder getItem() {
        return builder;
    }

    public int getSlot() {
        return slot;
    }

    public static CustomButton from(FileConfiguration config, String path) {
        String name = config.getString(path + ".server");
        if (name != null) {
            RegisteredServer server = null;
            for (RegisteredServer s : Servergui.get().getServer().getAllServers()) {
                if (s.getServerInfo().getName().equalsIgnoreCase(name)) {
                    server = s;
                    break;
                }
            }
            return from(config, path, server);
        }
        return from(config, path, null);
    }

    public static CustomButton from(FileConfiguration config, String path, RegisteredServer server) {
        CustomButton button = new CustomButton();
        button.slot = slotFrom(path);
        button.builder = itemBuilderFrom(config, path, server);
        if (button.builder == null || button.slot == -1) {
            return null;
        }
        return button;
    }

    private static ItemBuilder itemBuilderFrom(FileConfiguration config, String path, RegisteredServer server) {
        if (config.getConfigurationSection(path) != null) {
            ItemBuilder builder;
            if (config.getString(path + ".material") != null) {
                try {
                    builder = new ItemBuilder(ItemType.valueOf(config.getString(path + ".material")));
                } catch (IllegalArgumentException e) {
                    return null;
                }
                if (config.getString(path + ".name") != null) builder.name(VelocityChat.color(config.getString(path + ".name"), null, server));
                ServerStatus status = getStatus(server);
                if (config.getString(path + ".server") == null) {
                    status = ServerStatus.AVALIABLE;
                }
                if (config.getBoolean("server-selector.dynamic-items")) {
                    if (status != null) {
                        switch (status) {
                            case FULL -> {
                                builder.lore(config.getStringList("server-selector.lores.full").stream().map(l -> VelocityChat.color(l, null, server).asComponent()).collect(Collectors.toList()));
                                try { builder.type(ItemType.valueOf(config.getString("server-selector.materials.full")));
                                } catch (Exception e) {}
                            }
                            case UNAVALIABLE -> {
                                builder.lore(config.getStringList("server-selector.lores.unavailable").stream().map(l -> VelocityChat.color(l, null, server).asComponent()).collect(Collectors.toList()));
                                try { builder.type(ItemType.valueOf(config.getString("server-selector.materials.unavailable")));
                                } catch (Exception e) {}
                            }
                            case ALREADY_CONNECTED -> {
                                builder.lore(config.getStringList("server-selector.lores.connected").stream().map(l -> VelocityChat.color(l, null, server).asComponent()).collect(Collectors.toList()));
                                try { builder.type(ItemType.valueOf(config.getString("server-selector.materials.connected")));
                                } catch (Exception e) {}
                            }
                            default -> builder.lore(
                                    config.getStringList(path + ".lore")
                                            .stream().map(l -> VelocityChat.color(l, null, server).asComponent()).collect(Collectors.toList()));
                        }
                    }
                } else {
                    builder.lore(config.getStringList(path + ".lore").stream().map(l -> VelocityChat.color(l, null, server).asComponent()).collect(Collectors.toList()));
                }
                return builder;
            }
        }
        return null;
    }

    private static int slotFrom(String path) {
        String[] args = path.split("\\.");
        if (args.length == 0) {
            return -1;
        }
        try {
            return Integer.parseInt(args[args.length - 1]);
        } catch (Exception e) {
            return -1;
        }
    }

    public static ServerStatus getStatus(RegisteredServer server) {
        if (server == null) {
            return ServerStatus.UNAVALIABLE;
        }
//        if (server.getPlayersConnected().contains(p)) {
//            return ServerStatus.ALREADY_CONNECTED;
//        }
        try {
            server.ping().join();
            return ServerStatus.AVALIABLE;
        } catch (Exception e) {
            return ServerStatus.UNAVALIABLE;
        }
    }

    public CustomButton format(Player p) {
    }
}
