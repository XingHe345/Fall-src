package dev.Fall.ui;

import dev.Fall.utils.Utils;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.font.CustomFont;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class SplashScreen implements Utils {

    private static int currentProgress;

    // Background texture
    private static ResourceLocation splash;
    private static Framebuffer framebuffer;


//    static ResourceLocation image = new ResourceLocation("Fluid/splashscreen.png");

    private static int count;


    public static void continueCount() {
        continueCount(true);
    }

    public static void continueCount(boolean continueCount) {
        drawSplash();
        if(continueCount){
            count++;
        }
    }

    /**
     * Render the splash screen background
     */
    private static void drawSplash() {

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);



        // Create the projected image to be rendered
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();


        GlStateManager.color(0, 0, 0, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawSplashBackground(sr.getScaledWidth(), sr.getScaledHeight(), 1);

        RenderUtil.resetColor();
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtil.setAlphaLimit(0);

        CustomFont fr = FluidBoldFont80;

        if(count > 3){
            count = 0;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++){
            sb.append(".");
        }

//
//        fr.drawCenteredString("Fluid5.2" + sb, sr.getScaledWidth() / 2f, fr.getMiddleOfBox(sr.getScaledHeight()), -1);
        // Unbind the width and height as it's no longer needed
        framebuffer.unbindFramebuffer();

        // Render the previously used frame buffer
        framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

        // Update the texture to enable alpha drawing
        RenderUtil.setAlphaLimit(1);

        // Update the users screen
        Minecraft.getMinecraft().updateDisplay();
    }


    private static Animation fadeAnim;
    private static Animation moveAnim;
    private static Animation versionAnim;
    private static Animation progressAnim;
    private static Animation progress2Anim;
    static int w = 0;

    private static void drawScreen(float width, float height) {
        Gui.drawRect2(0, 0, width, height, Color.BLACK.getRGB());
        if (fadeAnim == null) {
            fadeAnim = new DecelerateAnimation(600, 1);
            moveAnim = new DecelerateAnimation(600, 1);
        }

        drawSplashBackground(width, height, 1);

        CustomFont fr = FluidBoldFont80;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(".");
        }

        RoundedUtil.drawRound(width / 2 - 150, height / 2 - 150 / 2, 300, 120, 4, new Color(0, 0, 0, 50));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor_(width / 2 - 150, height / 2 - 75, 300, 22);
        RoundedUtil.drawRound(width / 2 - 150, height / 2 - 75, 300, 25, 4, new Color(0, 0, 0, 50));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        FluidBoldFont24.drawString("Fall Client", width / 2 - FluidBoldFont24.getStringWidth("Fall Client") / 2, height / 2 - 75 + 11f - FluidBoldFont24.getHeight() / 2, -1);
        FluidBoldFont24.drawString("Loading...", width / 2 - FluidBoldFont24.getStringWidth("Loading...") / 2, height / 2 - FluidBoldFont24.getHeight() / 2 - 25, -1);
        if(w < 194) w ++;
        RoundedUtil.drawRoundOutline(width / 2 - 200 / 2, height / 2 - 15 / 2, 200, 15, 3, 1, new Color(0, 0, 0, 0), new Color(255, 255, 255));
        RoundedUtil.drawRound(width / 2 - 200 / 2 + 3, height / 2 - 15 / 2 + 3, w, 9, 1.5f, new Color(255, 255, 255));




        if (progressAnim == null) {
            if (moveAnim.isDone()) {
                progressAnim = new DecelerateAnimation(250, 1);
                progress2Anim = new DecelerateAnimation(1800, 1);
            }
        } else {
            if (progressAnim.finished(Direction.FORWARDS) && progress2Anim.finished(Direction.FORWARDS)) {
                progressAnim.changeDirection();
            }

        }


    }

    public static void drawScreen() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);


        while (progressAnim == null || !progressAnim.finished(Direction.BACKWARDS)) {
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            // Create the projected image to be rendered
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();


            GlStateManager.color(0, 0, 0, 0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            drawScreen(sr.getScaledWidth(), sr.getScaledHeight());

            // Unbind the width and height as it's no longer needed
            framebuffer.unbindFramebuffer();

            // Render the previously used frame buffer
            framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

            // Update the texture to enable alpha drawing
            RenderUtil.setAlphaLimit(1);

            // Update the users screen
            Minecraft.getMinecraft().updateDisplay();
        }
    }


    public static void drawSplashBackground(float width, float height, float alpha) {
        RenderUtil.resetColor();
        GlStateManager.color(1, 1, 1, alpha);
        mc.getTextureManager().bindTexture(new ResourceLocation("Fluid/splashscreen.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);


    }


}
