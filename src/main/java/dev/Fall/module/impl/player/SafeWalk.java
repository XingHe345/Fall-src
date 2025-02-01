package dev.Fall.module.impl.player;

import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.event.impl.player.SafeWalkEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;

public final class SafeWalk extends Module {

    @Override
    public void onSafeWalkEvent(SafeWalkEvent e) {
        if(mc.thePlayer == null) return;
        e.setSafe(true);
    }

    @Override
    public void onMotionEvent(MotionEvent e){
    }
    public SafeWalk() {
        super("Safe Walk", Category.PLAYER, "prevents walking off blocks");
    }

}
