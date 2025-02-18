package dev.Fall.utils.font.hanabi;

import dev.Fall.utils.font.AbstractFontRenderer;
import dev.Fall.utils.font.CustomFont;
import dev.Fall.utils.font.hanabi.noway.ttfr.HFontRenderer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TIMER_err
 * created 2023.5.7
 **/
public class FontUtil {
    public static AbstractFontRenderer FluidFont24 = new RendererInterface("Fluid.ttf", 24);
    public static AbstractFontRenderer FluidFont20 = new RendererInterface("Fluid.ttf", 20);
    public static AbstractFontRenderer FluidFont18 = new RendererInterface("Fluid.ttf", 18);
    public static AbstractFontRenderer icon18 = new RendererInterface("micon.ttf", 18);
    public static AbstractFontRenderer icon30 = new RendererInterface("micon.ttf", 30);
    private static final Map<CustomFont, AbstractFontRenderer> fontLoadedMap = new HashMap<>();

    public static AbstractFontRenderer getFromCustomFont(CustomFont font) {
        if (fontLoadedMap.get(font) != null)
            return fontLoadedMap.get(font);
        AbstractFontRenderer renderer = new RendererInterface(font.getFont());
        fontLoadedMap.put(font, renderer);
        return renderer;
    }


    private static HFontRenderer getFontRenderer(Font font, boolean antiAlias) {
        return new HFontRenderer(font, font.getSize(), true);
    }
}