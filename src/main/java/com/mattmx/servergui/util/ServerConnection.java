package com.mattmx.servergui.util;

import co.pvphub.velocity.extensions.SendResult;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.concurrent.CompletableFuture;

import static co.pvphub.velocity.util.FormattingKt.color;

public class ServerConnection {
    public static SendResult connectPlayer(Player player, RegisteredServer server) {
        try {
            CompletableFuture<ConnectionRequestBuilder.Result> result = player
                    .createConnectionRequest(server)
                    .connect();
            if (!result.get().isSuccessful()) {
                player.sendMessage(color("&7Sending you to %server-name%.", null, server));
                return SendResult.SUCCESS;
            }
        } catch (Exception e) {
            player.sendMessage(color("&cUnable to connect to %server-name%.", null, server));
        }
        return SendResult.FAIL_OTHER;
    }
}
