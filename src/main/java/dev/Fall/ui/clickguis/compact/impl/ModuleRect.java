package dev.Fall.ui.clickguis.compact.impl;

import dev.Fall.utils.render.RoundedUtil;
import dev.Fall.utils.tuples.Pair;
import dev.Fall.Fall;
import dev.Fall.module.Module;
import dev.Fall.module.impl.render.HUDMod;
import dev.Fall.ui.Screen;
import dev.Fall.ui.sidegui.utils.TooltipObject;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.misc.HoveringUtil;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ModuleRect implements Screen {
    @Getter
    @Setter
    private int searchScore;
    public final Module module;
    private final Animation enableAnimation = new DecelerateAnimation(150, 1);
    private final SettingComponents settingComponents;
    public float x, y, width, height;
    public float rectHeight;
    public float rectWidth;
    public Module binding;
    public boolean typing;
    public final TooltipObject tooltipObject = new TooltipObject();
    private final TimerUtil timerUtil = new TimerUtil();

    public ModuleRect(Module module) {
        this.module = module;
        settingComponents = new SettingComponents(module);
    }

    @Override
    public void initGui() {
        settingComponents.initGui();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (binding != null) {
            if (keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_DELETE)
                binding.getKeybind().setCode(Keyboard.KEY_NONE);
            else
                binding.getKeybind().setCode(keyCode);
            binding = null;
        } else {
            settingComponents.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        typing = false;
        RoundedUtil.drawRound(x, y, rectWidth, rectHeight + 20, 5, new Color(0, 0, 0, 50));
        float o = (float) enableAnimation.getOutput().floatValue();
        if(module.isEnabled()) {
            //RoundedUtil.drawGradientHorizontal(x, y, neverloseFont.size(20).getStringWidth(module.getName()) - 5, 20, 3, new Color(ColorUtil.interpolateColor(new Color(107, 56, 45, 255), new Color(77, 100, 55, 255),  o)), new Color(0, 0, 0, 0));
            RoundedUtil.drawRound(x, y, 2.5f, 20, 1, new Color(ColorUtil.interpolateColor(new Color(8, 150, 250, 0), new Color(8, 150, 250), o)));
        }
        HUDMod hudMod = Fall.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        Pair<Color, Color> colors = HUDMod.getClientColors();

        if (binding != null && !typing) {
            typing = true;
        }


        TAHOMA16.drawString(module.getName(), x + 5, y + neverloseFont.size(20).getMiddleOfBox(20), -1);



        boolean hoveringModule = HoveringUtil.isHovering(x, y, width, 20, mouseX, mouseY);

        if (!hoveringModule) {
            timerUtil.reset();
        }

        tooltipObject.setTip(module.getDescription());
        tooltipObject.setRound(false);
        tooltipObject.setHovering(timerUtil.hasTimeElapsed(900));


        Color bindRect = new Color(64, 68, 75);



        enableAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);


//        RenderUtil.fakeCircleGlow(x + rectWidth - 10, y + 10, (float) (2 + (5 * o)), accentColor, 115);

        settingComponents.x = x;
        settingComponents.y = y + 20;
        settingComponents.rectWidth = rectWidth;
        settingComponents.drawScreen(mouseX, mouseY);

        if (!typing) {
            typing = settingComponents.typing;
        }

        rectHeight = settingComponents.size > 0 ? settingComponents.size : 0;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float bindWidth = FluidFont14.getStringWidth(Keyboard.getKeyName(module.getKeybind().getCode())) + 4;
        boolean hovered = HoveringUtil.isHovering(x + FluidFont20.getStringWidth(module.getName()) + 13, y + 6, bindWidth, 8, mouseX, mouseY);
        if (!hovered && HoveringUtil.isHovering(x, y, rectWidth, 20, mouseX, mouseY)) {
            if (button == 0) {
                module.toggleSilent();
            }
        }
        settingComponents.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        settingComponents.mouseReleased(mouseX, mouseY, state);
    }

}
