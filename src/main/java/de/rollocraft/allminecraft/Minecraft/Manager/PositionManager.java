package de.rollocraft.allminecraft.Minecraft.Manager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class PositionManager {
    private double x;
    private double y;
    private double z;

    public PositionManager(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public Location toLocation(World world) {
        return new Location(world, this.getX(), this.getY(), this.getZ());
    }
}