package dev.Fall.module.impl.render;

import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.ui.clickguis.compact.CompactClickgui;
import dev.Fall.ui.clickguis.fall.FallClickgui;
import org.lwjgl.input.Keyboard;

public class ClickGUIMod extends Module {

    public static final ModeSetting clickguiMode = new ModeSetting("ClickGui", "Panel", "Panel", "Fall");

    public static final CompactClickgui fiveGoldClickgui = new CompactClickgui();
    public static final FallClickgui fallclickgui = new FallClickgui();

    private int activeCategory = 0;
    private Category activeCategory2 = Category.COMBAT;

    public static int prevGuiScale;

    public ClickGUIMod() {
        super("ClickGUI", Category.RENDER, "Displays modules");

        this.addSettings(clickguiMode);
        this.setKey(Keyboard.KEY_RSHIFT);
    }

    public void toggle() {
        this.onEnable();
    }

    public void onEnable() {

        switch (clickguiMode.getMode()) {
            case "Panel":
                mc.displayGuiScreen(fiveGoldClickgui);
                break;
            case "Fall":
                mc.displayGuiScreen(fallclickgui);
                break;
        }
    }


    public int getActiveCategoryy() {
        return activeCategory;
    }

    public Category getActiveCategory() {
        return activeCategory2;
    }

    public void setActiveCategory(int activeCategory) {
        this.activeCategory = activeCategory;
    }

    public void setActiveCategory(Category activeCategory) {
        this.activeCategory2 = activeCategory;
    }

}
