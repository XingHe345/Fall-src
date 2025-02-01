package dev.Fall.utils;

import dev.Fall.utils.font.CustomFont;
import dev.Fall.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public interface Utils {
    Minecraft mc = Minecraft.getMinecraft();
    IFontRenderer fr = mc.fontRendererObj;

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    FontUtil.FontType FluidFont = FontUtil.FontType.Fluid,
            iconFont = FontUtil.FontType.ICON,
            neverloseFont = FontUtil.FontType.NEVERLOSE,
            tahomaFont = FontUtil.FontType.TAHOMA,
            rubikFont = FontUtil.FontType.RUBIK,
            fluxICON = FontUtil.FontType.FluxICON,
            CoolFont = FontUtil.FontType.CoolFont,
            RobotoFont = FontUtil.FontType.Roboto,
            WeiLaiYuan = FontUtil.FontType.WeiLaiYuan,
            BRIGHT = FontUtil.FontType.BRIGHT,
            neno = FontUtil.FontType.NEON;



    //Regular Fonts
    CustomFont TAHOMA16 = tahomaFont.size(16);

    CustomFont bright16 = BRIGHT.size(16);

    CustomFont neno16 = neno.size(16),
               neno18 = neno.size(18);


    CustomFont FluidFont12 = FluidFont.size(12),
            FluidFont14 = FluidFont.size(14),
            FluidFont16 = FluidFont.size(16),
            FluidFont18 = FluidFont.size(18),
            FluidFont20 = FluidFont.size(20),
            FluidFont22 = FluidFont.size(22),
            FluidFont24 = FluidFont.size(24),
            FluidFont26 = FluidFont.size(26),
            FluidFont28 = FluidFont.size(28),
            FluidFont32 = FluidFont.size(32),
            FluidFont40 = FluidFont.size(40),
            FluidFont80 = FluidFont.size(80);

    CustomFont Roboto12 = RobotoFont.size(12),
            Roboto14 = RobotoFont.size(14),
            Roboto16 = RobotoFont.size(16),
            Roboto18 = RobotoFont.size(18),
            Roboto20 = RobotoFont.size(20),
            Roboto22 = RobotoFont.size(22),
            Roboto24 = RobotoFont.size(24),
            Roboto26 = RobotoFont.size(26),
            Roboto28 = RobotoFont.size(28),
            Roboto32 = RobotoFont.size(32),
            Roboto40 = RobotoFont.size(40),
            Roboto80 = RobotoFont.size(80);

    //rubik Fonts
    CustomFont rubikFont12 = rubikFont.size(12),
            rubikFont14 = rubikFont.size(14),
            rubikFont16 = rubikFont.size(16),
            rubikFont18 = rubikFont.size(18),
            rubikFont20 = rubikFont.size(20),
            rubikFont22 = rubikFont.size(22);

    //Bold Fonts
    CustomFont FluidBoldFont14 = FluidFont14.getBoldFont(),
            FluidBoldFont16 = FluidFont16.getBoldFont(),
            FluidBoldFont18 = FluidFont18.getBoldFont(),
            FluidBoldFont20 = FluidFont20.getBoldFont(),
            FluidBoldFont22 = FluidFont22.getBoldFont(),
            FluidBoldFont24 = FluidFont24.getBoldFont(),
            FluidBoldFont26 = FluidFont26.getBoldFont(),
            FluidBoldFont28 = FluidFont28.getBoldFont(),
            FluidBoldFont32 = FluidFont32.getBoldFont(),
            FluidBoldFont40 = FluidFont40.getBoldFont(),
            FluidBoldFont80 = FluidFont80.getBoldFont();

    //Icon Fontsor i
    CustomFont iconFont16 = iconFont.size(16),
            iconFont20 = iconFont.size(20),
            iconFont26 = iconFont.size(26),
            iconFont32 = iconFont.size(32),
            iconFont35 = iconFont.size(35),
            iconFont40 = iconFont.size(40),
            iconFont45 = iconFont.size(45),
            iconFont50 = iconFont.size(50);

    CustomFont fluxICON14 = fluxICON.size(14),
            fluxICON16 = fluxICON.size(16),
            fluxICON18 = fluxICON.size(18),
            fluxICON20 = fluxICON.size(20),
            fluxICON26 = fluxICON.size(26),
            fluxICON35 = fluxICON.size(35),
            fluxICON40 = fluxICON.size(40),
            fluxICON50 = fluxICON.size(50);

    CustomFont CoolFont14 = CoolFont.size(14),
            CoolFont16 = CoolFont.size(16),
            CoolFont18 = CoolFont.size(18),
            CoolFont20 = CoolFont.size(20),
            CoolFont24 = CoolFont.size(24),
            CoolFont26 = CoolFont.size(26);
}
