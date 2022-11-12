package com.mattmx.servergui;

import co.pvphub.velocity.command.SimpleCommandBuilder;
import co.pvphub.velocity.plugin.VelocityPlugin;
import co.pvphub.velocity.scheduling.AsyncTask;
import com.google.inject.Inject;
import com.mattmx.servergui.commands.CustomServerCommand;
import com.mattmx.servergui.gui.ServerSelectorGui;
import com.mattmx.servergui.util.updater.UpdateChecker;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static co.pvphub.velocity.scheduling.SchedulingKt.async;
import static co.pvphub.velocity.util.FormattingKt.color;
import static co.pvphub.velocity.util.FormattingKt.getSerializer;
import static com.mattmx.servergui.util.ServerConnection.connectPlayer;

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

    private ScheduledTask refreshTask;

    @Inject
    public Servergui(@NotNull ProxyServer server, @NotNull Logger logger, @DataDirectory Path dataDirectory) {
        super(server, logger, dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        saveDefaultConfig();
        async(this, (task) -> {
            checker = new UpdateChecker().get("https://api.github.com/repos/Matt-MX/ServerGUI/releases/latest");
            if (checker.isLatest(this.getClass().getAnnotation(Plugin.class).version())) {
                getLogger().info("Running latest version of ServerGUI (" + checker.getLatest() + ")");
            } else {
                getLogger().warning("Newer version available! (ServerGUI " + checker.getLatest() + ")");
                getLogger().warning("Get it here: " + checker.getLink());
            }
            return null;
        });
        instance = this;

        new SimpleCommandBuilder("server", "servergui.command.server", "servergui")
                .subCommand(new SimpleCommandBuilder("reload", "servergui.command.reload")
                        .executes((executor, args, alias) -> {
                            executor.sendMessage(color("&7Config reloading", null, null));
                            return null;
                        }))
                .executes((executor, args, alias) -> {
                    async(this, (task) -> {
                        if (args.size() == 0) {
                            ServerSelectorGui.get().open((Player) executor);
                        } else {
                            String serverName = args.get(0);
                            RegisteredServer server = getServer().getAllServers()
                                    .stream()
                                    .filter(s -> s.getServerInfo().getName().equalsIgnoreCase(serverName))
                                    .findFirst()
                                    .orElse(null);
                            if (server == null) {
                                executor.sendMessage(color("&cInvalid server name!", null, null));
                                return null;
                            }
                            connectPlayer((Player) executor, server);
                        }
                        return null;
                    });
                    return null;
                }).suggests( arg -> {
                    List<String> result =
                            getServer()
                            .getAllServers()
                            .stream()
                            .map(s -> s.getServerInfo().getName())
                            .filter(s -> s.startsWith(arg))
                            .collect(Collectors.toList());
                    result.add("reload");
                    return result;
                }).register(this);

        // Create a repeating task to refresh the gui instance
        refreshTask = new AsyncTask(this, (task) -> {
            ServerSelectorGui.refresh();
            return null;
        }).repeat(getConfig().getLong("server-selector.refresh"), TimeUnit.MILLISECONDS).schedule();

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
