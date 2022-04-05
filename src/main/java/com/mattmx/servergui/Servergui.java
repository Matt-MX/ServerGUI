package com.mattmx.servergui;

import com.google.inject.Inject;
import com.mattmx.servergui.commands.ServerCommand;
import com.mattmx.servergui.commands.ServerGuiCommand;
import com.mattmx.servergui.util.Config;
import com.mattmx.servergui.util.DependencyChecker;
import com.mattmx.servergui.util.VelocityPlugin;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "servergui",
        name = "Servergui",
        version = "1.0",
        description = "A GUI for all your servers!",
        url = "https://www.mattmx.com/",
        authors = {"MattMX"}
)
public class Servergui extends VelocityPlugin {
    static Servergui instance;

    @Inject
    public Servergui(ProxyServer server, Logger logger) {
        this.init(server, logger, "servergui");
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Config.init();
        getServer().getCommandManager().register("server", new ServerCommand());
        getServer().getCommandManager().register("servergui", new ServerGuiCommand());
    }

    public static Servergui get() {
        return instance;
    }
}
