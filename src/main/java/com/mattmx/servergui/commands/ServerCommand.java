package com.mattmx.servergui.commands;

import com.google.common.collect.ImmutableList;
import com.mattmx.servergui.Servergui;
import com.mattmx.servergui.gui.ServerSelector;
import com.mattmx.servergui.util.Config;
import com.mattmx.servergui.util.VelocityChat;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ServerCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("Player only command...").color(NamedTextColor.RED));
            return;
        }
        Player p = (Player) source;
        if (p.hasPermission("servergui.commands.server")) {
            if (args.length > 0) {
                if (isValidServer(args[0])) {
                    if (p.getCurrentServer().get().getServer() == Servergui.get().getServer().getServer(args[0]).get()) {
                        p.sendMessage(VelocityChat.color(
                                Config.MESSAGES.getString("command-feedback.server.already-connected")
                                        .replace("%server%", args[0]), p
                        ));
                        return;
                    }
                    p.sendMessage(VelocityChat.color(
                            Config.MESSAGES.getString("command-feedback.server.connecting")
                                    .replace("%server%", args[0])
                    ));
                    Servergui.get().getServer().getScheduler().buildTask(Servergui.get(), () -> {
                        Servergui.get().getServer().getServer(args[0]).ifPresent(s -> {
                            CompletableFuture<ConnectionRequestBuilder.Result> result = p.createConnectionRequest(s).connect();
                            try {
                                if (!result.get().isSuccessful()) {
                                    p.sendMessage(VelocityChat.color(
                                            Config.MESSAGES.getString("command-feedback.server.failure")
                                                    .replace("%server%", args[0]), p, s
                                    ));
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                p.sendMessage(VelocityChat.color(
                                        Config.MESSAGES.getString("command-feedback.server.failure")
                                                .replace("%server%", args[0]), p, s
                                ));
                            }
                        });
                    }).schedule();
                } else {
                    p.sendMessage(VelocityChat.color(
                            Config.MESSAGES.getString("command-feedback.server.invalid-server")
                                    .replace("%server%", args[0]), p
                    ));
                }
            } else {
                ServerSelector sel = new ServerSelector();
                sel.define(p);
                sel.open();
            }
        } else {
            p.sendMessage(VelocityChat.color(
                    Config.MESSAGES.getString("command-feedback.no-permissions")
            ));
        }
    }

    public boolean isValidServer(String name) {
        for (String s : getRegistered()) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            List<String> c = new ArrayList<>();
            for (String r : getRegistered()) {
                if (r.startsWith(args[0])) {
                    c.add(r);
                }
            }
            return c;
        } else if (args.length == 0) {
            return getRegistered();
        }
        return ImmutableList.of();
    }

    public List<String> getRegistered() {
        return Servergui.get().getServer().getAllServers().stream().map(s -> s.getServerInfo().getName()).collect(Collectors.toList());
    }
}