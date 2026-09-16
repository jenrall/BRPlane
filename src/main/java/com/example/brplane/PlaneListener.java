package com.example.brplane;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaneListener implements Listener {

    private final BRPlane plugin;

    public PlaneListener(BRPlane plugin) { this.plugin = plugin; }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getParachuteManager().endParachute(e.getPlayer());
    }
}
