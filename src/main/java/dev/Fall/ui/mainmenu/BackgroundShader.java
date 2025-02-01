package dev.Fall.ui.mainmenu;

import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.ShaderUtil;

public class BackgroundShader extends ShaderUtil {
    private float time;

    public BackgroundShader() {
        super("Fluid/Shaders/background.frag");
    }

    public void render(float width, float height) {
        this.init();
        this.setUniformf("iResolution", mc.displayWidth, mc.displayHeight);
        this.setUniformf("iTime",  this.time);
        ShaderUtil.drawQuads(width, height);

        this.unload();

        this.time += 0.001F * RenderUtil.deltaTime;
    }
}
