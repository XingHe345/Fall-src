package dev.Fall.module.impl.render;

import dev.Fall.Fall;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.ui.musicplayer.utils.Blur;
import dev.Fall.ui.musicplayer.cloudmusic.MusicManager;
import dev.Fall.ui.musicplayer.cloudmusic.impl.Lyric;
import dev.Fall.ui.musicplayer.cloudmusic.util.Stencil;
import dev.Fall.utils.font.AbstractFontRenderer;
import dev.Fall.utils.font.FontUtil;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import dev.Fall.utils.render.blur.GaussianBlur;
import javafx.scene.media.MediaPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @author TIMER_err
 */
@SuppressWarnings("DuplicatedCode")
public class MusicBar extends Module {
    private final float[] visWidth = new float[100];
    private final Dragging bar = Fall.INSTANCE.createDrag(this, "Music Bar", 5, 355);

    public MusicBar() {
        super("Music Bar", Category.RENDER,"music");
    }

    @Override
    public void onRender2DEvent(Render2DEvent event){
        if (MusicManager.INSTANCE.getCurrentTrack() != null && MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.getCurrentTrack().id) != null) {
            AbstractFontRenderer font = dev.Fall.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.FluidFont24);
            float x = bar.getX();
            float y = bar.getY();
            bar.setWidth(font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().artists) + font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().name) + 10);
            bar.setHeight(40);
            if (!System.getProperty("os.name").startsWith("Mac OS"))
                Blur.bloom(() -> RoundedUtil.drawRound(x, y,font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().artists) + font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().name) + 10,40, 0, new Color(0)), 2, 1);
            Stencil.write(false);
            RoundedUtil.drawRound(x, y, font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().artists) + font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().name) + 10, 40, 0, new Color(255, 255, 255));
            Stencil.erase(true);
            RenderUtil.drawImage(MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.getCurrentTrack().id), x, y - 200 / 3, font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().artists) + font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().name) + 10, 200);
            Stencil.dispose();
            GaussianBlur.startBlur();
            RoundedUtil.drawRound(x, y, font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().artists) + font.getStringWidth(MusicManager.INSTANCE.getCurrentTrack().name) + 10, 40, 0, new Color(255, 255, 255));
            GaussianBlur.endBlur(7, 1);

            Stencil.write(false);
            RoundedUtil.drawRound(x + 2, y + 2, 40 - 4, 40 - 4,0, new Color(255, 255, 255));
            Stencil.erase(true);
            RenderUtil.drawImage(MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.getCurrentTrack().id), x + 2, y + 2, 40 - 4, 40 - 4);
            Stencil.dispose();

            font.drawString(MusicManager.INSTANCE.getCurrentTrack().name, x + 2 +40 - 4 + 5, y + 10, new Color(255, 255, 255, 255).getRGB());
            dev.Fall.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.FluidFont14).drawString(MusicManager.INSTANCE.getCurrentTrack().artists, x + 2 + 40 - 4 + 5, y + 20, new Color(255, 255, 255, 255).getRGB());

            ScaledResolution sr = new ScaledResolution(mc);

            if (MusicManager.INSTANCE.magnitudes != null) {
                int visNum = 100;
                float width = sr.getScaledWidth() / (float) visNum;
                Gui.drawRect(0, 0, 0, 0, 0);
                for (int i = 0; i < 100; i += 100 / visNum) {
                    Gui.drawRect(i / (100 / (float) visNum) * width, sr.getScaledHeight() - visWidth[i], i / (100 / (float) visNum) * width + width, sr.getScaledHeight(), new Color(255,255,255,80).getRGB());
                    if (MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING)
                        visWidth[i] += (MusicManager.INSTANCE.magnitudes[i] - visWidth[i]) / (10f * (Minecraft.getDebugFPS() != 0 ? (Minecraft.getDebugFPS() / 60f) : 1));
                    else
                        visWidth[i] -= visWidth[i] / (10f * (Minecraft.getDebugFPS() != 0 ? (Minecraft.getDebugFPS() / 60f) : 1));
                    if (Float.isNaN(visWidth[i])) visWidth[i] = MusicManager.INSTANCE.magnitudes[i];
                }
            }

            if (MusicManager.INSTANCE.getMediaPlayer() != null && MusicManager.INSTANCE.getMediaPlayer().getCurrentTime() != null) {
                Lyric lyric = null;
                for (Lyric l : MusicManager.INSTANCE.lrc)
                    if (l.time <= MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toMillis()) lyric = l;
                AbstractFontRenderer font2 = dev.Fall.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.FluidFont22);
                if (lyric != null) {
                    float y3 = sr.getScaledHeight()-90;
                    float height = font2.getHeight() * 2 + 10;
                    Lyric finalLyric = lyric;
                    MusicManager.INSTANCE.lrc.forEach(l -> {
                        l.y = y3 + (MusicManager.INSTANCE.lrc.indexOf(l) + 1) * height - (MusicManager.INSTANCE.lrc.indexOf(finalLyric) + 1) * height;
                        l.cacheY += (l.y - l.cacheY) / (5f * (Minecraft.getDebugFPS() != 0 ? (Minecraft.getDebugFPS() / 60f) : 1));
                        if (Float.isNaN(l.cacheY)) l.cacheY = l.y;
                        if (Math.abs(MusicManager.INSTANCE.lrc.indexOf(l) - MusicManager.INSTANCE.lrc.indexOf(finalLyric)) <= 1) {
                            int alpha;
                            if (Math.abs(l.cacheY - y3) / 6 != 0 && (255 / (Math.abs(l.cacheY - y3) / 6) <= 255 && (255 / (Math.abs(l.cacheY - y3) / 6) >= 0)))
                                alpha = (int) (255 / (Math.abs(l.cacheY - y3) / 6));
                            else alpha = 255;
                            float x1 = (sr.getScaledWidth() - font2.getStringWidth(l.text)) / 2f;
                            float y1 = l.cacheY, y2 = l.cacheY + 10;
                            font2.drawString(l.text, x1, y1, new Color(255, 255, 255, (int) (alpha * 0.8)).getRGB());
                            MusicManager.INSTANCE.tlrc.stream().filter(tl -> tl.time == l.time).findFirst().ifPresent(tl ->
                                    font2.drawString(tl.text, (sr.getScaledWidth() - font2.getStringWidth(tl.text)) / 2f, y2, new Color(255, 255, 255, (int) (alpha * 0.8)).getRGB())
                            );
                        }
                    });
                }
            }
        }
    }
}
