package com.example.brplane;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class PlaneManager {

    private final BRPlane plugin;
    private final Map<String, Plane> planes = new HashMap<>();

    public PlaneManager(BRPlane plugin) {
        this.plugin = plugin;
        load();
    }

    public Plane getPlane(String arenaName) {
        return planes.get(arenaName.toLowerCase());
    }

    public Plane getOrCreate(String arenaName) {
        Plane p = planes.get(arenaName.toLowerCase());
        if (p == null) {
            p = new Plane(arenaName);
            planes.put(arenaName.toLowerCase(), p);
        }
        return p;
    }

    public Map<String, Plane> getPlanes() { return planes; }

    public void save() {
        FileConfiguration cfg = plugin.getConfig();
        for (Plane p : planes.values()) {
            String path = "planes." + p.getArenaName().toLowerCase();
            cfg.set(path + ".start", p.getStartPoint());
            cfg.set(path + ".end", p.getEndPoint());
            cfg.set(path + ".jump", p.getJumpPoint());
            cfg.set(path + ".speed", p.getSpeed());
        }
        plugin.saveConfig();
    }

    private void load() {
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection sec = cfg.getConfigurationSection("planes");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            String path = "planes." + key;
            Plane p = new Plane(key);
            p.setStartPoint(cfg.getLocation(path + ".start"));
            p.setEndPoint(cfg.getLocation(path + ".end"));
            p.setJumpPoint(cfg.getLocation(path + ".jump"));
            p.setSpeed(cfg.getInt(path + ".speed", 1));
            planes.put(key, p);
        }
    }
}
