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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServerGuiCommand implements SimpleCommand {
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
                switch (args[0]) {
                    case "reload" -> {
                        if (p.hasPermission("servergui.commands.reload")) {
                            Config.init();
                            p.sendMessage(VelocityChat.color("&#2790fdS&#269dfde&#26aafdr&#25b6fdv&#25c3fde&#24d0fdr&#23ddfdG&#23e9fdu&#22f6fdi &7» &fConfig Reloaded"));
                        } else {
                            p.sendMessage(VelocityChat.color(
                                    Config.MESSAGES.getString("command-feedback.no-permissions")
                            ));
                        }
                    }
                    default -> {
                        p.sendMessage(VelocityChat.color("&#2790fdS&#269dfde&#26aafdr&#25b6fdv&#25c3fde&#24d0fdr&#23ddfdG&#23e9fdu&#22f6fdi &7» &fRunning v1.0.0"));
                    }
                }
            } else {
                p.sendMessage(VelocityChat.color("&#2790fdS&#269dfde&#26aafdr&#25b6fdv&#25c3fde&#24d0fdr&#23ddfdG&#23e9fdu&#22f6fdi &7» &fRunning v1.0.0"));
            }
        } else {
            p.sendMessage(VelocityChat.color(
                    Config.MESSAGES.getString("command-feedback.no-permissions")
            ));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            if (invocation.source().hasPermission("servergui.commands.reload")) {
                return List.of("reload");
            }
        }
        return ImmutableList.of();
    }
}