package dev.Fall.module.impl.movement;

import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ModeSetting;

public class LongJump extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Sillydog", "Sillydog");
    int jumptimes = 0;

    public LongJump() {
        super("Long Jump", Category.MOVEMENT, "Looooooog jump!");
        addSettings(mode);
    }


    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(mode.getMode());
        if(mode.is("Sillydog")) {
            if(event.isPre()) {
                if(jumptimes <= 2) {
                    if(mc.thePlayer.onGround) {
                        jumptimes ++;
                        mc.thePlayer.motionY = 0.02;
                        event.setOnGround(false);
                    }
                }
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        jumptimes = 0;
        mc.thePlayer.speedInAir = 0.02f;
        mc.timer.timerSpeed = 1.0f;
        mc.gameSettings.keyBindForward.pressed = false;
        super.onDisable();
    }
}
