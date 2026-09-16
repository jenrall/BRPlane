package com.example.brplane;

import org.bukkit.Location;

public class Plane {

    private final String arenaName;
    private Location startPoint;
    private Location endPoint;
    private Location jumpPoint;
    private int speed = 1; // blocks per tick

    public Plane(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaName() { return arenaName; }
    public Location getStartPoint() { return startPoint; }
    public Location getEndPoint() { return endPoint; }
    public Location getJumpPoint() { return jumpPoint; }
    public int getSpeed() { return speed; }

    public void setStartPoint(Location l) { this.startPoint = l; }
    public void setEndPoint(Location l) { this.endPoint = l; }
    public void setJumpPoint(Location l) { this.jumpPoint = l; }
    public void setSpeed(int s) { this.speed = s; }

    public boolean isReady() {
        return startPoint != null && endPoint != null;
    }
}
