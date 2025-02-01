package dev.Fall.module.impl.render.wings;

import dev.Fall.event.impl.render.RendererLivingEntityEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ColorSetting;
import dev.Fall.module.settings.impl.NumberSetting;

import java.awt.*;

/**
 * @author senoe
 * @since 6/2/2022
 */
public class DragonWings extends Module {

    private final NumberSetting scale = new NumberSetting("Scale", 1, 1.25, 0.75, 0.25);
    private final ColorSetting color = new ColorSetting("Color", Color.WHITE);
    private final WingModel model = new WingModel();

    public DragonWings() {
        super("Dragon Wings", Category.RENDER, "gives you dragon wings");
        addSettings(scale, color);
    }

    @Override
    public void onRendererLivingEntityEvent(RendererLivingEntityEvent event) {
        if (event.isPost() && event.getEntity() == mc.thePlayer && !mc.thePlayer.isInvisible()) {
            model.renderWings(mc.thePlayer, event.getPartialTicks(), scale.getValue(), color.getColor());
        }
    }

}
