package dev.Fall.module.impl.render.targethud;

import dev.Fall.utils.animations.ContinualAnimation;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.text.DecimalFormat;

public class OldFluidTargetHUD extends TargetHUD {
    private final ContinualAnimation animation = new ContinualAnimation();


    public OldFluidTargetHUD() {
        super("Fall");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        float   wdith = FluidBoldFont24.getStringWidth(target.getName()) + 41;
        float height = 40;
        setWidth(wdith);
        setHeight(height);
        RoundedUtil.drawRound(x, y, wdith, height, 3, new Color(0, 0, 0, 150));
        RenderUtil.drawHead(target.getLocationSkin(), x + 2.5f, y + 2.5f, 35, 35, 4,255);
        FluidBoldFont24.drawString(target.getName(), x + 40, y + 3, -1);
        FluidFont16.drawString(new DecimalFormat("0.00").format(target.getHealth()) + " HP", x + 40, y + 18, -1);
        RoundedUtil.drawRound(x + 40, y + height - 11, wdith - 42.5f, 8, 2, new Color(0, 0, 0, 100));
        RoundedUtil.drawRound(x + 40, y + height - 11, (target.getHealth() / target.getMaxHealth()) * (wdith - 42.5f), 8, 2, new Color(-1));
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 4, ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

}
