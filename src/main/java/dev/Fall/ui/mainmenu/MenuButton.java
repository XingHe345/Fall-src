package dev.Fall.ui.mainmenu;

import dev.Fall.ui.Screen;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.misc.HoveringUtil;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;

import java.awt.*;

public class MenuButton implements Screen {

    public final String text;
    private Animation hoverAnimation;
    public float x, y, width, height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }


    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(200, 1);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {

        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);

        RoundedUtil.drawRound(x, y, width, height, 3, new Color(0, 0, 0, 50));
        RenderUtil.color(-1);

        FluidFont16.drawCenteredString(text, x + width / 2f, y + FluidFont16.getMiddleOfBox(height), -1);

    }


    public void drawRounded() {
        RoundedUtil.drawRound(x, y, width, height, 4, new Color(0, 0, 0, 50));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if(hovered) {
            clickAction.run();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
