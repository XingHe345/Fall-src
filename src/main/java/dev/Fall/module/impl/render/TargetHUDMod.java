package dev.Fall.module.impl.render;

import dev.Fall.utils.tuples.Pair;
import dev.Fall.Fall;
import dev.Fall.event.impl.render.PreRenderEvent;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.event.impl.render.Render3DEvent;
import dev.Fall.event.impl.render.ShaderEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.impl.combat.KillAura;
import dev.Fall.module.impl.render.targethud.TargetHUD;
import dev.Fall.module.settings.ParentAttribute;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.objects.GradientColorWheel;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.ESPUtil;
import dev.Fall.utils.render.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;

public class TargetHUDMod extends Module {

    private final ModeSetting targetHud = new ModeSetting("Mode", "Fall",  "Fall");
    private final BooleanSetting trackTarget = new BooleanSetting("Track Target", false);
    private final ModeSetting trackingMode = new ModeSetting("Tracking Mode", "Middle", "Middle", "Top", "Left", "Right");

    public static boolean renderLayers = true;

    private final GradientColorWheel colorWheel = new GradientColorWheel();

    public TargetHUDMod() {
        super("Target HUD", Category.RENDER, "Displays info about the KillAura target");
        trackingMode.addParent(trackTarget, ParentAttribute.BOOLEAN_CONDITION);
        addSettings(targetHud, trackTarget, trackingMode, colorWheel.createModeSetting("Color Mode", "Dark"), colorWheel.getColorSetting());
        TargetHUD.init();
    }

    private EntityLivingBase target;
    private final Dragging drag = Fall.INSTANCE.createDrag(this, "targetHud", 300, 300);

    private final Animation openAnimation = new DecelerateAnimation(175, .5);

    private KillAura killAura;

    private Vector4f targetVector;

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (trackTarget.isEnabled() && target != null) {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                    if (target.equals(entityLivingBase)) {
                        targetVector = ESPUtil.getEntityPositionsOn2D(entity);
                    }
                }
            }

        }
    }

    @Override
    public void onPreRenderEvent(PreRenderEvent event) {
        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());
        drag.setWidth(currentTargetHUD.getWidth());
        drag.setHeight(currentTargetHUD.getHeight());


        if (killAura == null) {
            killAura = (KillAura) Fall.INSTANCE.getModuleCollection().get(KillAura.class);
        }


        if (!(mc.currentScreen instanceof GuiChat)) {
            if (!killAura.isEnabled()) {
                openAnimation.setDirection(Direction.BACKWARDS);
                if (openAnimation.finished(Direction.BACKWARDS)) {
                }
            }

            if (target == null && KillAura.target != null) {
                target = KillAura.target;
                openAnimation.setDirection(Direction.FORWARDS);


            } else if (KillAura.target == null || target != KillAura.target) {
                openAnimation.setDirection(Direction.BACKWARDS);
            }

            if (openAnimation.finished(Direction.BACKWARDS)) {
                target = null;
            }
        } else {
            openAnimation.setDirection(Direction.FORWARDS);
            target = mc.thePlayer;
        }


        if (target != null) {
            colorWheel.setColorsForMode("Dark", ColorUtil.brighter(new Color(30, 30, 30), .65f));
            colorWheel.setColors();
            currentTargetHUD.setColorWheel(colorWheel);
        }

    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        this.setSuffix(targetHud.getMode());
        boolean tracking = trackTarget.isEnabled() && targetVector != null && target != mc.thePlayer;

        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());

        if (target != null) {


            float trackScale = 1;
            float x = drag.getX(), y = drag.getY();
            if (tracking) {
                float newWidth = (targetVector.getZ() - targetVector.getX()) * 1.4f;
                trackScale = Math.min(1, newWidth / currentTargetHUD.getWidth());

                Pair<Float, Float> coords = getTrackedCoords();
                x = coords.getFirst();
                y = coords.getSecond();
            }


            RenderUtil.scaleStart(x + drag.getWidth() / 2f, y + drag.getHeight() / 2f,
                    (float) (.5 + openAnimation.getOutput().floatValue()) * trackScale);
            float alpha = Math.min(1, openAnimation.getOutput().floatValue() * 2);

            currentTargetHUD.render(x, y, alpha, target);


            RenderUtil.scaleEnd();
        }
    }


    @Override
    public void onShaderEvent(ShaderEvent e) {
        float x = drag.getX(), y = drag.getY();
        float trackScale = 1;
        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());
        if (trackTarget.isEnabled() && targetVector != null && target != mc.thePlayer) {
            Pair<Float, Float> coords = getTrackedCoords();
            x = coords.getFirst();
            y = coords.getSecond();

            float newWidth = (targetVector.getZ() - targetVector.getX()) * 1.4f;
            trackScale = Math.min(1, newWidth / currentTargetHUD.getWidth());
        }


        if (target != null) {

            boolean glow = e.getBloomOptions().getSetting("TargetHud").isEnabled();
            RenderUtil.scaleStart(x + drag.getWidth() / 2f, y + drag.getHeight() / 2f,
                    (float) (.5 + openAnimation.getOutput().floatValue()) * trackScale);
            float alpha = Math.min(1, openAnimation.getOutput().floatValue() * 2);

            currentTargetHUD.renderEffects(x, y, alpha, glow);

            RenderUtil.scaleEnd();
        }
    }


    @Override
    public void onEnable() {
        super.onEnable();

        target = null;
    }


    private Pair<Float, Float> getTrackedCoords() {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = drag.getWidth(), height = drag.getHeight();
        float x = targetVector.getX(), y = targetVector.getY();
        float entityWidth = (targetVector.getZ() - targetVector.getX());
        float entityHeight = (targetVector.getW() - targetVector.getY());
        float middleX = x + entityWidth / 2f - width / 2f;
        float middleY = y + entityHeight / 2f - height / 2f;
        switch (trackingMode.getMode()) {
            case "Middle":
                return Pair.of(middleX, middleY);
            case "Top":
                return Pair.of(middleX, y - (height / 2f + height / 4f));
            case "Left":
                return Pair.of(x - (width / 2f + width / 4f), middleY);
            default:
                return Pair.of(x + entityWidth - (width / 4f), middleY);
        }
    }


}
