package dev.Fall.module.impl.render;


import dev.Fall.Fall;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.event.impl.render.ShaderEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.EaseBackIn;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.objects.GradientColorWheel;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FPSCounter extends Module {

    private final Dragging dragging = Fall.INSTANCE.createDrag(this, "render", 5, 150);
    private final NumberSetting fontsize = new NumberSetting("Font size", 14, 26, 12, 2);
    private final NumberSetting radius = new NumberSetting("Radius", 5, 10, 0, 0.5);
    private static final NumberSetting opacity = new NumberSetting("Opacity", .5, 1, 0, .05);
    private final EaseBackIn animation = new EaseBackIn(200, 1.0, 1.3f);
    public FPSCounter() {
        super("FPS Counter", Category.RENDER, "show your fps");
        addSettings(fontsize, radius, opacity);
    }
    private final GradientColorWheel colorWheel = new GradientColorWheel();
    private final BooleanSetting seprateMotionGraph = new BooleanSetting("r", false);

    @Override
    public void onShaderEvent(ShaderEvent e) {
        float height = FluidFont.size(fontsize.getValue().intValue()).getHeight() + 9;
        float width = FluidFont.size(fontsize.getValue().intValue()).getStringWidth(mc.getDebugFPS() + " FPS");
        float x = this.dragging.getX(), y = this.dragging.getY();
        boolean seperated = seprateMotionGraph.isEnabled();
        if (e.getBloomOptions().getSetting("FPS Counter").isEnabled()) {
            RoundedUtil.drawGradientRound(x, y, width + 10, height, radius.getValue().floatValue(), colorWheel.getColor1(), colorWheel.getColor4(), colorWheel.getColor2(), colorWheel.getColor3());
            if (seperated) {
                RoundedUtil.drawGradientRound(dragging.getX(), dragging.getY(),
                        dragging.getWidth(), dragging.getHeight(), radius.getValue().floatValue(), colorWheel.getColor1(),
                        colorWheel.getColor4(), colorWheel.getColor2(), colorWheel.getColor3());
            }

        } else {
            RoundedUtil.drawRound(x, y, width + 10, height, radius.getValue().floatValue(), Color.BLACK);

            if (seperated) {
                RoundedUtil.drawRound(dragging.getX(), dragging.getY(),
                        dragging.getWidth(), dragging.getHeight(), radius.getValue().floatValue(), Color.BLACK);
            }
        }

    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        float height = FluidFont.size(fontsize.getValue().intValue()).getHeight() + 9;
        float width = FluidFont.size(fontsize.getValue().intValue()).getStringWidth(mc.getDebugFPS() + " FPS");
        float x = dragging.getX(), y = dragging.getY();
        dragging.setWidth(width + 8);
        dragging.setHeight(height);
        GL11.glPopMatrix();
        GlStateManager.color(1, 1, 1, 1);
        Color color = ColorUtil.applyOpacity(Color.BLACK, opacity.getValue().floatValue());
        //背景框
        RoundedUtil.drawRound(x, y, width + 10, height, radius.getValue().floatValue(), color);
        //信息
        FluidFont.size(fontsize.getValue().intValue()).drawString(mc.getDebugFPS() + "", x + 5, y + 5f, Color.white);
        FluidFont.size(fontsize.getValue().intValue()).drawString(" FPS", x + 5 + FluidFont.size(fontsize.getValue().intValue()).getStringWidth(mc.getDebugFPS() + ""), y + 5f, new Color(0, 133, 250, 255));
    }
}
