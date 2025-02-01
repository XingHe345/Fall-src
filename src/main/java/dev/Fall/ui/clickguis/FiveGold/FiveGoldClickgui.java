package dev.Fall.ui.clickguis.FiveGold;

import dev.Fall.module.Category;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class FiveGoldClickgui extends GuiScreen {
    private static float rectWidth = 450;
    private static float rectHeight = 350;
    private static float x = 50;
    private static float y = 50;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RoundedUtil.drawRound(x, y, rectWidth, rectHeight, 2, new Color(25, 25, 25));
        RenderUtil.drawImage(new ResourceLocation("Fluid/Logo.png"), x + 2, x + 2, 24, 29);
        RoundedUtil.drawRound(x, y + 32, rectWidth, 0.5f, 0, new Color(0, 0, 0));
        RoundedUtil.drawRound(x + 25, y + 32.5f, 0.5f, rectHeight - 32.5f, 0, new Color(0, 0, 0));
        int i = 0;
        for (Category cat : Category.values()) {
            i ++;
            //FluidFont14.drawString(cat.name().toLowerCase(), x + (20 - FluidFont14.getStringWidth(cat.name().toLowerCase()) / 2), (y + 34) + (i * 30), -1);
            if(cat.name().equalsIgnoreCase("scripts")) return;
            iconFont35.drawString(cat.icon, x + 2, y + 20 + (i * 25), -1);
        }
    }
}
