package com.mattmx.servergui.gui;

import com.mattmx.servergui.Servergui;
import com.mattmx.servergui.util.*;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import net.kyori.adventure.text.TextComponent;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServerSelector extends InventoryBuilder {

    @Override
    public void define(Player p) {
        super.define(p);
        FileConfiguration config = Config.DEFAULT;
        this.setTitle(VelocityChat.color(config.getString("server-selector.title"), p));
        this.type(getInventoryType(config.getInt("server-selector.rows")));
        if (config.getConfigurationSection("server-selector.items") != null) {
            for (String key : config.getConfigurationSection("server-selector.items").getKeys(false)) {
                String fullKey = "server-selector.items." + key;
                CustomButton button = CustomButton.from(config, fullKey, p);
                if (button != null) {
                    setItem(button.getSlot(), button.getItem().build());
                }
            }
        }
    }

    @Override
    public void onClick(InventoryClick click) {
        if (click.clickedItem() != null) {
            ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(getPlayer().getUniqueId());
            Player p = getPlayer();
            String name;
            FileConfiguration config = Config.DEFAULT;
            if (config.getString("server-selector.items." + click.slot() + ".server") != null) {
                name = config.getString("server-selector.items." + click.slot() + ".server");
            } else {
                return;
            }
            if (p.getCurrentServer().get().getServer().getServerInfo().getName().equalsIgnoreCase(name)) {
                p.sendMessage(VelocityChat.color(
                        Config.MESSAGES.getString("command-feedback.server.already-connected")
                                .replace("%server%", name)
                ));
                return;
            }
            p.sendMessage(VelocityChat.color(
                    Config.MESSAGES.getString("command-feedback.server.connecting")
                            .replace("%server%", name)
            ));
            Servergui.get().getServer().getScheduler().buildTask(Servergui.get(), () -> {
                Servergui.get().getServer().getServer(name).ifPresentOrElse(s -> {
                    try {
                        s.ping();
                        CompletableFuture<ConnectionRequestBuilder.Result> result = p.createConnectionRequest(s).connect();
                        try {
                            if (!result.get().isSuccessful()) {
                                p.sendMessage(VelocityChat.color(Config.MESSAGES.getString("command-feedback.server.failure"), p, s));
                            } else {
                                p.sendMessage(VelocityChat.color(Config.MESSAGES.getString("command-feedback.server.connected"), p, s));
                                protocolizePlayer.closeInventory();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            p.sendMessage(VelocityChat.color(
                                    Config.MESSAGES.getString("command-feedback.server.failure")
                                            .replace("%server%", name)
                            ));
                        }
                    } catch (Exception e) {
                        p.sendMessage(VelocityChat.color(
                                Config.MESSAGES.getString("command-feedback.server.failure")
                                        .replace("%server%", name)
                        ));
                    }
                }, () -> {
                    p.sendMessage(VelocityChat.color(
                            Config.MESSAGES.getString("command-feedback.server.invalid-server")
                                    .replace("%server%", name)
                    ));
                });
            }).schedule();
        }
    }
}