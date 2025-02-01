package dev.Fall.module.impl.movement;

import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.event.impl.player.SlowDownEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.Setting;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.utils.server.PacketUtils;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;


public class NoSlow extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Sillydog", new String[] { "Vanilla", "Sillydog", "Grim"});

    private boolean synced;
    boolean sendC08 = false;

    public NoSlow() {
        super("NoSlow", Category.MOVEMENT, "prevent item slowdown");
        addSettings(new Setting[] { (Setting)this.mode });
    }

    public void onSlowDownEvent(SlowDownEvent event) {
        if(mode.is("Sillydog")) {
            if(mc.thePlayer.getHeldItem() == null) return;
            if(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) event.cancel();
        }
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        setSuffix(this.mode.getMode());
        switch (this.mode.getMode()) {
            case "Sillydog": {
                if (e.isPre()) {
                    if (mc.thePlayer.getHeldItem() == null) return;
                    if (mc.thePlayer.isUsingItem()) {
                        if(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0F, 0.0F, 0.0F));
                        }

                        else if(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                    }
                }
            }
                break;
        }
    }
}


//public class NoSlow extends Module {
//
//    //public static ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Vanilla", "NCP", "Watchdog");
//    public static MultipleBoolSetting Enable = new MultipleBoolSetting("Enable", new BooleanSetting("Sword", true),
//            new BooleanSetting("Consume", true),
//            new BooleanSetting("Bow", true)
//    );
//    public static ModeSetting Sword = new ModeSetting("Sword", "Vanilla", "Vanilla", "Watchdog", "NCP", "NewNCP", "Spoof", "C09Spoof");
//    public static ModeSetting Consume = new ModeSetting("Consume", "Vanilla", "Vanilla", "NCP", "Spoof", "C09Spoof", "Watchdog");
//    public static ModeSetting Bow = new ModeSetting("Bow", "Vanilla", "Vanilla", "NCP", "Spoof", "C09Spoof", "Watchdog");
//
//    public static ModeSetting watchdogMode = new ModeSetting("WatchdogMode", "Spoof", "ViaVersion");
//
//    private boolean ncp_state;
//    private boolean synced;
//
//
//    public NoSlow() {
//        super("No Slow", Category.MOVEMENT, "prevent item slowdown");
//        this.addSettings(Enable, Sword, watchdogMode, Consume, Bow);
//        watchdogMode.addParent(Sword, modeSetting -> Sword.is("Watchdog"));
//    }
//
//
//    public static boolean isOnGround(double height) {
//        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
//    }
//
//    @Override
//    public void onSlowDownEvent(SlowDownEvent event) {
//        if ((Enable.isEnabled("Consume") && mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion)) || (Enable.isEnabled("Sword") && mc.thePlayer.isBlocking()) || (Enable.isEnabled("Bow") && mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)) {
//            event.cancel();
//        }
//
//    }
//
//    public boolean pressingUseItem() {
//        return !(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest) && Mouse.isButtonDown(1);
//    }
//
//    @Override
//    public void onEnable() {
//        ncp_state = false;
//        this.setSuffix(Sword.getMode());
//        super.onEnable();
//    }
//
//    @Override
//    public void onUpdateEvent(UpdateEvent e) {
//        this.setSuffix(Sword.getMode());
//        if (Enable.isEnabled("Sword")) {
//            switch (Sword.getMode()) {
//                case "Watchdog" : {
//                    if (mc.thePlayer.ticksExisted % 3 == 0) {
//                        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0f, 0.0f, 0.0f));
//                    }
//                }
//                    break;
//
//                case "Spoof":
//                    if (mc.thePlayer.isBlocking() && MovementUtils.isMoving())
//                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                    break;
//                case "C09Spoof":
//                    if(mc.thePlayer.isBlocking() && MovementUtils.isMoving()) {
//                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
//                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                    }
//                    break;
//            }
//        }
//
//        if (Enable.isEnabled("Consume")) {
//            if (mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion)) {
//                switch (Consume.getMode()) {
//                    case "Spoof":
//                        if (MovementUtils.isMoving())
//                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                        break;
//
//                    case "Watchdog" : {
//                        if (mc.thePlayer.ticksExisted % 3 == 0) {
//                            sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0f, 0.0f, 0.0f));
//                        }
//                    }
//                    break;
//                }
//            }
//        }
//
//        if (Enable.isEnabled("Bow")) {
//            if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
//                switch (Bow.getMode()) {
//                    case "Spoof":
//                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                        break;
//                    case "C09Spoof":
//                        if(mc.thePlayer.isBlocking() && MovementUtils.isMoving()) {
//                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
//                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                        }
//                        break;
//
//                    case "Watchdog" : {
//                        if (mc.thePlayer.ticksExisted % 3 == 0) {
//                            sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0f, 0.0f, 0.0f));
//                        }
//                    }
//                    break;
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onMotionEvent(MotionEvent e) {
//        if (Enable.isEnabled("Sword")) {
//            switch (Sword.getMode()) {
//                case "NCP":
//                    if (e.isPre()) {
//                        if (!ncp_state && MovementUtils.isMoving() && mc.thePlayer.isBlocking() && isOnGround(0.42)) {
//                            PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//                            ncp_state = true;
//                        } else {
//                            ncp_state = false;
//                        }
//                    } else {
//                        if (ncp_state && MovementUtils.isMoving() && mc.thePlayer.isBlocking() && isOnGround(0.42)) {
//                            PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
//                            ncp_state = false;
//                        }
//                    }
//                    break;
//
//                case "NewNCP":
//                    if (e.isPre())
//                        if (MovementUtils.isMoving() && mc.thePlayer.isBlocking() && mc.thePlayer.onGround) {
//                            PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
//                        }
//                    break;
//                case "Spam":
//                    if(mc.thePlayer.isBlocking() && MovementUtils.isMoving() && e.isPre()) {
//                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//                        PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
//                    }
//                    break;
//
//            }
//        }
//
//        if (Enable.isEnabled("Consume")) {
//            if (mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion))
//                switch (Consume.getMode()) {
//                    case "C09Spoof":
//                        if(MovementUtils.isMoving()) {
//                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
//                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                        }
//                        break;
//                }
//        }
//
//        if (Enable.isEnabled("Bow")) {
//            if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)
//                switch (Bow.getMode()) {
//                }
//        }
//    }
//
//    @Override
//    public void onPacketSendEvent(PacketSendEvent event){
//        if (Enable.isEnabled("Sword")) {
//            switch (Sword.getMode()) {
//
//            }
//        }
//    }
//
//    @Override
//    public void onPacketReceiveEvent(PacketReceiveEvent e) {
//        if (e.getPacket() instanceof S30PacketWindowItems) {
//            final S30PacketWindowItems packet = (S30PacketWindowItems) e.getPacket();
//            if (Enable.isEnabled("Sword")) {
//                if (Sword.is("NewNCP")) {
//                    if (e.getPacket() instanceof S30PacketWindowItems) {
//                        final S30PacketWindowItems packet_ = (S30PacketWindowItems) e.getPacket();
//
//                        if (MovementUtils.isMoving() && packet_.getWindowId() == 0 && mc.thePlayer.isBlocking()) {
//                            e.cancel();
//                            PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
//                        }
//                    } else if (e.getPacket() instanceof S2FPacketSetSlot) {
//                        final S2FPacketSetSlot packet_ = (S2FPacketSetSlot) e.getPacket();
//
//                        if ((packet_.func_149175_c() == 0 || packet_.func_149175_c() == -1) &&
//                                (packet_.func_149173_d() == 0 || packet_.func_149173_d() == -1) &&
//                                packet_.func_149174_e() == null &&
//                                MovementUtils.isMoving() && mc.thePlayer.isBlocking()) {
//                            e.cancel();
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
