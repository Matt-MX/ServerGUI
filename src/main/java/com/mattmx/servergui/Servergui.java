package com.mattmx.servergui;

import co.pvphub.velocity.command.SimpleCommandBuilder;
import co.pvphub.velocity.plugin.VelocityPlugin;
import co.pvphub.velocity.scheduling.AsyncTask;
import com.google.inject.Inject;
import com.mattmx.servergui.commands.CustomServerCommand;
import com.mattmx.servergui.commands.ServerCommand;
import com.mattmx.servergui.commands.ServerGuiCommand;
import com.mattmx.servergui.gui.ServerSelectorGui;
import com.mattmx.servergui.listener.Listener;
import com.mattmx.servergui.util.updater.UpdateChecker;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static co.pvphub.velocity.scheduling.SchedulingKt.async;
import static co.pvphub.velocity.util.FormattingKt.color;

@Plugin(
        id = "servergui",
        name = "Servergui",
        version = "1.1",
        description = "A GUI for all your servers!",
        url = "https://www.mattmx.com/",
        authors = {"MattMX"}
)
public class Servergui extends VelocityPlugin {
    static Servergui instance;
    private UpdateChecker checker;
    public YamlConfiguration messages;

    @Inject
    public Servergui(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        super(server, logger, dataDirectory);
        saveDefaultConfig();
        messages = createOrLoadConfig("messages.yml", "messages.yml");
        async(this, (task) -> {
            checker = new UpdateChecker().get("https://api.github.com/repos/Matt-MX/ServerGUI/releases/latest");
            if (checker.isLatest(this.getClass().getAnnotation(Plugin.class).version())) {
                logger.info("Running latest version of ServerGUI (" + checker.getLatest() + ")");
            } else {
                logger.info("Newer version available! (ServerGUI " + checker.getLatest() + ")");
                logger.info("Get it here: " + checker.getLink());
            }
            return null;
        });
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        getServer().getEventManager().register(this, new Listener());
        new SimpleCommandBuilder("server", "servergui.command.server", "servergui")
                .subCommand(new SimpleCommandBuilder("reload", "servergui.command.reload")
                        .executes((executor, args, alias) -> {
                            executor.sendMessage(color("&7Config reloading", null, null));
                            return null;
                        }))
                .executes((executor, args, alias) -> {
                    async(this, (task) -> {
                        // if args.size == 0 then server gui open
                        if (args.size() == 0) {
                            ServerSelectorGui.get().openSync((Player) executor);
                        } else {
                            // try connect to server
                        }
                        return null;
                    });
                    return null;
                }).register(this);

        // Initialize custom server commands
        FileConfiguration config = getConfig();
        if (config.getConfigurationSection("commands") == null) return;
        for (String cmd : config.getConfigurationSection("commands").getKeys(false)) {
            List<String> servers = new ArrayList<>();
            if (config.getString("commands." + cmd) != null) {
                servers = List.of(config.getString("commands." + cmd));
            } else {
                if (config.getStringList("commands." + cmd).size() > 0) {
                    servers = config.getStringList("commands." + cmd);
                }
            }
            getServer().getCommandManager().register(cmd, new CustomServerCommand(servers));
            getLogger().info("Registered command /" + cmd + " with servers " + String.join(", ", servers));
        }
    }

    public static Servergui get() {
        return instance;
    }

    public UpdateChecker getUpdateChecker() {
        return checker;
    }
}
