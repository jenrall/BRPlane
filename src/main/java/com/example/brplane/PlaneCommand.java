package com.example.brplane;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaneCommand implements CommandExecutor {

    private final BRPlane plugin;

    public PlaneCommand(BRPlane plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players.");
            return true;
        }

        if (args.length == 0) { help(p); return true; }

        switch (args[0].toLowerCase()) {
            case "help" -> help(p);
            case "setroute" -> setRoute(p, args);
            case "jumppoint" -> setJumpPoint(p, args);
            case "speed" -> setSpeed(p, args);
            case "start" -> start(p, args);
            case "stop" -> {
                plugin.stopPlane();
                p.sendMessage(Component.text("هواپیما متوقف شد.", NamedTextColor.YELLOW));
            }
            case "jump" -> plugin.jump(p);
            case "info" -> info(p, args);
            default -> p.sendMessage(Component.text("Unknown. /brplane help", NamedTextColor.RED));
        }
        return true;
    }

    private void help(Player p) {
        p.sendMessage(Component.text("=== BRPlane ===", NamedTextColor.GOLD));
        p.sendMessage(Component.text("/brplane setroute <arena>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/brplane jumppoint <arena>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/brplane speed <arena> <n>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/brplane start <arena>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/brplane stop", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/brplane jump", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/brplane info <arena>", NamedTextColor.YELLOW));
    }

    private void setRoute(Player p, String[] args) {
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /brplane setroute <arena>", NamedTextColor.RED)); return; }
        Plane pl = plugin.getPlaneManager().getOrCreate(args[1]);
        if (pl.getStartPoint() == null) {
            pl.setStartPoint(p.getLocation());
            p.sendMessage(Component.text("Start point set. حالا برو به نقطه پایان و دوباره بزن.", NamedTextColor.GREEN));
        } else if (pl.getEndPoint() == null) {
            pl.setEndPoint(p.getLocation());
            plugin.getPlaneManager().save();
            p.sendMessage(Component.text("End point set. مسیر آماده‌ست!", NamedTextColor.GREEN));
        } else {
            pl.setStartPoint(p.getLocation());
            pl.setEndPoint(null);
            p.sendMessage(Component.text("Start point reset. حالا برو به نقطه پایان.", NamedTextColor.YELLOW));
        }
    }

    private void setJumpPoint(Player p, String[] args) {
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /brplane jumppoint <arena>", NamedTextColor.RED)); return; }
        Plane pl = plugin.getPlaneManager().getOrCreate(args[1]);
        pl.setJumpPoint(p.getLocation());
        plugin.getPlaneManager().save();
        p.sendMessage(Component.text("نقطه پرش تنظیم شد.", NamedTextColor.GREEN));
    }

    private void setSpeed(Player p, String[] args) {
        if (args.length < 3) { p.sendMessage(Component.text("Usage: /brplane speed <arena> <1-5>", NamedTextColor.RED)); return; }
        Plane pl = plugin.getPlaneManager().getOrCreate(args[1]);
        try {
            int s = Integer.parseInt(args[2]);
            if (s < 1 || s > 5) { p.sendMessage(Component.text("عدد باید بین 1 تا 5 باشه.", NamedTextColor.RED)); return; }
            pl.setSpeed(s);
            plugin.getPlaneManager().save();
            p.sendMessage(Component.text("سرعت تنظیم شد.", NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            p.sendMessage(Component.text("عدد نامعتبر.", NamedTextColor.RED));
        }
    }

    private void start(Player p, String[] args) {
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /brplane start <arena>", NamedTextColor.RED)); return; }
        if (!plugin.startPlane(args[1])) {
            p.sendMessage(Component.text("شروع نشد. مسیر تنظیم شده؟", NamedTextColor.RED));
        }
    }

    private void info(Player p, String[] args) {
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /brplane info <arena>", NamedTextColor.RED)); return; }
        Plane pl = plugin.getPlaneManager().getPlane(args[1]);
        if (pl == null) { p.sendMessage(Component.text("پیدا نشد.", NamedTextColor.RED)); return; }
        p.sendMessage(Component.text("=== " + pl.getArenaName() + " ===", NamedTextColor.GOLD));
        p.sendMessage(Component.text("Start: " + (pl.getStartPoint() != null ? "✅" : "❌"), NamedTextColor.YELLOW));
        p.sendMessage(Component.text("End: " + (pl.getEndPoint() != null ? "✅" : "❌"), NamedTextColor.YELLOW));
        p.sendMessage(Component.text("Jump: " + (pl.getJumpPoint() != null ? "✅" : "❌"), NamedTextColor.YELLOW));
        p.sendMessage(Component.text("Speed: " + pl.getSpeed(), NamedTextColor.YELLOW));
    }
}
