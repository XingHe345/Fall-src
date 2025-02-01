package dev.Fall.module.impl.movement;

import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.utils.server.PacketUtils;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class Nofall extends Module {
    private final ModeSetting mode = new ModeSetting("Packet Mode", "Vanilla", "Vanilla", "Sillydog");
    private TimerUtil timer = new TimerUtil();

    public Nofall() {
        super("No Fall", Category.MOVEMENT, "No Fall Damage");
        addSettings(mode);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Vanilla": {
                event.setOnGround(true);
            }
            break;

            case "Sillydog": {

                if(mc.thePlayer.fallDistance <= 0.5) {
                    mc.timer.timerSpeed = 1;
                }
                else mc.timer.timerSpeed = 0.9f;
            }
            break;
        }
    }

        public static boolean aabb(double d) {
            double d2;
            AxisAlignedBB axisAlignedBB = mc.thePlayer.getEntityBoundingBox();
            if(axisAlignedBB == null) return false;
            for (d2 = 0.0; d2 < d; d2 += (double) mc.thePlayer.height) {
                if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, axisAlignedBB.offset(0.0, -d2, 0.0)).isEmpty())
                    continue;
                return false;
            }
            for (d2 = 0.0; d2 < axisAlignedBB.minY; d2 += (double) mc.thePlayer.height) {
                if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, axisAlignedBB.offset(0.0, -d2, 0.0)).isEmpty())
                    continue;
                return true;
            }
            return false;
        }

        public static boolean jumpPotion() {
            PotionEffect potionEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
            float f = potionEffect != null ? (float) (potionEffect.getAmplifier() + 1) : 0.0f;
            return MathHelper.ceiling_float_int((float) (mc.thePlayer.fallDistance - 3.0f - f)) > 0;
        }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if(mode.is("Sillydog")) {
            if((event.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer) event.getPacket()).onGround) || !timer.hasTimeElapsed(100L) || !aabb(0.0) || !jumpPotion())
                return;
            timer.reset();
            PacketUtils.sendPacket(new C03PacketPlayer(true));
            mc.thePlayer.fallDistance = 0;
        }

    }
}
