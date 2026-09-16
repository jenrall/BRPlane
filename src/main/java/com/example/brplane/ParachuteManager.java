package com.example.brplane;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ParachuteManager {

    private final Set<UUID> jumping = new HashSet<>();
    private final Set<UUID> parachuting = new HashSet<>();

    public void startJump(Player p) {
        jumping.add(p.getUniqueId());
        parachuting.remove(p.getUniqueId());
    }

    public void openChute(Player p) {
        jumping.remove(p.getUniqueId());
        parachuting.add(p.getUniqueId());
    }

    public void endParachute(Player p) {
        jumping.remove(p.getUniqueId());
        parachuting.remove(p.getUniqueId());
    }

    public boolean isJumping(Player p) {
        return jumping.contains(p.getUniqueId());
    }

    public boolean isParachuting(Player p) {
        return parachuting.contains(p.getUniqueId());
    }

    public Set<UUID> getParachuting() { return parachuting; }

    public void clear() {
        jumping.clear();
        parachuting.clear();
    }
}
