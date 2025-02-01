package dev.Fall.module.impl.render;

import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.ui.notifications.Notification;
import dev.Fall.ui.notifications.NotificationManager;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class NotificationsMod extends Module {
    public static final BooleanSetting toggleNotifications = new BooleanSetting("Show Toggle", true);

    public NotificationsMod() {
        super("Notifications", Category.RENDER, "Allows you to customize the client notifications");
        this.addSettings(toggleNotifications);
        if (!enabled) this.toggleSilent();
    }

    public void render() {
        GlStateManager.resetColor();
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);

        NotificationManager.setToggleTime(2.5f);

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }
            float x, y;
            animation.setDuration(250);
            actualOffset = 8;
            notificationHeight = 27;
            notificationWidth = (int) Math.max(FluidFont14.getStringWidth(notification.getTitle()), FluidFont16.getStringWidth(notification.getDescription())) + 35;


            x = sr.getScaledWidth() - (notificationWidth + 5) * (float) animation.getOutput().floatValue();
            y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * (float) GuiChat.openingAnimation.getOutput().floatValue()));
            notification.drawDefault(x, y, notificationWidth, notificationHeight);


            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }

    public void renderEffects(boolean glow) {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);

        GlStateManager.resetColor();
        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            actualOffset = 8;
            notificationHeight = 27;
            notificationWidth = (int) Math.max(FluidFont14.getStringWidth(notification.getTitle()), FluidFont16.getStringWidth(notification.getDescription())) + 35;

            x = sr.getScaledWidth() - (notificationWidth + 5) * (float) animation.getOutput().floatValue();
            y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * (float) GuiChat.openingAnimation.getOutput().floatValue()));

            notification.blurDefault(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue(), true);


            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }


}
