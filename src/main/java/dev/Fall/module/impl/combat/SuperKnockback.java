package dev.Fall.module.impl.combat;

import dev.Fall.event.impl.player.AttackEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;

public final class SuperKnockback extends Module {
    private boolean unsprint, wTap;

    public SuperKnockback() {
        super("WTap", Category.COMBAT, "Makes the player your attacking take extra knockback");
    }

    @Override
    public void onAttackEvent(AttackEvent event) {
        wTap = Math.random() * 100 < 95;

        if (!wTap) return;

        if (mc.thePlayer.isSprinting() || mc.gameSettings.keyBindSprint.isKeyDown()) {
            mc.gameSettings.keyBindSprint.pressed = true;
            unsprint = true;
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if(event.isPre()) {
            if (!wTap) return;

            if (unsprint) {
                mc.gameSettings.keyBindSprint.pressed = false;
                unsprint = false;
            }
        }
    }
}
