package com.mattmx.servergui.listener;

import com.mattmx.servergui.Servergui;
import com.mattmx.servergui.util.VelocityChat;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class Listener {
    @Subscribe
    public void login(LoginEvent e) {
        if (e.getPlayer().hasPermission("velocity.reconnect.admin")) {
            if (!Servergui.get().getUpdateChecker().isLatest()) {
                e.getPlayer().sendMessage(VelocityChat.color("&9&lServerGui &7» &bNewer version available! &fServerGui v" + Servergui.get().getUpdateChecker().getLatest())
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, Servergui.get().getUpdateChecker().getLink()))
                        .hoverEvent(HoverEvent.showText(VelocityChat.color("&6Click to update!"))));
            }
        }
    }
}
