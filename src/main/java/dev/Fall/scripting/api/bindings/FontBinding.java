package dev.Fall.scripting.api.bindings;

import dev.Fall.utils.Utils;
import dev.Fall.utils.font.AbstractFontRenderer;
import dev.Fall.utils.font.FontUtil;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.util.Arrays;

@Exclude(Strategy.NAME_REMAPPING)
public class FontBinding implements Utils {

    public AbstractFontRenderer getCustomFont(String fontName, int fontSize) {
        FontUtil.FontType fontType = Arrays.stream(FontUtil.FontType.values()).filter(fontType1 -> fontType1.name().equals(fontName)).findFirst().orElse(FontUtil.FontType.Fluid);
        return fontType.size(fontSize);
    }

    public AbstractFontRenderer getMinecraftFontRenderer() {
        return mc.fontRendererObj;
    }


    public AbstractFontRenderer getFluidFont14() {return FluidFont14; }
    public AbstractFontRenderer getFluidFont16() {return FluidFont16; }
    public AbstractFontRenderer getFluidFont18() {return FluidFont18; }
    public AbstractFontRenderer getFluidFont20() {return FluidFont20; }
    public AbstractFontRenderer getFluidFont22() {return FluidFont22; }
    public AbstractFontRenderer getFluidFont24() {return FluidFont24; }
    public AbstractFontRenderer getFluidFont26() {return FluidFont26; }
    public AbstractFontRenderer getFluidFont28() {return FluidFont28; }
    public AbstractFontRenderer getFluidFont32() {return FluidFont32; }
    public AbstractFontRenderer getFluidFont40() {return FluidFont40; }
    public AbstractFontRenderer getFluidFont80() {return FluidFont80; }
}
