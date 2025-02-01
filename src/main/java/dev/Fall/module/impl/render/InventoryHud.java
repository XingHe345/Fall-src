package dev.Fall.module.impl.render;

import dev.Fall.Fall;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.event.impl.render.ShaderEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.objects.GradientColorWheel;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class InventoryHud extends Module {
    private final Dragging dragging = Fall.INSTANCE.createDrag(this, "Inentory_HUD", 5, 150);
    private final GradientColorWheel colorWheel = new GradientColorWheel();

    public InventoryHud() {
        super("Inventory HUD", Category.RENDER, "Show your Inventory");
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), dragging.getHeight(), 2, colorWheel.getColor1());
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        ItemStack[] items = mc.thePlayer.inventory.mainInventory;
        dragging.setHeight(75);
        dragging.setWidth(162);
        GlStateManager.resetColor();
        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), dragging.getHeight(), 2, new Color(0, 0, 0, 150));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor_(dragging.getX() - 5, dragging.getY() - 5, dragging.getWidth() + 10, 20);
        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), 20, 2, new Color(0, 0, 0, 100));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        RenderUtil.drawImage(new ResourceLocation("Fluid/从Styles偷的icon不要告诉袋子月求求你了/你猜。3.png"), dragging.getX() + 2, dragging.getY() + 2f, 11, 11);
        FluidFont18.drawString("Inventory Hud", dragging.getX() + 15, dragging.getY() + 4, -1);
        for (ItemStack i : mc.thePlayer.inventory.mainInventory) {
            for (int size = items.length, item = 9; item < size; ++item) {
                final int slotX = (int) (dragging.getX() + (item) % 9 * 18);
                final int slotY = (int) (dragging.getY() + 17 + (item / 9 - 1) * 18);
                GlStateManager.resetColor();
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(items[item], slotX + 1, slotY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, items[item], slotX, slotY, null);
                RenderHelper.disableStandardItemLighting();
            }
        }
    }
}
