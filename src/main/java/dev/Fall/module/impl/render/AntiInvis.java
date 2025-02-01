package dev.Fall.module.impl.render;

import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import net.minecraft.potion.Potion;

@SuppressWarnings("unused")
public final class AntiInvis extends Module {

    public AntiInvis() {
        super("Anti Invis", Category.RENDER, "Shows invisible people");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        mc.theWorld.playerEntities.stream()
                .filter(player -> player != mc.thePlayer && player.isPotionActive(Potion.invisibility))
                .forEach(player -> {
                    player.removePotionEffect(Potion.invisibility.getId());
                    player.setInvisible(false);
                });
    }

}
