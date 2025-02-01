package dev.Fall.ui.mainmenu;

import dev.Fall.Fall;
import dev.Fall.intent.cloud.Cloud;
import dev.Fall.ui.Screen;
import dev.Fall.ui.altmanager.panels.LoginPanel;
import dev.Fall.ui.video.VideoPlayer;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.misc.*;
import dev.Fall.utils.render.*;
import dev.Fall.utils.render.blur.GaussianBlur;
import lombok.Getter;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import lombok.SneakyThrows;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomMainMenu extends GuiScreen {


    public static boolean animatedOpen = false;


    private final List<MenuButton> buttons = new ArrayList() {{
        add(new MenuButton("Single"));
        add(new MenuButton("Multi"));
        add(new MenuButton("Alt"));
        add(new MenuButton("Option"));
        add(new MenuButton("Exit"));
    }};

    private final List<TextButton> textButtons = new ArrayList() {{
        add(new TextButton("Group"));
        add(new TextButton("  Discord"));
    }};


    private final ResourceLocation backgroundResource = new ResourceLocation("Fluid/MainMenu/funny_1.png");
    private final ResourceLocation video = new ResourceLocation("Fluid/Video/DINO.mp4");
    private static boolean firstInit = false;

    @Override
    public void initGui() {
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


    @SneakyThrows
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        VideoPlayer videoPlayer = new VideoPlayer();
        ScaledResolution sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();
        RenderUtil.resetColor();
         RenderUtil.drawImage(backgroundResource, - mouseX / 9, - mouseY / 15, width + 142, height + 50);

        GaussianBlur.startBlur();
        RoundedUtil.drawRound(width / 2 - 150, height / 2 - 150 / 2, 300, 120, 4, new Color(0, 0, 0, 50));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor_(width / 2 - 150, height / 2 - 75, 300, 22);
        RoundedUtil.drawRound(width / 2 - 150, height / 2 - 75, 300, 25, 4, new Color(0, 0, 0, 50));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GaussianBlur.endBlur(20, 2);
        RoundedUtil.drawRound(width / 2 - 150, height / 2 - 150 / 2, 300, 120, 4, new Color(0, 0, 0, 50));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor_(width / 2 - 150, height / 2 - 75, 300, 22);
        RoundedUtil.drawRound(width / 2 - 150, height / 2 - 75, 300, 25, 4, new Color(0, 0, 0, 50));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        FluidBoldFont24.drawString("Fall Client", width / 2 - FluidBoldFont24.getStringWidth("Fall Client") / 2, height / 2 - 75 + 11f - FluidBoldFont24.getHeight() / 2, -1);
        //FluidBoldFont24.drawString("Loading...", width / 2 - FluidBoldFont24.getStringWidth("Loading...") / 2, height / 2 - FluidBoldFont24.getHeight() / 2 - 25, -1);



        //float outlineImgWidth = 688 / 2f;
        //float outlineImgHeight = 681 / 2f;
        GLUtil.startBlend();
        //RenderUtil.color(-1);
        //RenderUtil.drawImage(blurredRect, width / 2f - outlineImgWidth / 2f, height / 2f - outlineImgHeight / 2f,
        //        outlineImgWidth, outlineImgHeight);


        if (animatedOpen) {
                FluidFont80.drawStringWithShadow("Fall", width / 2f, height / 2f - 110, Color.WHITE.getRGB());
                FluidFont32.drawString(Fall.VERSION, width / 2f + FluidFont80.getStringWidth("Fall") / 2f - (FluidFont32.getStringWidth(Fall.VERSION) / 2f), height / 2f - 113, Color.WHITE.getRGB());
        }

        GL11.glEnable(GL11.GL_BLEND);


        StencilUtil.initStencilToWrite();

        RenderUtil.setAlphaLimit(13);
        buttons.forEach(MenuButton::drawRounded);

        RenderUtil.setAlphaLimit(0);
        StencilUtil.readStencilBuffer(1);


        float circleW = 174 / 2f;
        float circleH = 140 / 2f;
        ResourceLocation rs = new ResourceLocation("Fluid/MainMenu/circle-funny.png");
        mc.getTextureManager().bindTexture(rs);
        GLUtil.startBlend();
        RenderUtil.drawImage(rs, mouseX - circleW / 2f, mouseY - circleH / 2f, circleW, circleH);

        StencilUtil.uninitStencilBuffer();


        float buttonWidth = 50;
        float buttonHeight = 50;

        int count = 0;
        for (MenuButton button : buttons) {

            button.x = width / 2f + count - 135;
            button.y = ((height / 2f - buttonHeight / 2f)) - 5;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Single":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multi":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "Alt":
                        mc.displayGuiScreen(Fall.INSTANCE.getAltManager());
                        break;
                    case "Option":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "Exit":
                        mc.shutdown();
                        break;
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += buttonHeight + 5;
        }


        float buttonCount = 0;
        float buttonsWidth = (float) textButtons.stream().mapToDouble(TextButton::getWidth).sum();
        int buttonsSize = textButtons.size();
        buttonsWidth += FluidFont16.getStringWidth(" | ") * (buttonsSize - 1);

        int buttonIncrement = 0;
        for (TextButton button : textButtons) {
            button.x = width / 2f - buttonsWidth / 2f + buttonCount;
            button.y = (height / 2f) + 50;
            switch (button.text) {
                case "Group":
                    button.clickAction = () -> {
                        IOUtils.openLink("https://www.baidu.com/");
                    };
                    break;
                case "  Discord":
                    button.clickAction = () -> {
                        IOUtils.openLink("https://www.baidu.com/");
                    };
                    break;
            }

            button.addToEnd = (buttonIncrement != (buttonsSize - 1));

            button.drawScreen(mouseX, mouseY);


            buttonCount += button.getWidth() + FluidFont14.getStringWidth(" | ");
            buttonIncrement++;
        }

        //FluidBoldFont80.drawCenteredString("Fluid", width / 2f, height / 2f - 115, Color.WHITE.getRGB());
        //FluidFont32.drawString(Fluid.VERSION, width / 2f + FluidBoldFont80.getStringWidth("Fluid") / 2f - (FluidFont32.getStringWidth(Fluid.VERSION) / 2f), height / 2f - 116, Color.WHITE.getRGB());
//        mc.getTextureManager().bindTexture(new ResourceLocation("Fluid/light_logo.png"));
//        Gui.drawModalRectWithCustomSizedTexture(width / 2f - 66F, height / 2f - 142 , 0, 0, 135, 135, 135, 135);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        LoginPanel.cracked = Cloud.getApiKey() == null;
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        textButtons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onGuiClosed() {
        if (Fall.updateGuiScale) {
            mc.gameSettings.guiScale = Fall.prevGuiScale;
            Fall.updateGuiScale = false;
        }
    }

    private static class TextButton implements Screen {
        public float x, y;
        @Getter
        private final float width, height;
        public Runnable clickAction;
        private final String text;

        private final Animation hoverAnimation = new DecelerateAnimation(150, 1);

        public boolean addToEnd;

        public TextButton(String text) {
            this.text = text;
            width = FluidFont16.getStringWidth(text);
            height = FluidFont16.getHeight();
        }

        @Override
        public void initGui() {
            mc.getFramebuffer().bindFramebuffer(true);
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {

        }

        @Override
        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            FluidFont16.drawStringWithShadow(text, x, y - (height / 2f * hoverAnimation.getOutput().floatValue()), Color.WHITE.getRGB());
            if (addToEnd) {
                FluidFont16.drawStringWithShadow(" | ", x + width + 1, y, Color.WHITE.getRGB());
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            if (hovered && button == 0) {
                clickAction.run();
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {

        }
    }

}
