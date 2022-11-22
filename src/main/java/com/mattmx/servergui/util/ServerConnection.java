package com.mattmx.servergui.util;

import co.pvphub.velocity.extensions.SendResult;
import com.mattmx.servergui.Servergui;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.util.concurrent.CompletableFuture;

import static co.pvphub.velocity.util.FormattingKt.color;

public class ServerConnection {
    public static SendResult connectPlayer(Player player, RegisteredServer server) {
        YamlConfiguration config = Servergui.get().getConfig();
        if (player.getCurrentServer().get().getServer() == server) {
            player.sendMessage(color(config.getString("command-feedback.server.already-connected", "&7You are already connected to that server"), player, server));
            return SendResult.FAIL_OTHER;
        }
        try {
            CompletableFuture<ConnectionRequestBuilder.Result> result = player
                    .createConnectionRequest(server)
                    .connect();
            if (!result.get().isSuccessful()) {
                player.sendMessage(color(config.getString("command-feedback.server.connecting", "&7Sending you to %server-name%."), null, server));
                return SendResult.SUCCESS;
            }
        } catch (Exception e) {
            player.sendMessage(color(config.getString("command-feedback.server.failure", "&cUnable to connect to %server-name%."), null, server));
        }
        return SendResult.FAIL_OTHER;
    }
}
