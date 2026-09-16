package com.example.brplane;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class BRPlane extends JavaPlugin {

    private static BRPlane instance;
    private PlaneManager planeManager;
    private ParachuteManager parachuteManager;

    private ArmorStand planeEntity;
    private BukkitTask planeTask;
    private final Set<UUID> riders = new HashSet<>();
    private Location currentPlaneLoc;

    @Override
    public void onEnable() {
        instance = this;
        this.planeManager = new PlaneManager(this);
        this.parachuteManager = new ParachuteManager();

        getCommand("brplane").setExecutor(new PlaneCommand(this));
        getServer().getPluginManager().registerEvents(new PlaneListener(this), this);

        Bukkit.getScheduler().runTaskTimer(this, this::parachuteTick, 0L, 2L);

        getLogger().info("BRPlane enabled!");
    }

    @Override
    public void onDisable() {
        if (planeTask != null) planeTask.cancel();
        if (planeEntity != null) planeEntity.remove();
        if (planeManager != null) planeManager.save();
        getLogger().info("BRPlane disabled!");
    }

    public static BRPlane getInstance() { return instance; }
    public PlaneManager getPlaneManager() { return planeManager; }
    public ParachuteManager getParachuteManager() { return parachuteManager; }
    public Set<UUID> getRiders() { return riders; }

    // ============ Plane ============

    public boolean startPlane(String arenaName) {
        Plane plane = planeManager.getPlane(arenaName);
        if (plane == null) return false;
        if (!plane.isReady()) return false;

        Location start = plane.getStartPoint();
        Location end = plane.getEndPoint();

        planeEntity = (ArmorStand) start.getWorld().spawnEntity(start, EntityType.ARMOR_STAND);
        planeEntity.setVisible(false);
        planeEntity.setGravity(false);
        planeEntity.setInvulnerable(true);
        planeEntity.setCustomName("✈ BR Plane");
        planeEntity.setCustomNameVisible(true);
        planeEntity.setSmall(false);

        currentPlaneLoc = start.clone();
        riders.clear();

        // همه بازیکنای آنلاین سوار شن (ساده: همه بازیکنای آنلاین)
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(start);
            riders.add(p.getUniqueId());
        }

        Vector dir = end.toVector().subtract(start.toVector()).normalize();
        double speed = plane.getSpeed() * 0.5;

        planeTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (planeEntity == null || currentPlaneLoc == null) return;
            if (currentPlaneLoc.distance(end) < 2) {
                stopPlane();
                return;
            }

            currentPlaneLoc.add(dir.clone().multiply(speed));
            planeEntity.teleport(currentPlaneLoc);

            currentPlaneLoc.getWorld().spawnParticle(Particle.CLOUD, currentPlaneLoc, 3, 0.5, 0.5, 0.5, 0.05);

            for (UUID id : riders) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) p.teleport(currentPlaneLoc.clone().add(0, -1, 0));
            }

            if (plane.getJumpPoint() != null
                    && currentPlaneLoc.distance(plane.getJumpPoint()) < 3) {
                Bukkit.broadcast(Component.text("🪂 الان می‌تونی بپری! /brplane jump", NamedTextColor.GOLD));
            }
        }, 0L, 1L);

        Bukkit.broadcast(Component.text("✈ هواپیما پرواز کرد!", NamedTextColor.GOLD));
        return true;
    }

    public void stopPlane() {
        if (planeTask != null) { planeTask.cancel(); planeTask = null; }
        if (planeEntity != null) { planeEntity.remove(); planeEntity = null; }
        riders.clear();
        currentPlaneLoc = null;
        Bukkit.broadcast(Component.text("✈ هواپیما ناپدید شد.", NamedTextColor.YELLOW));
    }

    public void jump(Player p) {
        if (!riders.contains(p.getUniqueId())) {
            p.sendMessage(Component.text("تو سوار هواپیما نیستی.", NamedTextColor.RED));
            return;
        }
        riders.remove(p.getUniqueId());
        p.teleport(currentPlaneLoc != null ? currentPlaneLoc : p.getLocation());
        parachuteManager.startJump(p);
        parachuteManager.openChute(p); // چتر رو مستقیم باز کن
        p.sendMessage(Component.text("🪂 پریدی! چتر نجات باز شد!", NamedTextColor.GOLD));
    }

    // ============ Parachute tick ============

    private void parachuteTick() {
        for (UUID id : new HashSet<>(parachuteManager.getParachuting())) {
            Player p = Bukkit.getPlayer(id);
            if (p == null) continue;

            if (p.getVelocity().getY() < -0.3) {
                p.setVelocity(p.getVelocity().setY(-0.3));
            }

            p.setFallDistance(0);

            Location loc = p.getLocation().add(0, 2, 0);
            loc.getWorld().spawnParticle(Particle.CLOUD, loc, 5, 0.3, 0.1, 0.3, 0.01);

            if (p.isOnGround()) {
                parachuteManager.endParachute(p);
                p.sendMessage(Component.text("✅ فرود آمدی!", NamedTextColor.GREEN));
            }
        }
    }
}
