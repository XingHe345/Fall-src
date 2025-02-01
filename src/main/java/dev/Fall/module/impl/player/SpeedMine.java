package dev.Fall.module.impl.player;

import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.player.ChatUtil;
import dev.Fall.utils.server.PacketUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SpeedMine extends Module {

    private final NumberSetting speed = new NumberSetting("Speed", 1.4, 3, 1, 0.1);
    private final ModeSetting mode = new ModeSetting("mode", "Normal", "Normal", "Blink");
    final ConcurrentLinkedQueue<Packet<?>> C07packets = new ConcurrentLinkedQueue<>();
    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost;
    private float damage;

    public SpeedMine() {
        super("Speed Mine", Category.PLAYER, "mines blocks faster");

        this.addSettings(mode, speed);
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
            if (e.isPre()) {
                mc.playerController.blockHitDelay = 0;
                if (pos != null && boost) {
                    IBlockState blockState = mc.theWorld.getBlockState(pos);
                    if (blockState == null) return;

                    try {
                        damage += blockState.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer) * speed.getValue();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }

                    if (damage >= 1) {
                        try {
                            mc.theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 11);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return;
                        }
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
                        damage = 0;
                        boost = false;
                    }
                }
            }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (e.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packet = (C07PacketPlayerDigging) e.getPacket();
            if (packet.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                boost = true;
                pos = packet.getPosition();
                facing = packet.getFacing();
                damage = 0;
            } else if (packet.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK | packet.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                boost = false;
                pos = null;
                facing = null;
            }
        } else if (mode.is("Blink")) {
            if (e.getPacket() instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                C07packets.add(e.getPacket());
                e.cancel();
            } else {
                if (!C07packets.isEmpty()) {
                    C07packets.forEach(PacketUtils::sendPacketNoEvent);
                    C07packets.clear();
                    ChatUtil.print("[SpeedMine] Send C07 Packets.");
                }
            }
        }
    }

    @Override
    public void onDisable(){
        C07packets.forEach(PacketUtils::sendPacketNoEvent);
        C07packets.clear();
        super.onDisable();
    }

}
