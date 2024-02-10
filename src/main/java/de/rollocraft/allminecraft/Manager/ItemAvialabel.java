package de.rollocraft.allminecraft.Manager;

import org.bukkit.Material;

import de.rollocraft.allminecraft.Manager.BukkitReflectionUtils;

import javax.annotation.Nonnull;

public class ItemAvialabel {
    public static boolean isObtainableInSurvival(@Nonnull Material material) {
        if (!material.isItem()) return false;
        String name = material.name();
        if (BukkitReflectionUtils.isAir(material)) return false;
        if (name.endsWith("_SPAWN_EGG")) return false;
        if (name.startsWith("INFESTED_")) return false;
        if (name.startsWith("LEGACY_")) return false; // Legacy items should not be obtainable
        if (name.endsWith("_COPPER_TRAPDOOR")) return false;
        if (name.endsWith("_COPPER_DOOR")) return false;
        if (name.endsWith("_COPPER_GRATE")) return false;
        if (name.endsWith("_COPPER_BLUB")) return false;
        if (name.contains("_WALL_")) return false;
        switch (name) { // Use name instead of enum its self, to prevent NoSuchFieldErrors in older versions where this specific enum does not exist
            case "CHAIN_COMMAND_BLOCK":
            case "REPEATING_COMMAND_BLOCK":
            case "COMMAND_BLOCK":
            case "COMMAND_BLOCK_MINECART":
            case "JIGSAW":
            case "STRUCTURE_BLOCK":
            case "STRUCTURE_VOID":
            case "BARRIER":
            case "BEDROCK":
            case "KNOWLEDGE_BOOK":
            case "DEBUG_STICK":
            case "END_PORTAL_FRAME":
            case "END_PORTAL":
            case "NETHER_PORTAL":
            case "END_GATEWAY":
            case "LAVA":
            case "WATER":
            case "LARGE_FERN":
            case "TALL_GRASS":
            case "TALL_SEAGRASS":
            case "PATH_BLOCK":
            case "CHORUS_PLANT":
            case "PETRIFIED_OAK_SLAB":
            case "FARMLAND":
            case "PLAYER_HEAD":
            case "GLOBE_BANNER_PATTERN":
            case "SPAWNER":
            case "AMETHYST_CLUSTER":
            case "BUDDING_AMETHYST":
            case "POWDER_SNOW":
            case "LIGHT":
            case "BUNDLE":
            case "REINFORCED_DEEPSLATE":
            case "FROGSPAWN":
                return false;
        }

        return true;
    }

}
