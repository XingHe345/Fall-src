package dev.Fall.scripting.api.bindings;

import dev.Fall.utils.tuples.Pair;
import dev.Fall.Fall;
import dev.Fall.module.Module;
import dev.Fall.module.impl.combat.KillAura;
import dev.Fall.module.impl.render.HUDMod;
import dev.Fall.utils.player.ChatUtil;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.awt.*;

@Exclude(Strategy.NAME_REMAPPING)
public class ClientBinding {

    public String getClientVersion() {
        return Fall.INSTANCE.getVersion();
    }


    public TimerUtil createTimer() {
        return new TimerUtil();
    }

    public void printClientMsg(String text) {
        ChatUtil.print(text);
    }

    public float fps() {
        return Minecraft.getDebugFPS();
    }

    public EntityLivingBase getAuraTarget() {
        KillAura killAura = Fall.INSTANCE.getModuleCollection().getModule(KillAura.class);
        return KillAura.target;
    }

    public Pair<Color, Color> getClientColors() {
        return HUDMod.getClientColors();
    }

    public boolean leftMouseButtonDown() {
        return Mouse.isButtonDown(0);
    }

    public boolean rightMouseButtonDown() {
        return Mouse.isButtonDown(1);
    }

    public boolean isKeyDown(int key) {
        return Keyboard.isCreated() && Keyboard.isKeyDown(key);
    }


    public Module getModule(String moduleName) {
        Module module = Fall.INSTANCE.getModuleCollection().getModuleByName(moduleName);
        if (module != null) {
            return module;
        } else {
            throw new NullPointerException("Module " + moduleName + " does not exist.");
        }
    }

}
