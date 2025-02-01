package dev.Fall.ui.altmanager;

import dev.Fall.ui.Screen;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height;


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(27));
    }


}
