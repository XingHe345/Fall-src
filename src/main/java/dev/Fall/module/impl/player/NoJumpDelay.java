package dev.Fall.module.impl.player;

import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("No Jump Delay", Category.PLAYER, "Cancel jump delay");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        mc.thePlayer.jumpTicks = 0;
    }
}
