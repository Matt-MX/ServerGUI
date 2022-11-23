package com.mattmx.servergui.util;

import co.pvphub.velocity.extensions.SendResult;
import co.pvphub.velocity.scheduling.AsyncTask;
import com.mattmx.servergui.Servergui;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import static co.pvphub.velocity.util.FormattingKt.color;

public class ServerConnection {

    public static void connectPlayerAsync(Player player, RegisteredServer server, Consumer<SendResult> cb) {
        new AsyncTask(Servergui.get(), (task) -> {
            cb.accept(connectPlayer(player, server));

            return null;
        }).schedule();
    }

    public static SendResult connectPlayer(Player player, RegisteredServer server) {
        YamlConfiguration config = Servergui.get().getConfig();
        // Make sure the player isn't already connected to this server.
        if (player.getCurrentServer().get().getServer() == server) {
            player.sendMessage(color(config.getString("command-feedback.server.already-connected", "&7You are already connected to that server"), player, server));
            return SendResult.FAIL_OTHER;
        }
        // Make sure the server is available to join
        try {
            server.ping().join();
        } catch (CancellationException | CompletionException e) {
            player.sendMessage(color(config.getString("command-feedback.server.failure", "&cUnable to connect to %server-name%."), null, server));
            return SendResult.FAIL_UNAVAILABLE;
        }
        // Try and connect the player
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
