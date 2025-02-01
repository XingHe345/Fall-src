package dev.Fall.module.impl.combat;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.utils.PacketUtil;
import dev.Fall.Fall;
import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.player.MovementUtils;
import dev.Fall.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;

public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Sillydog", "Grim");
    private final BooleanSetting legitSprint = new BooleanSetting("Legit Sprint", false);
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, 0, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, 0, 1);
    private final NumberSetting airhorizontal = new NumberSetting("air Horizontal", 0, 100, 0, 1);
    private final NumberSetting airvertical = new NumberSetting("air Vertical", 0, 100, 0, 1);
    private int lastSprint = -1;

    public Velocity() {
        super("Velocity", Category.COMBAT, "Reduces your knockback");
        horizontal.addParent(mode, modeSetting -> mode.is("Normal"));
        airhorizontal.addParent(mode, modeSetting -> mode.is("Normal"));
        vertical.addParent(mode, modeSetting -> mode.is("Normal"));
        airvertical.addParent(mode, modeSetting -> mode.is("Normal"));
        legitSprint.addParent(mode, modeSetting -> mode.is("Grim"));
        this.addSettings(mode, legitSprint, horizontal, vertical, airhorizontal, airvertical);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (mode.is("Sillydog")) {
        }
        if (legitSprint.isEnabled()) {
            if (lastSprint == 0) {
                lastSprint--;
                if (!MovementUtils.canSprint(true))
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            } else if (lastSprint > 0) {
                lastSprint--;
                if (mc.thePlayer.onGround && !MovementUtils.canSprint(true)) {
                    lastSprint = -1;
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
        }
    }

    @Override
    public void onTickEvent(TickEvent event) {
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02f;
        lastSprint = -1;
        super.onDisable();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        this.setSuffix(mode.getMode());
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = ((S12PacketEntityVelocity) e.getPacket());
            if(s12.getEntityID() != mc.thePlayer.getEntityId()) return;
            switch (mode.getMode()) {
                case "SillydogSB": {
                    if (mc.thePlayer.isOnGround()) {
                        if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= horizontal.getValue().intValue() / 100;
                        s12.motionZ *= horizontal.getValue().intValue() / 100;
                        s12.motionY *= vertical.getValue().intValue() / 100;
                    } else {
                        if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionY *= (int) airvertical.getValue().intValue() / 100;
                    }
                }
                break;

                case "Grim": {
                    Packet<?> packet = e.getPacket();

                    if (packet instanceof S12PacketEntityVelocity) {
                        final S12PacketEntityVelocity wrapped = (S12PacketEntityVelocity) packet;

                        if (wrapped.getEntityID() == mc.thePlayer.getEntityId()) {
                            KillAura aura = Fall.INSTANCE.getModuleCollection().getModule(KillAura.class);
                            if (aura.target != null) {

                                if (mc.thePlayer.getDistanceToEntity(aura.target) > aura.reach.getValue().doubleValue()) {
                                    return;
                                }

                                e.cancel();

                                if (!EntityPlayerSP.serverSprintState) {
                                    if (legitSprint.isEnabled()) {
                                        if (lastSprint < 0)
                                            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                                        lastSprint = 2;
                                    } else {
                                        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                                    }
                                }

                                for (int i = 0; i < 8; i++) {
                                    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(Fall.INSTANCE.getModuleCollection().getModule(KillAura.class).target, C02PacketUseEntity.Action.ATTACK));
                                    if (ViaLoadingBase.getInstance().getNativeVersion() >= 47) {
                                        PacketWrapper c0A = PacketWrapper.create(26, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                                        c0A.write(Type.VAR_INT, 0);
                                        PacketUtil.sendToServer(c0A, Protocol1_8TO1_9.class, true, true);
                                    }
                                }


                                double velocityX = wrapped.motionX / 8000.0;
                                double velocityZ = wrapped.motionZ / 8000.0;

                                if (MathHelper.sqrt_double(velocityX * velocityX * velocityZ * velocityZ) <= 5F) {
                                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                                } else {
                                    mc.thePlayer.motionX = velocityX * 0.15;
                                    mc.thePlayer.motionZ = velocityZ * 0.15;
                                }

                                mc.thePlayer.motionY = wrapped.motionY / 8000.0;

                                if (!EntityPlayerSP.serverSprintState && !legitSprint.isEnabled())
                                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

                            }
                        }
                    }
                }
                break;

                case "Sillydog": {
                    e.cancel();
                    mc.thePlayer.motionY = s12.motionY / 8000.0D;
                    //PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.serverPosX + s12.motionX / 8000.0D, mc.thePlayer.serverPosY + s12.motionY / 8000.0D, mc.thePlayer.serverPosZ + s12.motionZ / 8000.0D, mc.thePlayer.onGround));
                }
            }
            if (e.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion s27 = ((S27PacketExplosion) e.getPacket());
                switch (mode.getMode()) {
                    case "SillydogSB": {
                        if (mc.thePlayer.onGround) {
                            if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                                e.cancel();
                                return;
                            }
                            s27.motionX *= horizontal.getValue().intValue() / 0;
                            s27.motionZ *= horizontal.getValue().intValue() / 0;
                            s27.motionY *= vertical.getValue().intValue() / 100;
                        } else {
                            if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                                e.cancel();
                                return;
                            }
                            s27.motionX *= (int) airhorizontal.getValue().intValue() / 0;
                            s27.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                            s27.motionY *= (int) airvertical.getValue().intValue() / 100;
                        }
                    }
                    break;

                }
            }
        }
    }

//    @Override
//    public void onPacketSendEvent(PacketSendEvent event) {
//        if(event.getPacket() instanceof C02PacketUseEntity) {
//            if(((C02PacketUseEntity) event.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
//                ChatUtil.print("你攻击了: " + ((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld).getName() + "他的当前血量: " + ((EntityLivingBase) ((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld)).getHealth() + " \n 你手持武器的伤害: " + InventoryUtils.getSwordStrength(mc.thePlayer.getHeldItem()));
//            }
//        }
//    }
}
