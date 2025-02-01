package dev.Fall.module;

import dev.Fall.Fall;
import dev.Fall.config.DragManager;
import dev.Fall.event.ListenerAdapter;
import dev.Fall.event.impl.game.GameCloseEvent;
import dev.Fall.event.impl.game.KeyPressEvent;
import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.game.WorldEvent;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.event.impl.render.ShaderEvent;
import dev.Fall.module.impl.movement.Flight;
import dev.Fall.module.impl.movement.Scaffold;
import dev.Fall.utils.Utils;

public class BackgroundProcess extends ListenerAdapter implements Utils {

    private final Scaffold scaffold = (Scaffold) Fall.INSTANCE.getModuleCollection().get(Scaffold.class);

    @Override
    public void onKeyPressEvent(KeyPressEvent event) {

        // We should probably have a static arraylist of all the modules instead of creating a new on in getModules()
        for (Module module : Fall.INSTANCE.getModuleCollection().getModules()) {
            if (module.getKeybind().getCode() == event.getKey()) {
                module.toggle();
            }
        }
    }

    @Override
    public void onGameCloseEvent(GameCloseEvent event) {
        Fall.INSTANCE.getConfigManager().saveDefaultConfig();
        DragManager.saveDragData();
    }


    @Override
    public void onTickEvent(TickEvent event) {
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        if (mc.thePlayer != null) {
            scaffold.renderCounterBlur();
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (mc.thePlayer != null) {
            scaffold.renderCounter();
        }
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        if (event instanceof WorldEvent.Load) {
            Flight.hiddenBlocks.clear();
        }
    }

}
