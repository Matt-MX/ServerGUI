package com.mattmx.servergui;

import com.google.inject.Inject;
import com.mattmx.servergui.commands.CustomServerCommand;
import com.mattmx.servergui.commands.ServerCommand;
import com.mattmx.servergui.commands.ServerGuiCommand;
import com.mattmx.servergui.listener.Listener;
import com.mattmx.servergui.util.Config;
import com.mattmx.servergui.util.DependencyChecker;
import com.mattmx.servergui.util.VelocityPlugin;
import com.mattmx.servergui.util.updater.UpdateChecker;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

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

    @Inject
    public Servergui(ProxyServer server, Logger logger) {
        this.init(server, logger, "servergui");
        checker = new UpdateChecker().get("https://api.github.com/repos/Matt-MX/ServerGUI/releases/latest");
        if (checker.isLatest(this.getClass().getAnnotation(Plugin.class).version())) {
            logger().info("Running latest version of ServerGUI (" + checker.getLatest() + ")");
        } else {
            logger().info("Newer version available! (ServerGUI " + checker.getLatest() + ")");
            logger().info("Get it here: " + checker.getLink());
        }
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Config.init();
        getServer().getEventManager().register(this, new Listener());
        getServer().getCommandManager().register("server", new ServerCommand());
        getServer().getCommandManager().register("servergui", new ServerGuiCommand());
        FileConfiguration config = Config.DEFAULT;
        for (String cmd : config.getConfigurationSection("commands").getKeys(false)) {
            if (config.getString("commands." + cmd) != null) {
                getServer().getCommandManager().register(cmd, new CustomServerCommand(List.of(config.getString("commands." + cmd))));
            } else {
                if (config.getStringList("commands." + cmd).size() > 0) {
                    getServer().getCommandManager().register(cmd, new CustomServerCommand(config.getStringList("commands." + cmd)));
                }
            }
        }
    }

    public static Servergui get() {
        return instance;
    }

    public UpdateChecker getUpdateChecker() {
        return checker;
    }
}
