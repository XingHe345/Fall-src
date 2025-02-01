package dev.Fall.module.impl.player;

import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.server.PacketUtils;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

public class AntiVoid extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Sillydog", "Sillydog", "NCP");
    private final NumberSetting fallDist = new NumberSetting("Fall Distance", 3, 20, 1, 0.5);
    private final TimerUtil timer = new TimerUtil();
    private boolean reset;
    private double lastGroundY;

    private final List<Packet> packets = new ArrayList<>();

    public AntiVoid() {
        super("Anti Void", Category.PLAYER, "saves you from the void");
        this.addSettings(mode, fallDist);
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        this.setSuffix(mode.getMode());
        if((mode.is("Sillydog") || mode.is("NCP"))) {
            if(event.getPacket() instanceof C03PacketPlayer && mc.thePlayer.ticksExisted > 50) {
                if(!isBlockUnder()) {
                    if(mc.thePlayer.fallDistance < fallDist.getValue()) {
                        event.cancel();
                        packets.add(event.getPacket());
                    } else {
                        if(!packets.isEmpty()) {
                            for(Packet packet : packets) {
                                final C03PacketPlayer c03 = (C03PacketPlayer) packet;
                                c03.setY(lastGroundY);
                                PacketUtils.sendPacketNoEvent(packet);
                            }
                            packets.clear();
                        }
                    }
                } else {
                    lastGroundY = mc.thePlayer.posY;
                    if(!packets.isEmpty()) {
                        packets.forEach(PacketUtils::sendPacketNoEvent);
                        packets.clear();
                    }
                }
            }
        }
    }

    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0) return false;
        for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
