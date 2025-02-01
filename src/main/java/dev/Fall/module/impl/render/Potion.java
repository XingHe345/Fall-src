package dev.Fall.module.impl.render;


import dev.Fall.Fall;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.utils.animations.ContinualAnimation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.EaseBackIn;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.objects.GradientColorWheel;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Potion extends Module {

    private final Dragging dragging = Fall.INSTANCE.createDrag(this, "Potion", 5, 150);
    private int maxString = 0;
    private final Map<Integer, Integer> potionMaxDurations = new HashMap<Integer, Integer>();
    private final ContinualAnimation widthanimation = new ContinualAnimation();
    private final ContinualAnimation heightanimation = new ContinualAnimation();
    private final EaseBackIn animation = new EaseBackIn(200, 1.0, 1.3f);
    List<PotionEffect> effects = new ArrayList<PotionEffect>();
    public Potion() {
        super("Potion", Category.RENDER, "show your effet");
    }
    private final GradientColorWheel colorWheel = new GradientColorWheel();
    private final BooleanSetting seprateMotionGraph = new BooleanSetting("r", false);
    private String get(PotionEffect potioneffect) {
        net.minecraft.potion.Potion potion = net.minecraft.potion.Potion.potionTypes[potioneffect.getPotionID()];
        String s1 = I18n.format(potion.getName(), new Object[0]);
        s1 = s1 + " " + this.intToRomanByGreedy(potioneffect.getAmplifier() + 1);
        return s1;
    }

    public int getTotalHeight() {
        int h2 = mc.thePlayer.getActivePotionEffects().size() == 0 ? 16 : (mc.thePlayer.getActivePotionEffects().size() == 1 ? 52 : (mc.thePlayer.getActivePotionEffects().size() == 2 ? 38 : (mc.thePlayer.getActivePotionEffects().size() == 3 ? 33 : (mc.thePlayer.getActivePotionEffects().size() == 4 ? 31 : (mc.thePlayer.getActivePotionEffects().size() == 5 ? 29 : 28)))));
        return h2 * mc.thePlayer.getActivePotionEffects().size();
    }

    private String intToRomanByGreedy(int num) {
        int[] values = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i2 = 0; i2 < values.length && num >= 0; ++i2) {
            while (values[i2] <= num) {
                num -= values[i2];
                stringBuilder.append(symbols[i2]);
            }
        }
        return stringBuilder.toString();
    }
    @Override
    public void onRender2DEvent(Render2DEvent event) {
        this.effects = mc.thePlayer.getActivePotionEffects().stream().sorted(Comparator.comparingInt(it -> (int) FluidFont18.getStringWidth(this.get((PotionEffect)it)))).collect(Collectors.toList());
        int x2 = (int) dragging.getX();
        int y2 = (int) dragging.getY();
        int offsetX = 21;
        int offsetY = 14;
        int i2 = 16;
        ArrayList<Integer> needRemove = new ArrayList<Integer>();
        for (Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(net.minecraft.potion.Potion.potionTypes[entry.getKey()]) != null) continue;
            needRemove.add(entry.getKey());
        }
        RoundedUtil.drawRound(x2, y2 - 25 /*+ i2 - offsetY*/, (int)this.widthanimation.getOutput(), getTotalHeight(), 4.0f, new Color(19, 19, 19, 150));
        Iterator iterator = needRemove.iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            this.potionMaxDurations.remove(id);
        }
        for (PotionEffect effect : this.effects) {
            if (this.potionMaxDurations.containsKey(effect.getPotionID()) && this.potionMaxDurations.get(effect.getPotionID()) >= effect.getDuration()) continue;
            this.potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
        }
        float width = !this.effects.isEmpty() ? 125.0f : 0.0f;
        float height = this.effects.size() * 25;
        this.widthanimation.animate(width, 20);
        this.heightanimation.animate(height, 20);
        if (mc.currentScreen instanceof GuiChat && this.effects.isEmpty()) {
            this.animation.setDirection(Direction.FORWARDS);
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            this.animation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.scaleStart(x2 + 50, y2 + 15, (float) this.animation.getOutput().floatValue());
        FluidFont18.drawStringWithShadow("Potion Example", (float)x2 + 52.0f - (float)(FluidFont18.getStringWidth("Potion Example") / 2), y2 + 18 - FluidFont18.getHeight() / 2, new Color(255, 255, 255, 60).getRGB());
        RenderUtil.scaleEnd();
        if (this.effects.isEmpty()) {
            this.maxString = 0;
        }
        if (!this.effects.isEmpty()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableLighting();
            for (PotionEffect potioneffect : this.effects) {
                net.minecraft.potion.Potion potion = net.minecraft.potion.Potion.potionTypes[potioneffect.getPotionID()];
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                if (potion.hasStatusIcon()) {
                    mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                    int i1 = potion.getStatusIconIndex();
                    GlStateManager.enableBlend();
                    mc.ingameGUI.drawTexturedModalRect(x2 + offsetX - 17, y2 + i2 - offsetY - 1, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }
                FluidFont18.drawString("Potion", x2 + 21, y2 - 19, Color.WHITE.getRGB(), true);
                float potionDurationRatio = (float)potioneffect.getDuration() / (float)(this.potionMaxDurations.get(potioneffect.getPotionID()) != null ? this.potionMaxDurations.get(potioneffect.getPotionID()) : 1);
                String s2 = net.minecraft.potion.Potion.getDurationString(potioneffect);
                String s1 = this.get(potioneffect);
                mc.fontRendererObj.drawStringWithShadow(s1, x2 + offsetX + 3, y2 + i2 - offsetY - 1, -1);
                int finalI = i2;
                RoundedUtil.drawGradientCornerLR(x2 + offsetX + 3, y2 + finalI + 11 - offsetY - 1, 90.0f * potionDurationRatio, 2.0f, 0.0f, HUDMod.color1.getColor(), HUDMod.color2.getColor());
                i2 += 23;
                if (this.maxString >= mc.fontRendererObj.getStringWidth(s1)) continue;
                this.maxString = (int) mc.fontRendererObj.getStringWidth(s1);
            }
        }
    }

    public void drawLine(double x, double y, double width, double height, Color color) {
        Gui.drawRect(x, y, x + width, y + height, color.getRGB());
    }
}
