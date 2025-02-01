package dev.Fall.module.impl.render;

import dev.Fall.Fall;
import dev.Fall.event.impl.player.UpdateEvent;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.event.impl.render.ShaderEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.*;
import dev.Fall.network.user.User;
import dev.Fall.network.user.UserManager;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.objects.GradientColorWheel;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import dev.Fall.utils.render.Theme;
import dev.Fall.utils.tuples.Pair;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class HUDMod extends Module {
    private final Dragging dragging = Fall.INSTANCE.createDrag(this, "logo", 5, 3);
    public static final ModeSetting mod = new ModeSetting("Mod","Image1","Image1", "Image2", "Logo", "New");
    private final NumberSetting colorIndex = new NumberSetting("Color Seperation", 20, 100, 5, 1);
    private final NumberSetting colorSpeed = new NumberSetting("Color Speed", 15, 30, 2, 1);
    private static final NumberSetting opacity = new NumberSetting("Opacity", 0.75, 1, 0, .05);
    private final NumberSetting radius = new NumberSetting("Radius", 3.0, 10, 0, 0.5);
    private final NumberSetting radius2 = new NumberSetting("Radius-text", 5, 10, 0, 0.5);
    public static final ColorSetting color1 = new ColorSetting("Color 1", new Color(0xffa028d4));
    public static final ColorSetting color2 = new ColorSetting("Color 2", new Color(0xff0008ff));
    public static final ColorSetting color3 = new ColorSetting("Color 3", new Color(0xff0008ff));
    public static final ColorSetting color4 = new ColorSetting("Color 4", new Color(0xff0008ff));
    public static final ModeSetting theme = Theme.getModeSetting("Global Color", "Custom");


    public HUDMod() {
        super("Interface", Category.RENDER, "customizes the client's appearance");
        color1.addParent(theme, modeSetting -> modeSetting.is("Custom"));
        color2.addParent(theme, modeSetting -> modeSetting.is("Custom") && !color1.isRainbow());
        opacity.addParent(mod, modeSetting -> mod.is("Logo"));
        radius.addParent(mod, modeSetting -> mod.is("Logo"));
        radius2.addParent(mod, modeSetting -> mod.is("Logo"));
        radius.addParent(mod, modeSetting -> mod.is("New"));
        colorIndex.addParent(mod, modeSetting -> mod.is("Logo"));
        colorSpeed.addParent(mod, modeSetting -> mod.is("Logo"));
        this.addSettings( mod, colorIndex, colorSpeed, radius, radius2, theme, color1, color2);
        if (!enabled) this.toggleSilent();
    }

    public static int offsetValue = 0;
    private final Animation fadeInText = new DecelerateAnimation(500, 1);
    private int ticks = 0;

    private boolean version = true;

    public static float xOffset = 0;
    private final GradientColorWheel colorWheel = new GradientColorWheel();


    @Override
    public void onUpdateEvent(UpdateEvent e) {
        Display.setTitle("Minecraft 1.8.9" );
    }

    @Override
    public void onShaderEvent(ShaderEvent e) {
        User user = Fall.INSTANCE.userManager.getUser();
        //+ "[" + user.getUsername() + "]"
        Pair<Color, Color> clientColors = getClientColors();
        String name = Fall.NAME;
        if (e.isBloom()) {
            switch (mod.getMode()) {
                case "New":
                boolean glow = e.getBloomOptions().getSetting("Watermark").isEnabled();
                if (!glow) {
                    float player = FluidFont16.getStringWidth(mc.thePlayer.getName());
                    float stringWidth2 = FluidFont18.getStringWidth(Fall.NAME);
                    clientColors = Pair.of(Color.BLACK);
                    Color color2 = ColorUtil.applyOpacity(Color.BLACK, opacity.getValue().floatValue());
                    RoundedUtil.drawRound(dragging.getX(), dragging.getY(), stringWidth2 + 19, 10, radius.getValue().floatValue(), color2);
                    RoundedUtil.drawRound(dragging.getX() + 36, dragging.getY(), player + 10, 10, radius.getValue().floatValue(), color2);
                    RoundedUtil.drawRound(dragging.getX() + 50 + player, dragging.getY(), FluidFont16.getStringWidth(getCurrentTimeStamp()) + fluxICON16.getStringWidth("F"), 10, radius.getValue().floatValue(), color2);
                    break;
                }
            }
        }

    }
    public static Color color2(int tick) {
        return new Color(RenderUtil.colorSwitch(color1.getColor(), color2.getColor(), 2000.0f, -(tick * 200) / 40, 75L, 2.0, 255.0));
    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        ScaledResolution sr = new ScaledResolution(mc);
        String name = Fall.NAME;
        version = true;
        int count = 0;
        int index = (int) (count * colorIndex.getValue());
        Pair<Color, Color> clientColors = HUDMod.getClientColors();
        Color textcolor = ColorUtil.interpolateColorsBackAndForth(colorSpeed.getValue().intValue(), index, clientColors.getFirst(), clientColors.getSecond(), false);
        version = name.equalsIgnoreCase(Fall.NAME);
        FluidFont18.drawString("BPS"+mc.thePlayer.getBPS(),1,506,Color.WHITE);
        switch (mod.getMode()) {
            case "Image1":
                RenderUtil.drawImage(new ResourceLocation("Fluid/Logo.png"), 5, 5, 64, 64);
                break;
            case "Image2":
                RenderUtil.drawImage(new ResourceLocation("Fluid/1.jpg"), 5, 5, 64, 64);
                break;
            case "Logo":
                float stringWidth = FluidFont18.getStringWidth("Fall | "+ mc.thePlayer.getName() + " | " + "Developer" + " | "+ Fall.VERSION);
                Color color = ColorUtil.applyOpacity(Color.BLACK, opacity.getValue().floatValue());
                RoundedUtil.drawGradientCornerLR(5, 1, stringWidth, 1F, radius2.getValue().floatValue(), HUDMod.color1.getColor(), HUDMod.color2.getColor());
                RoundedUtil.drawRound(5,3,stringWidth,10,radius.getValue().floatValue(),color);
                FluidFont18.drawString("Fall",5,5,textcolor);
                FluidFont18.drawString(" | " +mc.thePlayer.getName() + " | " + "Developer" + " | "+ Fall.VERSION,19,5,Color.WHITE);
                break;
            case "New":
                float stringWidth2 = FluidFont18.getStringWidth(Fall.NAME);
                float player = FluidFont16.getStringWidth(mc.thePlayer.getName());
                Color color2 = ColorUtil.applyOpacity(Color.BLACK, opacity.getValue().floatValue());
                RoundedUtil.drawRound(dragging.getX(),dragging.getY(),stringWidth2 + 19,10,radius.getValue().floatValue(),color2);
                RoundedUtil.drawRound(dragging.getX() + 36,dragging.getY(),player + 10,10,radius.getValue().floatValue(),color2);
                RoundedUtil.drawRound(dragging.getX() + 50 + player,dragging.getY(),FluidFont16.getStringWidth(getCurrentTimeStamp()) +  fluxICON16.getStringWidth("F"),10,radius.getValue().floatValue(),color2);
                fluxICON16.drawString("c",dragging.getX() + 1,dragging.getY() + 3.5f,Color.WHITE);
                neno16.drawString(Fall.NAME, dragging.getX() + 9, dragging.getY() + 3f, Color.WHITE);
                fluxICON16.drawString("d", dragging.getX() + 37, dragging.getY() + 3.5f, Color.WHITE);
                FluidFont16.drawString(mc.thePlayer.getName(), dragging.getX() + 45, dragging.getY() + 3, Color.WHITE);
                fluxICON16.drawString("F", dragging.getX() + 51 + player, dragging.getY() + 3.7f, Color.WHITE);
                FluidFont16.drawString(getCurrentTimeStamp(), dragging.getX() + 60 + player, dragging.getY() + 3, Color.WHITE);
                break;
        }
    }


    private final Map<String, String> bottomLeftText = new LinkedHashMap<>();


    public static Pair<Color, Color> getClientColors() {
        return Theme.getThemeColors(theme.getMode());
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("h:mm a").format(new Date());
    }

//    public static String get(String text) {
//        return hudCustomization.getSetting("Lowercase").isEnabled() ? text.toLowerCase() : text;
//    }

    private void drawArmor(ScaledResolution sr) {
//        if (hudCustomization.getSetting("Armor HUD").isEnabled()) {
//            List<ItemStack> equipment = new ArrayList<>();
//            boolean inWater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water);
//            int x = -94;
//
//            ItemStack armorPiece;
//            for (int i = 3; i >= 0; i--) {
//                if ((armorPiece = mc.thePlayer.inventory.armorInventory[i]) != null) {
//                    equipment.add(armorPiece);
//                }
//            }
//            Collections.reverse(equipment);
//
//            for (ItemStack itemStack : equipment) {
//                armorPiece = itemStack;
//                RenderHelper.enableGUIStandardItemLighting();
//                x += 15;
//                GlStateManager.pushMatrix();
//                GlStateManager.disableAlpha();
//                GlStateManager.clear(256);
//                mc.getRenderItem().zLevel = -150.0F;
//                int s = mc.thePlayer.capabilities.isCreativeMode ? 15 : 0;
//                mc.getRenderItem().renderItemAndEffectIntoGUI(armorPiece, -x + sr.getScaledWidth() / 2 - 4,
//                        (int) (sr.getScaledHeight() - (inWater ? 65 : 55) + s - (16 * GuiChat.openingAnimation.getOutput().floatValue())));
//                mc.getRenderItem().zLevel = 0.0F;
//                GlStateManager.disableBlend();
//                GlStateManager.disableDepth();
//                GlStateManager.disableLighting();
//                GlStateManager.enableDepth();
//                GlStateManager.enableAlpha();
//                GlStateManager.popMatrix();
//                armorPiece.getEnchantmentTagList();
//            }
//        }
    }

    public static boolean isRainbowTheme() {
        return theme.is("Custom") && color1.isRainbow();
    }

    public static boolean drawRadialGradients() {
        return true;
    }


}

