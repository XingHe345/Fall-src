package dev.Fall.module.impl.player;

import com.mojang.authlib.GameProfile;
import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.render.Render3DEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.ParentAttribute;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.server.PacketUtils;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Blink extends Module {
    private final BooleanSetting pulse = new BooleanSetting("Pulse", false);
    private final NumberSetting delayPulse = new NumberSetting("Tick Delay", 20, 3000, 4, 1);
    public static BooleanSetting addPlayer = new BooleanSetting("Render Server Positions", false);

    private final CopyOnWriteArrayList<Packet<?>> packetsQueue = new CopyOnWriteArrayList<>();
    public static EntityOtherPlayerMP blinkEntity;

    private final TimerUtil timer = new TimerUtil();


    public Blink() {
        super("Blink", Category.PLAYER, "holds movement packets");
        delayPulse.addParent(pulse, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(pulse, delayPulse, addPlayer);
    }


    public void onEnable() {
        if(addPlayer.isEnabled()) addEntity();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        delEntity();
        blink();
        super.onDisable();
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (pulse.isEnabled()) {
            if (timer.hasTimeElapsed(delayPulse.getValue().longValue())) {
                this.blink();
            }
        }

        if(!addPlayer.isEnabled()) {
            delEntity();
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (
                packet instanceof C03PacketPlayer ||
                        packet instanceof C0BPacketEntityAction ||
                        packet instanceof C0FPacketConfirmTransaction ||
                        packet instanceof C00PacketKeepAlive) {
            event.cancel();
            packetsQueue.add(packet);
        }
    }

    private void blink() {
        if (!packetsQueue.isEmpty()) {
            ArrayList<Packet<?>> toRemove = new ArrayList<>();

            for (Packet<?> packet : packetsQueue) {
                PacketUtils.sendPacketNoEvent(packet);
                if(blinkEntity != null) {
                    this.blinkEntity.inventory = mc.thePlayer.inventory;
                    this.blinkEntity.inventoryContainer = mc.thePlayer.inventoryContainer;
                    this.blinkEntity.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                    this.blinkEntity.rotationYaw = mc.thePlayer.rotationYaw;
                    this.blinkEntity.rotationPitch = mc.thePlayer.rotationPitch;
                    this.blinkEntity.rotationYawHead = mc.thePlayer.rotationYawHead;
                    this.blinkEntity.rotationPitchHead = mc.thePlayer.rotationPitchHead;
                    this.blinkEntity.renderYawOffset = mc.thePlayer.renderYawOffset;
                    this.blinkEntity.setInvisible(true);
                    this.blinkEntity.setSneaking(mc.thePlayer.isSneaking());
                }
                toRemove.add(packet);
            }

            for (Packet<?> p : toRemove) {
                packetsQueue.remove(p);
            }

            toRemove.clear();
        }
        timer.reset();
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if(blinkEntity != null && addPlayer.isEnabled()) {
            RenderUtil.renderBoundingBox(blinkEntity, new Color(255, 0 , 0), 0.5f);
        }
    }

    private void addEntity() {
        this.blinkEntity = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(new UUID(69L, 96L), "[Blink] " + mc.thePlayer.getName()));
        this.blinkEntity.inventory = mc.thePlayer.inventory;
        this.blinkEntity.inventoryContainer = mc.thePlayer.inventoryContainer;
        this.blinkEntity.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        this.blinkEntity.rotationYaw = mc.thePlayer.rotationYaw;
        this.blinkEntity.rotationPitch = mc.thePlayer.rotationPitch;
        this.blinkEntity.rotationYawHead = mc.thePlayer.rotationYawHead;
        this.blinkEntity.rotationPitchHead = mc.thePlayer.rotationPitchHead;
        this.blinkEntity.renderYawOffset = mc.thePlayer.renderYawOffset;
        this.blinkEntity.setSneaking(mc.thePlayer.isSneaking());
        mc.theWorld.addEntityToWorld(this.blinkEntity.getEntityId(), this.blinkEntity);
    }

    private void delEntity() {
        if(blinkEntity == null) return;
        mc.theWorld.removeEntityFromWorld(this.blinkEntity.getEntityId());
    }
}
