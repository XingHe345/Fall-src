package dev.Fall.ui.notifications;

import dev.Fall.module.impl.render.HUDMod;
import dev.Fall.utils.Utils;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import dev.Fall.utils.time.TimerUtil;
import dev.Fall.utils.tuples.Pair;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Getter
public class Notification implements Utils {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new DecelerateAnimation(250, 1);
    }


    public void drawDefault(float x, float y, float width, float height) {
        RoundedUtil.drawRound(x, y, width, height, 2, new Color(0, 0, 0, 150));
        RenderUtil.drawImage(new ResourceLocation("Fluid/从Styles偷的icon不要告诉袋子月求求你了/你猜。4.png"), x - 1.5f, y + 1f, 48 / 2f, 48 / 2f);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor_(x, y + height - 0.5f, (getTime() - (float)getTimerUtil().getTime()) / getTime() * width, 1.4);
        RoundedUtil.drawRound(x, y + height - 1.5f, (getTime() - (float)getTimerUtil().getTime()) / getTime() * width, 1.4f, 2, new Color(-1));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        FluidFont14.drawString(getTitle(), x + width - 2 - FluidFont14.getStringWidth(getTitle()), y + 2, new Color(150, 150, 150));
        FluidFont16.drawString(getDescription(), x + 25, y + 12, -1);
    }

    public void blurDefault(float x, float y, float width, float height, float alpha, boolean glow) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, getNotificationType().getColor(), glow ? .65f : 0), alpha);
        Pair<Color, Color> colors = HUDMod.getClientColors();
        RoundedUtil.drawRound(x, y, width, height, 2, Color.BLACK);
    }



}
