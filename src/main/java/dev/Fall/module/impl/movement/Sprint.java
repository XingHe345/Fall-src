package dev.Fall.module.impl.movement;

import dev.Fall.Fall;
import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.utils.player.MovementUtils;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Sprint extends Module {
    public static ModeSetting mode = new ModeSetting("Sprint Mode", "Legit", "Omni", "Semi", "Legit");
    public static ModeSetting OmniMode = new ModeSetting("OmniSprint Mode", "Vanilla", "Vanilla");
    public static ModeSetting SemiMode = new ModeSetting("SemiSprint Mode", "Vanilla", "Vanilla");
    public static BooleanSetting usingItem = new BooleanSetting("Using Item", false);
    public static BooleanSetting NoPackets = new BooleanSetting("No Sprint Packets", false);
    boolean isSprintWhenSemiSprint = false;

    public Sprint() {
        super("Sprint", Category.MOVEMENT, "Sprints automatically");
        OmniMode.addParent(mode, modeSetting -> modeSetting.is("Omni"));
        SemiMode.addParent(mode, modeSetting -> modeSetting.is("Semi"));
        usingItem.addParent(mode, modeSetting -> modeSetting.is("Legit"));
        this.addSettings(mode, OmniMode, SemiMode, usingItem, NoPackets);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(mode.getMode());
        if (Fall.INSTANCE.isEnabled(Scaffold.class)) return;
        if (mode.is("Legit")) mc.gameSettings.keyBindSprint.pressed = true;
        if (mode.is("Omni") && OmniMode.is("Vanilla")) {
            if (MovementUtils.isMoving()) mc.thePlayer.setSprinting(true);
            else mc.thePlayer.setSprinting(false);

        }

        if (mode.is("Semi") && SemiMode.is("Vanilla")) {
            if (MovementUtils.isMoving() && !mc.gameSettings.keyBindBack.isKeyDown()) mc.thePlayer.setSprinting(true);
            else mc.thePlayer.setSprinting(false);
        }
        if (usingItem.isEnabled()) {
            if (mc.gameSettings.keyBindForward.isKeyDown() && !(mc.thePlayer.isInLava() || mc.thePlayer.isInWater()) && MovementUtils.isMoving() && mc.thePlayer.isUsingItem() && !mc.thePlayer.isOnLadder())
                mc.thePlayer.setSprinting(true);
        }

    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (NoPackets.isEnabled() && event.getPacket() instanceof C0BPacketEntityAction) {
            if (((C0BPacketEntityAction) event.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING || ((C0BPacketEntityAction) event.getPacket()).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                event.cancel();
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(false);
        mc.gameSettings.keyBindSprint.pressed = false;
        super.onDisable();
    }

}
