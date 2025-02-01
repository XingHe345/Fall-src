package dev.Fall.ui.musicplayer.utils;

import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.blur.GaussianBlur;
import dev.Fall.utils.render.blur.KawaseBloom;
import net.minecraft.client.shader.Framebuffer;

public class Blur {
    private static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public static void blur(Runnable connect, int radius, int compression) {
        GaussianBlur.startBlur();

        connect.run();

        GaussianBlur.endBlur(radius, compression);
    }
    public static boolean isMacOS(){
        return System.getProperty("os.name").startsWith("Mac OS");
    }
    public static void bloom(Runnable connect, int shadowRadius, int shadowOffset) {//3(1-8),1(1-10)
        if (isMacOS()) return;
        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);

        connect.run();

        stencilFramebuffer.unbindFramebuffer();

        KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, shadowRadius, shadowOffset);
    }
}
