package dev.Fall.module.impl.player;

import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.player.UpdateEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.ui.notifications.NotificationManager;
import dev.Fall.ui.notifications.NotificationType;
import dev.Fall.utils.server.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import java.lang.annotation.Target;

public class Stuck extends Module {
    public Stuck() {
        super("Stuck", Category.PLAYER, "NoC03");
    }

    private double x;
    private double y;
    private double z;
    private double motionX;
    private double motionY;
    private double motionZ;
    private boolean onGround;
    private Vector2f rotation;

    @Override
    public void onEnable() {
        this.onGround = mc.thePlayer.onGround;
        this.x = Stuck.mc.thePlayer.posX;
        this.y = Stuck.mc.thePlayer.posY;
        this.z = Stuck.mc.thePlayer.posZ;
        this.motionX = Stuck.mc.thePlayer.motionX;
        this.motionY = Stuck.mc.thePlayer.motionY;
        this.motionZ = Stuck.mc.thePlayer.motionZ;
        this.rotation = new Vector2f(Stuck.mc.thePlayer.rotationYaw, Stuck.mc.thePlayer.rotationPitch);
        final float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        final Vector2f rotation = this.rotation;
        rotation.x -= this.rotation.x % gcd;
        final Vector2f rotation2 = this.rotation;
        rotation2.y -= this.rotation.y % gcd;
        if (Stuck.mc.thePlayer == null) {
            return;
        }
        super.onEnable();
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            final C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) e.getPacket();
            final Vector2f current = new Vector2f(Stuck.mc.thePlayer.rotationYaw, Stuck.mc.thePlayer.rotationPitch);
            final float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float gcd = f * f * f * 1.2f;
            final Vector2f vector2f = current;
            vector2f.x -= current.x % gcd;
            final Vector2f vector2f2 = current;
            vector2f2.y -= current.y % gcd;
            if (this.rotation.equals((Tuple2f) current)) {
                return;
            }
            this.rotation = current;
            e.cancel();
            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, this.onGround));
            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));

        }
    }

    @Override
    public void onUpdateEvent(UpdateEvent e) {
        Stuck.mc.thePlayer.motionX = 0.0;
        Stuck.mc.thePlayer.motionY = 0.0;
        Stuck.mc.thePlayer.motionZ = 0.0;
        Stuck.mc.thePlayer.setPosition(this.x, this.y, this.z);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
        }
    }

    public void throwPearl(final Vector2f current) {

        Stuck.mc.thePlayer.rotationYaw = current.x;
        Stuck.mc.thePlayer.rotationPitch = current.y;
        final float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        current.x -= current.x % gcd;
        current.y -= current.y % gcd;
        if (!rotation.equals(current)) {
            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, onGround));
        }
        rotation = current;
        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(Stuck.mc.thePlayer.getHeldItem()));
    }
}
