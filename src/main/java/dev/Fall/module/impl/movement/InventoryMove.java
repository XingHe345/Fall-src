package dev.Fall.module.impl.movement;

import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.utils.player.MovementUtils;
import dev.Fall.utils.server.PacketUtils;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class InventoryMove extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Spoof", "Delay", "Watchdog");
    private final TimerUtil delayTimer = new TimerUtil();
    private boolean wasInContainer;
    final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    boolean sendc0e = false;
    int afterc0e = 0;

    private static final List<KeyBinding> keys = Arrays.asList(
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    );

    public InventoryMove() {
        super("Inventory Move", Category.MOVEMENT, "lets you move in your inventory");
        addSettings(mode);
    }

    public static void updateStates() {
        if (mc.currentScreen != null) {
            keys.forEach(k -> KeyBinding.setKeyBindState(k.getKeyCode(), GameSettings.isKeyDown(k)));
        }
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        boolean inContainer = mc.currentScreen instanceof GuiContainer;
        if (wasInContainer && !inContainer) {
            wasInContainer = false;
            updateStates();
        }
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Spoof":
            case "Vanilla":
                if (inContainer) {
                    wasInContainer = true;
                    updateStates();
                }
                break;
            case "Watchdog":
                if(!MovementUtils.isMoving()){
                    for (Packet<?> p : packets) {
                        PacketUtils.sendPacketNoEvent(p);
                        packets.remove(p);
                    }
                    packets.clear();
                }
                break;

            case "Delay":
                if (e.isPre() && inContainer) {
                    if (delayTimer.hasTimeElapsed(100)) {
                        wasInContainer = true;
                        updateStates();
                        delayTimer.reset();
                    }
                }
                break;
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (mode.is("Spoof") && (e.getPacket() instanceof S2DPacketOpenWindow || e.getPacket() instanceof S2EPacketCloseWindow)) {
            e.cancel();
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e){
        if(mode.is("Watchdog")) {
            if(sendc0e) afterc0e ++;
            if(!sendc0e) afterc0e = 0;
            if(afterc0e < 20) sendc0e = false;
            if (e.getPacket() instanceof C0EPacketClickWindow) {
                sendc0e = true;
                packets.add(e.getPacket());
                e.cancel();
            }
            if (e.getPacket() instanceof C0DPacketCloseWindow) {
                packets.add(e.getPacket());
                e.cancel();
            }

            if (e.getPacket() instanceof C16PacketClientStatus && ((C16PacketClientStatus) e.getPacket()).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                packets.add(e.getPacket());
                e.cancel();
            }

            if(sendc0e && (e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
                packets.add(e.getPacket());
                e.cancel();
            }
        }

    }

    @Override
    public void onEnable(){
        super.onEnable();
       // NotificationManager.post(NotificationType.DISABLE, ">_<", ">_<");
        //this.setToggled(false);
    }

    @Override
    public void onDisable(){
        packets.forEach(PacketUtils::sendPacketNoEvent);
        packets.clear();
        super.onDisable();
    }
}
