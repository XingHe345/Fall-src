package dev.Fall.module.impl.render;

import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.NumberSetting;

public class ScoreboardMod extends Module {

    public static final NumberSetting yOffset = new NumberSetting("Y Offset", 0, 250, 1, 5);
    public static final BooleanSetting textShadow = new BooleanSetting("Text Shadow", true);

    public ScoreboardMod() {
        super("Scoreboard", Category.RENDER, "Scoreboard preferences");
        this.addSettings(yOffset, textShadow);
        this.setToggled(true);
    }

}
