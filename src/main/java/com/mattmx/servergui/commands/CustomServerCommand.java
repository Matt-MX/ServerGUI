package com.mattmx.servergui.commands;

import co.pvphub.velocity.scheduling.AsyncTask;
import com.mattmx.servergui.Servergui;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CustomServerCommand implements SimpleCommand {
    private List<String> servers;

    public CustomServerCommand(List<String> servers) {
        this.servers = servers;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            return;
        }
        Player p = (Player) invocation.source();
        new AsyncTask(Servergui.get(), (task -> {
            for (String server : servers) {
                Servergui.get().getServer().getServer(server).ifPresent(s -> {
                    CompletableFuture<ConnectionRequestBuilder.Result> result = p.createConnectionRequest(s).connect();
                    try {
                        if (result.get().isSuccessful()) {
                        }
                    } catch (InterruptedException | ExecutionException e) {
                    }
                });
            }
            return null;
        })).schedule();
    }
}
