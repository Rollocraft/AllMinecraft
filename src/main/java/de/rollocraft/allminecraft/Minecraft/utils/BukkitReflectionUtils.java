package de.rollocraft.allminecraft.Minecraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import javax.annotation.Nonnull;

public final class BukkitReflectionUtils {
    public static boolean isAir(@Nonnull Material material) {
        try {
            return material.isAir();
        } catch (Throwable ex) {
            Bukkit.getLogger().info("Failed to check if material is air: " + ex);
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

    @Deprecated
    public static boolean isInWater(@Nonnull Entity entity) {
        try {
            return entity.isInWater();
        } catch (Throwable ex) {
            Bukkit.getLogger().info("Failed to check if entity is in water: " + ex);
        }

        return false;
    }
}