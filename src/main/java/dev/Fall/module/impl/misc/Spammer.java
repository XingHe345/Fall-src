package dev.Fall.module.impl.misc;

import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.MultipleBoolSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.module.settings.impl.StringSetting;
import dev.Fall.utils.misc.MathUtils;
import dev.Fall.utils.time.TimerUtil;

public final class Spammer extends Module {

    private final StringSetting text = new StringSetting("Text");
    private final NumberSetting delay = new NumberSetting("Delay", 100, 10000, 100, 1);
    private final MultipleBoolSetting settings = new MultipleBoolSetting("Settings",
            new BooleanSetting("AntiSpam", false),
            new BooleanSetting("Bypass", false));
    private final TimerUtil delayTimer = new TimerUtil();

    @Override
    public void onMotionEvent(MotionEvent event) {
        String spammerText = text.getString();

        if (spammerText != null && delayTimer.hasTimeElapsed(settings.getSetting("Bypass").isEnabled() ? 2000 : delay.getValue().longValue())) {

            if (settings.getSetting("AntiSpam").isEnabled()) {
                spammerText += " " + MathUtils.getRandomInRange(10, 100000);
            }

            mc.thePlayer.sendChatMessage(spammerText);
            delayTimer.reset();
        }
    }

    public Spammer() {
        super("Spammer", Category.MISC, "Spams in chat");
        this.addSettings(text, delay, settings);
    }

}
