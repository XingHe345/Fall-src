package dev.Fall.ui.guilogin;

import dev.Fall.Fall;
import dev.Fall.intent.cloud.Cloud;
import dev.Fall.ui.Screen;
import dev.Fall.ui.altmanager.panels.LoginPanel;
import dev.Fall.ui.mainmenu.CustomMainMenu;
import dev.Fall.ui.mainmenu.MenuButton;
import dev.Fall.ui.video.VideoPlayer;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.misc.HoveringUtil;
import dev.Fall.utils.misc.IOUtils;
import dev.Fall.utils.misc.NetworkingUtils;
import dev.Fall.utils.render.GLUtil;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import dev.Fall.utils.render.StencilUtil;
import dev.Fall.utils.render.blur.GaussianBlur;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginMenu extends GuiScreen {
    private final List<MenuButton> buttons = new ArrayList() {{
        add(new MenuButton("Login"));
    }};
    private static boolean firstInit = false;
    private final ResourceLocation backgroundResource = new ResourceLocation("Fluid/MainMenu/AI.png");
    @Override
    public void initGui() {
       // Display.setTitle("Waiting for login");
        if (!firstInit) {
            NetworkingUtils.bypassSSL();
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            }
            firstInit = true;
        }

        if (mc.gameSettings.guiScale != 2) {
            Fall.prevGuiScale = mc.gameSettings.guiScale;
            Fall.updateGuiScale = true;
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
        buttons.forEach(MenuButton::initGui);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
    private static void start(){
        mc.displayGuiScreen(new CustomMainMenu());
    }
    @SneakyThrows
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();
        RenderUtil.drawImage(backgroundResource, - mouseX / 9, - mouseY / 15, width + 142, height + 50);
        RoundedUtil.drawRound(width / 2 - 65, height / 2 - 100, 130, 150, 4, new Color(195, 192, 192,200));
        float buttonWidth = 50;
        float buttonHeight = 20;

        int count = 0;
        for (MenuButton button : buttons) {

            button.x = width / 2f + count - 145 + 116;
            button.y = ((height / 2f - buttonHeight / 2f)) - 5 + 28;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Login":
                        mc.displayGuiScreen(new CustomMainMenu());
                        break;
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += buttonHeight + 5;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        LoginPanel.cracked = Cloud.getApiKey() == null;
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
