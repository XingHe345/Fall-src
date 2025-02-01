package dev.Fall.module.impl.combat;

import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.event.impl.player.UpdateEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.server.PacketUtils;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C09PacketHeldItemChange;


import java.util.ArrayList;
import java.util.List;





public class AutoSnowball extends Module {
    private final NumberSetting Range = new NumberSetting("Range", 10.0, 30.0, 0.0, 1.0); // 距离
    private final NumberSetting delay = new NumberSetting("Delay", 1.0, 10.0, 0.0, 1.0); // 投掷延迟
    private final NumberSetting number = new NumberSetting("Number", 1.0, 16.0, 6.0, 1.0); // 投掷数量
    private final NumberSetting Handoff = new NumberSetting("HandoffDelay", 100.0, 1000.0, 0.0, 10.0); // 切换目标延迟
    private final NumberSetting fov = new NumberSetting("FOV", 90.0, 180.0, 90.0, 10.0); // 角度

    public AutoSnowball() {
        super("AutoSnowball", Category.COMBAT,"");
        this.addSettings(Range, number, Handoff, delay, fov);
    }

    private final TimerUtil switchTimer = new TimerUtil();

    int index;

    private float yaw, pitch;
    private final List<EntityPlayer> targets = new ArrayList<>();
    private TimerUtil timer = new TimerUtil();
    private EntityPlayer target;

    @Override
    public void onEnable() {
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;
        index = 0;
        switchTimer.reset();
        targets.clear();
        target = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;
        index = 0;
        switchTimer.reset();
        targets.clear();
        target = null;
        super.onDisable();
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
     //   if (Fluid.INSTANCE.getModuleCollection().getModule(Stuck.class).isEnable) return; // 这个是卡空
      //  e.setRotations(yaw, pitch);
        if (target != null) {
            e.setYaw(yaw);
        }
        if (e.isPre()) {
            long te = delay.getValue().longValue() * 50L;
            if (target != null) {
                if (target.getHealth() <= 0 || target.isDead || mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) > Range.getValue() || mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) <= 4) {
                    target = null;
                }
            }

            if (target != null) {
                if (getEgg() < 0 || !isVisibleFOV(target, fov.getValue().intValue())) {
                    target = null;
                }
            }

          //  if (target != null && !Fluid.INSTANCE.getModuleCollection().getModule(Stuck.class).isEnable) {
           //     if (timer.hasTimeElapsed(te)) {
            //        this.throwing();
             //       timer.reset();
              //  }
          //  }

            List<EntityPlayer> targets = this.getTargets();

            if (this.targets.size() > 1) {
                if (switchTimer.hasTimeElapsed(Handoff.getValue().intValue())) {
                    switchTimer.reset();
                    ++this.index;
                }
            }
            if (this.index >= this.targets.size()) {
                this.index = 0;
            }

            if (!targets.isEmpty()) {
                target = targets.get(index);
            }
        }
    }

    @Override
    public void onUpdateEvent(UpdateEvent eventUpdate) {
        if (target != null) {
            calculateRotation(target);

            yaw = calculateRotation(target)[0];
            pitch = calculateRotation(target)[1];
        }

        if (target != null) {
            if (target.getDistanceToEntity(mc.thePlayer) <= Range.getValue()) {
                if (mc.thePlayer.onGround) {
                    if (target.getDistanceToEntity(mc.thePlayer) >= 8 && target.getDistanceToEntity(mc.thePlayer) <= 12) {
                        pitch -= 3;
                    } else if (target.getDistanceToEntity(mc.thePlayer) >= 13 && target.getDistanceToEntity(mc.thePlayer) <= 17) {
                        pitch -= 5;
                    } else if (target.getDistanceToEntity(mc.thePlayer) >= 18 && target.getDistanceToEntity(mc.thePlayer) <= 24) {
                        pitch -= 8;
                    } else if (target.getDistanceToEntity(mc.thePlayer) >= 25 && target.getDistanceToEntity(mc.thePlayer) <= Range.getValue()) {
                        pitch -= 11;
                    }
                }
            }
        }

        if (target == null) {
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }
    }



/*
    @EventTarget
    private void onStrafe(EventStrafe eventStrafe) {
        if (target != null) {
            eventStrafe.setYaw(yaw);
        }
    }

    @Override
    public void onJumpEvent(JumpEvent eventJump) {
        if (target != null) {
            eventJump.setYaw(yaw);
        }
    }
*/
    private List<EntityPlayer> getTargets() {
        targets.clear();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer sb = (EntityPlayer) entity;
                if ((sb.getDistanceToEntity(mc.thePlayer) <= Range.getValue() && sb.getDistanceToEntity(mc.thePlayer) >= 4) && sb != mc.thePlayer && getEgg() > 0 && isVisibleFOV(sb, fov.getValue().intValue())) {
                    targets.add((EntityPlayer) entity);
                }
            }
        }
        return targets;
    }

    private void throwing() {
        PacketUtils.sendPacket(new C09PacketHeldItemChange(getEgg()));
        for (int a = 0; a < number.getValue(); a++) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
        }
        PacketUtils.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    public int getEgg() { // 改成坤但也行，之前是写的坤但，所以没改名
        for (int i = 0; i < 9; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack()
                    || !(mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack()
                    .getItem() instanceof ItemSnowball))
                continue;
            return i;
        }
        return -1;
    }

    private float[] calculateRotation(EntityPlayer player) {
        double deltaX = player.posX - mc.thePlayer.posX;
        double deltaY = (player.posY + mc.thePlayer.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double deltaZ = player.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(deltaZ, deltaX) * (180 / Math.PI) - 90;
        double pitch = -Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (180 / Math.PI);

        return new float[]{(float) yaw, (float) pitch};
    }

    private boolean isVisibleFOV(final EntityPlayer e, final float fov) {
        return ((Math.abs(calculateRotation(e)[0] - mc.thePlayer.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(calculateRotation(e)[0] - mc.thePlayer.rotationYaw) % 360.0f) : (Math.abs(calculateRotation(e)[0] - mc.thePlayer.rotationYaw) % 360.0f)) <= fov;
    }
}
