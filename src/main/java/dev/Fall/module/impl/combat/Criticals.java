package dev.Fall.module.impl.combat;

import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.event.impl.player.AttackEvent;
import dev.Fall.event.impl.player.JumpEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.server.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public final class Criticals extends Module {

    public boolean hypixel = false;
    private final ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Motion", "NoGround","Sillydog");
    private final NumberSetting motion = new NumberSetting("motion", 0.1, 0.2, 0.01, .01);
    int onGroundTicks = 0;
    int JumpTimes = 0;


    public Criticals() {
        super("Criticals", Category.COMBAT, "Crit attacks");
        motion.addParent(mode, modeSetting -> mode.is("Motion"));
        this.addSettings(mode, motion);
    }

    @Override
    public void onJumpEvent(JumpEvent event) {
        JumpTimes ++;
    }

    @Override
    public void onAttackEvent(AttackEvent event) {
        switch (mode.getMode()) {
            case "Packet": {
                setSuffix(mode.getMode());
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer(false));
            }
            break;

            case "Motion": {
                setSuffix(mode.getMode() + " " + motion.getValue());
                if(mc.thePlayer.onGround) mc.thePlayer.motionY = motion.getValue();
            }
            break;

            case "Grim Packet": {
                if(mc.thePlayer.onGround && event.getTargetEntity() != null) {
                    PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ), EnumFacing.DOWN));
                }
            }
            break;
        }
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if(mc.thePlayer == null) return;
        if(mc.thePlayer.onGround) onGroundTicks ++;
        else onGroundTicks = 0;
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        switch (mode.getMode()) {
            case "NoGround": {
                setSuffix(mode.getMode());
                event.setOnGround(false);
            }
            break;

            case "Watchdog": {
                event.setOnGround(false);
                setSuffix(mode.getMode());
            }
            break;

            case "Grim Packet": {
                setSuffix(mode.getMode());
                mc.timer.timerSpeed = 0.9f;
            }
            break;

        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if(event.getPacket() instanceof S14PacketEntity) {

        }
    }

    @Override
    public void onDisable() {
        JumpTimes = 0;
        onGroundTicks = 0;
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }
}
