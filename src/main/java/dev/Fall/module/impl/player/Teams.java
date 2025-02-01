package dev.Fall.module.impl.player;

import dev.Fall.module.Category;
import dev.Fall.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class Teams extends Module {
    public Teams() {
        super("Teams", Category.MISC, "Teams");
    }

    public static boolean isOnSameTeam(Entity entity) {
        if (!Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().isEmpty() && Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().charAt(0) == '\247') {
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2
                    || entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2).equals(entity.getDisplayName().getUnformattedText().substring(0, 2))) {
                return true;
            }
        }
        return false;
    }
}
