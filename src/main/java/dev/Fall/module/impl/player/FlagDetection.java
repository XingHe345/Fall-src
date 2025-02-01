package dev.Fall.module.impl.player;

import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.ui.notifications.NotificationManager;
import dev.Fall.ui.notifications.NotificationType;
import dev.Fall.utils.player.ChatUtil;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class FlagDetection extends Module {
    private final NumberSetting TicksExisted = new NumberSetting("ticksExisted", 10, 200, 1, 1);
    public FlagDetection() {
        super("Flag Detector", Category.PLAYER, "Check if you bounce flag");
        addSettings(TicksExisted);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if(mc.thePlayer == null) return;
        if (mc.thePlayer.ticksExisted > TicksExisted.getValue()) {
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                ChatUtil.print("[旗帜检测器]检测到S08flag恭喜！！！.");
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                NotificationManager.post(NotificationType.WARNING, "旗帜检测器", "S08Found!", 2.0F);
            }
        }
    }
}
