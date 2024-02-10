package de.rollocraft.allminecraft.Manager;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import javax.annotation.Nonnull;

public final class BukkitReflectionUtils {
    public static boolean isAir(@Nonnull Material material) {
        try {
            return material.isAir();
        } catch (Throwable ex) {
        }

        switch (material.name()) {
            case "AIR":
            case "VOID_AIR":
            case "CAVE_AIR":
            case "LEGACY_AIR":
                return true;
            default:
                return false;
        }
    }

    public static int getMinHeight(@Nonnull World world) {
        try {
            return world.getMinHeight();
        } catch (Throwable ex) {
        }

        return 0;
    }
    @Deprecated
    public static boolean isInWater(@Nonnull Entity entity) {
        try {
            return entity.isInWater();
        } catch (Throwable ex) {
        }

        return false;
    }
}