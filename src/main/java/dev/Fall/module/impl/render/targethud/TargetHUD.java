package dev.Fall.module.impl.render.targethud;

import dev.Fall.utils.Utils;
import dev.Fall.utils.objects.GradientColorWheel;
import dev.Fall.utils.render.GLUtil;
import lombok.Getter;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;

import java.util.HashMap;

public abstract class TargetHUD implements Utils {

    protected GradientColorWheel colorWheel;
    private float width, height;
    @Getter
    private final String name;

    public TargetHUD(String name) {
        this.name = name;
    }

    public void setColorWheel(GradientColorWheel colorWheel) {
        this.colorWheel = colorWheel;
    }

    protected void renderPlayer2D(float x, float y, float width, float height, AbstractClientPlayer player) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, width, height, 64.0F, 64.0F);
        GLUtil.endBlend();
    }

    public abstract void render(float x, float y, float alpha, EntityLivingBase target);

    public abstract void renderEffects(float x, float y, float alpha, boolean glow);

    private static final HashMap<Class<? extends TargetHUD>, TargetHUD> targetHuds = new HashMap<>();

    public static TargetHUD get(String name) {
        return targetHuds.values().stream().filter(hud -> hud.getName().equals(name)).findFirst().orElse(null);
    }

    public static <T extends TargetHUD> T get(Class<T> clazz) {
        return (T) targetHuds.get(clazz);
    }

    public static void init() {
        targetHuds.put(OldFluidTargetHUD.class, new OldFluidTargetHUD());
//        targetHuds.put(CoolTargetHUD.class, new CoolTargetHUD());
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
