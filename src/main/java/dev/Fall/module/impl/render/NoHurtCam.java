package dev.Fall.module.impl.render;

import dev.Fall.event.impl.render.HurtCamEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("No HurtCam", Category.RENDER, "removes shaking after being hit");
    }

    @Override
    public void onHurtCamEvent(HurtCamEvent e) {
        e.cancel();
    }

}
