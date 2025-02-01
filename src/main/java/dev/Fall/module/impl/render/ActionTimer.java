package dev.Fall.module.impl.render;

import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.ui.notifications.NotificationManager;
import dev.Fall.ui.notifications.NotificationType;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemFood;
import java.awt.*;

public class ActionTimer extends Module {

        private final BooleanSetting showBackground = new BooleanSetting("Show Background", true);
        private final BooleanSetting roundCorners = new BooleanSetting("Round Corners", true);
        private final BooleanSetting eatFood = new BooleanSetting("Eat Food", true);
        private long eatStartTime = 0;
        private boolean isEating = false;

        private boolean hasEaten = false;

    public ActionTimer() {
        super("ActionTimer",Category.RENDER,"ActionTimer");
    }

    @Override
        public void onRender2DEvent(Render2DEvent e) {
            //if (isNull()) return;

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();

            int x = screenWidth / 2 - 80;
            int y = screenHeight / 2 + 10;
            float width = 160;
            float height = 5;
            float radius = roundCorners.getConfigValue() ? 2 : 0;

            if (eatFood.getConfigValue() && mc.thePlayer.getHeldItem() != null) {
                if (mc.thePlayer.isEating() && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
                    if (!isEating) {
                        isEating = true;
                        eatStartTime = System.currentTimeMillis();
                        hasEaten = false;
                    }

                    float timerSpeed = mc.timer.timerSpeed;
                    long adjustedDuration = (long) (1500 / timerSpeed);
                    long elapsedTime = System.currentTimeMillis() - eatStartTime;
                    float progress = Math.min(1.0f, elapsedTime / (float) adjustedDuration);

                    if (showBackground.getConfigValue()) {
                        RoundedUtil.drawRound(x, y, width, height, radius, new Color(0, 0, 0, 120));
                    }

                    float fillWidth = width * progress;
                    RoundedUtil.drawRound(x, y, fillWidth, height, radius, new Color(240, 240, 240, 200));

                    if (elapsedTime >= adjustedDuration && !hasEaten) {
                        hasEaten = true;
                        NotificationManager.post(NotificationType.SUCCESS,"Eating Complete", "You have finished eating.", 1);
                    }
                } else {
                    isEating = false;
                    hasEaten = false;
                }
            }
        }
    }

