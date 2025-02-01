package dev.Fall.module.impl.render;

import dev.Fall.commands.impl.FriendCommand;
import dev.Fall.event.impl.render.NametagRenderEvent;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.event.impl.render.Render3DEvent;
import dev.Fall.event.impl.render.ShaderEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.MultipleBoolSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.font.AbstractFontRenderer;
import dev.Fall.utils.render.*;
import dev.Fall.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class ESP2D extends Module {

    private final MultipleBoolSetting validEntities = new MultipleBoolSetting("Valid Entities",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Mobs", true));


    private final NumberSetting scale = new NumberSetting("Tag Scale", .75, 4, .35, .05);

    private final MultipleBoolSetting nametagSettings = new MultipleBoolSetting("Nametag Settings",
            new BooleanSetting("Formatted Tags", false),
            new BooleanSetting("Add PostProcessing", false),
            new BooleanSetting("Background", true),
            new BooleanSetting("Round", true));

    public ESP2D() {
        super("Name Tags", Category.RENDER, "Draws a box in 2D space around entitys");

        addSettings(validEntities, scale, nametagSettings);
    }


    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();

    @Override
    public void onNametagRenderEvent(NametagRenderEvent e) {
        e.cancel();
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && ESPUtil.isInView(entity)) {
                entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
            }
        }
    }

    @Override
    public void onShaderEvent(ShaderEvent e) {


        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(), y = pos.getY(), right = pos.getZ(), bottom = pos.getW();

            if (entity instanceof EntityLivingBase) {
                AbstractFontRenderer font = FluidBoldFont20;
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText()));
                StringBuilder text = new StringBuilder(
                        (FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : false ? "§c" : "§f") + name);
                double fontScale = scale.getValue();
                float middle = x + ((right - x) / 2);
                float textWidth = 0;
                double fontHeight;
                textWidth = font.getStringWidth(text.toString());
                middle -= (textWidth * fontScale) / 2f;
                fontHeight = font.getHeight() * fontScale;

                glPushMatrix();
                glTranslated(middle, y - (fontHeight + 2), 0);
                glScaled(fontScale, fontScale, 1);
                glTranslated(-middle, -(y - (fontHeight + 2)), 0);

                Color backgroundTagColor = false ? Color.RED : Color.BLACK;
                RenderUtil.resetColor();
                GLUtil.startBlend();
                if (nametagSettings.getSetting("Round").isEnabled()) {

                    RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                            (float) ((fontHeight / fontScale) + 4), 4, backgroundTagColor);
                } else {
                    Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                            (fontHeight / fontScale) + 4, backgroundTagColor.getRGB());
                }

                glPopMatrix();

            }
        }
    }


    private final NumberFormat df = new DecimalFormat("0.#");
    private final Color backgroundColor = new Color(10, 10, 10, 130);

    private Color firstColor = Color.BLACK, secondColor = Color.BLACK, thirdColor = Color.BLACK, fourthColor = Color.BLACK;


    @Override
    public void onRender2DEvent(Render2DEvent e) {
        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY(),
                    right = pos.getZ(),
                    bottom = pos.getW();

            if (entity instanceof EntityLivingBase) {
                AbstractFontRenderer font = FluidBoldFont20;
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;

                float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
                String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText()));
                StringBuilder text = new StringBuilder(
                        (FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : false ? "§c" : "§f") + name);

                double fontScale = scale.getValue();
                float middle = x + ((right - x) / 2);
                float textWidth;
                double fontHeight = font.getHeight() * fontScale;
                textWidth = font.getStringWidth(text.toString());
                middle -= (textWidth * fontScale) / 2f;

                glPushMatrix();
                glTranslated(middle, y - (fontHeight + 2), 0);
                glScaled(fontScale, fontScale, 1);
                glTranslated(-middle, -(y - (fontHeight + 2)), 0);


                if (nametagSettings.getSetting("Background").isEnabled()) {
                    Color backgroundTagColor = backgroundColor;
                    if (nametagSettings.getSetting("Round").isEnabled()) {
                        RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (float) ((fontHeight / fontScale) + 4), 4, backgroundTagColor);
                    } else {
                        Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (fontHeight / fontScale) + 4, backgroundTagColor.getRGB());
                    }
                }


                RenderUtil.resetColor();
                FluidBoldFont20.drawSmoothStringWithShadow(text.toString(), middle, (float) (y - (fontHeight + 5)), healthColor.getRGB());
                glPopMatrix();

            }

        }

    }

    private void gradientColorWheel(Pair<Color, Color> colors) {
        firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, colors.getFirst(), colors.getSecond(), false);
        secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90, colors.getFirst(), colors.getSecond(), false);
        thirdColor = ColorUtil.interpolateColorsBackAndForth(15, 180, colors.getFirst(), colors.getSecond(), false);
        fourthColor = ColorUtil.interpolateColorsBackAndForth(15, 270, colors.getFirst(), colors.getSecond(), false);
    }

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (validEntities.getSetting("Players").isEnabled() && entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }
        if (validEntities.getSetting("Animals").isEnabled() && entity instanceof EntityAnimal) {
            return true;
        }

        return validEntities.getSetting("mobs").isEnabled() && entity instanceof EntityMob;
    }


}
