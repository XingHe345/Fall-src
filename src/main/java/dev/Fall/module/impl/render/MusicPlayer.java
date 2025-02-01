package dev.Fall.module.impl.render;

import dev.Fall.module.Category;
import dev.Fall.module.Module;

public class MusicPlayer extends Module {
    public MusicPlayer() {
        super("MusicPlayer",Category.RENDER,"Play music");
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new dev.Fall.ui.musicplayer.MusicPlayer());
        this.toggleSilent(false);
    }

}
