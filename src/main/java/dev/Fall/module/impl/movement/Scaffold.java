package dev.Fall.module.impl.movement;

import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.player.*;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.ParentAttribute;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.misc.MathUtils;
import dev.Fall.utils.player.*;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import dev.Fall.utils.server.PacketUtils;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;

import java.awt.*;

public class Scaffold extends Module {

    public static final ModeSetting mode = new ModeSetting("Scaffold Mode", "Normal", "Normal");

    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Watchdog", "Watchdog", "NCP", "Backwards");
    public static ModeSetting sprintMode = new ModeSetting("Sprint Mode", "Vanilla", "Vanilla", "Watchdog", "Legit", "None");
    public static ModeSetting watchdogSprint = new ModeSetting("Watchdog Mode", "Jump SameY",  "Jump SameY");
    public static ModeSetting towerMode = new ModeSetting("Tower Mode", "Legit", "Vanilla", "NCP", "Legit", "Watchdog");
    public static NumberSetting delay = new NumberSetting("Delay", 0, 2, 0, 0.05);
    //public static NumberSetting extend = new NumberSetting("Extend", 0, 6, 0, 0.05);
    private final NumberSetting timer = new NumberSetting("Timer", 1, 5, 0.1, 0.1);
    public static BooleanSetting speedcontrol = new BooleanSetting("Custom Speed", false);
    public static NumberSetting customspeed = new NumberSetting("Speed", 4.32, 6.0, 4, .01);
    public static final BooleanSetting safewalk = new BooleanSetting("Safewalk", false);

    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;
    private float y;
    private float speed;
    private final MouseFilter pitchMouseFilter = new MouseFilter();
    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil timerUtil = new TimerUtil();
    public static double keepYCoord;
    private boolean shouldSendPacket;
    private boolean shouldTower;
    private boolean firstJump;
    private boolean pre;
    private int jumpTimer;
    private int slot;
    private int prevSlot;

    boolean onGround = false;




    private float[] cachedRots = new float[2];

    private final Animation anim = new DecelerateAnimation(250, 1);
    float yaw = 0;
    public static double moveTicks = 0;
    int towerTick = 0;
    int index = 0;

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT, "Automatically places blocks under you");
        this.addSettings(mode, rotations, rotationMode, sprintMode, watchdogSprint, towerMode, speedcontrol, customspeed, delay, timer, safewalk);
        customspeed.addParent(speedcontrol, ParentAttribute.BOOLEAN_CONDITION);
        rotationMode.addParent(rotations, ParentAttribute.BOOLEAN_CONDITION);
        watchdogSprint.addParent(sprintMode, sprintMode -> sprintMode.is("Watchdog"));
    }

    @Override
    public void onJumpEvent(JumpEvent event) {
        if(mc.gameSettings.keyBindJump.pressed && (towerMode.is("Watchdog") || towerMode.is("Test")) && MovementUtils.isMoving()) event.cancel();
    }


    @Override
    public void onMotionEvent(MotionEvent e) {
        if(mc.gameSettings.keyBindJump.pressed) keepYCoord = mc.thePlayer.posY - 1;

        if(sprintMode.is("Legit")) {
            if (Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - MathHelper.wrapAngleTo180_float(yaw)) > 90) {
                mc.gameSettings.keyBindSprint.pressed = false;
                mc.thePlayer.setSprinting(false);
            }
            else {
                mc.gameSettings.keyBindSprint.pressed = true;
                mc.thePlayer.setSprinting(true);
            }
        }

        else if(sprintMode.is("None")) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
        }

        else {
            mc.gameSettings.keyBindSprint.pressed = true;
            mc.thePlayer.setSprinting(true);
        }

        // Timer Stuff
        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.timer.timerSpeed = timer.getValue().floatValue();
        }
        if (e.isPre()) {
            if(sprintMode.is("Watchdog") && !mc.gameSettings.keyBindJump.pressed) {
                if(watchdogSprint.is("Jump SameY")) {
                    if(mc.thePlayer.onGround && MovementUtils.isMoving()) {
                        mc.thePlayer.jump();
                    }
                }
            }

            if(towerMode.is("Watchdog")) {
                if(mc.gameSettings.keyBindJump.pressed) {if (mc.thePlayer.onGround) onGround = true;}
                else onGround = false;

            }
            // Rotations
            if (rotations.isEnabled()) {
                float[] rotations = new float[]{0, 0};
                switch (rotationMode.getMode()) {
                    case "Watchdog":
                        rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 180, y};
                        if(mc.thePlayer.onGround && !MovementUtils.isMoving()) {
                            float prevYaw = cachedRots[0];
                            if ((blockCache = ScaffoldUtils.getBlockInfo()) == null) {
                                blockCache = lastBlockCache;
                            }
                            if (blockCache != null && (mc.thePlayer.ticksExisted % 3 == 0
                                    || mc.theWorld.getBlockState(new BlockPos(e.getX(), ScaffoldUtils.getYLevel(), e.getZ())).getBlock() == Blocks.air)) {
                                cachedRots = RotationUtils.getRotations(blockCache.getPosition(), blockCache.getFacing());
                            }
                            rotations = cachedRots;
                            yaw = rotations[0];
                            e.setRotations(rotations[0], rotations[1]);
                            break;
                        }
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "NCP":
                        float prevYaw = cachedRots[0];
                        if ((blockCache = ScaffoldUtils.getBlockInfo()) == null) {
                            blockCache = lastBlockCache;
                        }
                        if (blockCache != null && (mc.thePlayer.ticksExisted % 3 == 0
                                || mc.theWorld.getBlockState(new BlockPos(e.getX(), ScaffoldUtils.getYLevel(), e.getZ())).getBlock() == Blocks.air)) {
                            cachedRots = RotationUtils.getRotations(blockCache.getPosition(), blockCache.getFacing());
                        }
                        rotations = cachedRots;
                        yaw = rotations[0];
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Backwards":
                        rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 180, 77};
                        yaw = rotations[0];
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Down":
                        e.setPitch(90);
                        break;
                    case "Better":
                        float val;
                        if (MovementUtils.isMoving()) {
                            float f = MovementUtils.getMoveYaw(e.getYaw()) - 180;
                            float[] numbers = new float[]{-135, -90, -45, 0, 45, 90, 135, 180};
                            float lastDiff = 999;
                            val = f;
                            for (float v : numbers) {
                                float diff = Math.abs(v - f);
                                if (diff < lastDiff) {
                                    lastDiff = diff;
                                    val = v;
                                }
                            }
                        } else {
                            val = rotations[0];
                        }
                        rotations = new float[]{
                                (val + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationYawHead)) / 2.0F,
                                (77 + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationPitchHead)) / 2.0F};
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Enum":
                        if (lastBlockCache != null) {
                            float yaw = RotationUtils.getEnumRotations(lastBlockCache.getFacing());
                            e.setRotations(yaw, 77);
                        } else {
                            e.setRotations(mc.thePlayer.rotationYaw + 180, 77);
                        }
                        break;
                    case "0":
                        e.setRotations(0, 0);
                        break;
                }
                yaw = rotations[0];

                RotationUtils.setVisualRotations(e);
            }

            // Speed 2 Slowdown

            // Save ground Y level for keep Y
            if (mc.thePlayer.onGround) {
                keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
            }

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                double centerX = Math.floor(e.getX()) + 0.5, centerZ = Math.floor(e.getZ()) + 0.5;
                switch (towerMode.getMode()) {
                    case "Vanilla":
                        mc.thePlayer.motionY = 0.42f;
                        break;

                    case "Watchdog": {
                        if(onGround) {
                            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                                if (MovementUtils.isMoving()) {
                                    if(mc.thePlayer.ticksExisted % 3 == 0) {
                                        PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                                        PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                                    }
                                    towerTick++;
                                    if (mc.thePlayer.onGround)
                                        towerTick = 0;

                                    mc.thePlayer.motionY = 0.41965;
                                    mc.thePlayer.motionX = Math.min(mc.thePlayer.motionX, 0.265);
                                    mc.thePlayer.motionZ = Math.min(mc.thePlayer.motionZ, 0.265);

                                    if (towerTick == 1)
                                        mc.thePlayer.motionY = 0.33;
                                    else if (towerTick == 2)
                                        mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                                    else if (towerTick >= 3)
                                        towerTick = 0;
                                } else {
                                    towerTick = 0;
                                    if (mc.thePlayer.onGround) mc.thePlayer.jump();
                                }
                            } else {
                                towerTick = 0;
                            }
                        }
                    }
                    break;
                    case "NCP":
                        if (!MovementUtils.isMoving() || MovementUtils.getSpeed() < 0.16) {
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42;
                            } else if (mc.thePlayer.motionY < 0.23) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                                mc.thePlayer.motionY = 0.42;
                            }
                        }
                        break;
                }
            }

            // Setting Block Cache
            blockCache = ScaffoldUtils.getBlockInfo();
            if (blockCache != null) {
                lastBlockCache = ScaffoldUtils.getBlockInfo();
            } else {
                return;
            }

            if (mc.thePlayer.ticksExisted % 4 == 0) {
                pre = true;
            }

            // Placing Blocks (Pre)
        } else {
            // Setting Item Slot

            // Placing Blocks (Post)

            pre = false;
        }
    }

//    @Override
//    public void onPlayerMoveUpdateEvent(PlayerMoveUpdateEvent event) {
//        event.setYaw(yaw);
//    }

    public static boolean isAirOrLiquid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();

        return block instanceof BlockAir || block instanceof BlockLiquid;
    }

        @Override
        public void onTickEvent(TickEvent event) {
            if (mc.thePlayer == null) return;
            setSuffix(mode.getMode());

            if(MovementUtils.isMoving()) moveTicks ++;
            else moveTicks = 0;

    //        if (!mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMoving() && !mc.thePlayer.onGround && sprintMode.is("Watchdog")) {
    //            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
    //            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
    //            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.1F;
    //        }
        }


    @Override
    public void onMoveEvent(MoveEvent event) {
        mc.thePlayer.setSprinting(false);
        if(mc.thePlayer.onGround && MovementUtils.isMoving()) {
            if(speedcontrol.isEnabled()) event.setSpeed(customspeed.getValue() / 20);
        }
    }

    private boolean place() {
        int slot = ScaffoldUtils.getBlockSlot();
        if (blockCache == null || lastBlockCache == null || slot == -1) return false;

        if (this.slot != slot) {
            this.slot = slot;

            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
        }

        boolean placed = false;
        if (delayTimer.hasTimeElapsed(delay.getValue() * 1000)) {
            firstJump = false;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(this.slot),
                    lastBlockCache.getPosition(), lastBlockCache.getFacing(),
                    ScaffoldUtils.getHypixelVec3(lastBlockCache))) {
                placed = true;
                y = MathUtils.getRandomInRange(79.5f, 83.5f);
                PacketUtils.sendPacket(new C0APacketAnimation());
            }

            delayTimer.reset();
            blockCache = null;
        }
        return placed;
    }

    @Override
    public void onBlockPlaceable(BlockPlaceableEvent event) {
        place();
    }



    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            if (slot != mc.thePlayer.inventory.currentItem)
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

        }
        mc.timer.timerSpeed = 1;
        mc.gameSettings.keyBindSneak.pressed = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        lastBlockCache = null;
        if (mc.thePlayer != null) {
            prevSlot = mc.thePlayer.inventory.currentItem;
            slot = mc.thePlayer.inventory.currentItem;
            if (mc.thePlayer.isSprinting() && !sprintMode.is("None") && !sprintMode.is("Vanilla") && !sprintMode.is("Legit")) {
                PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
        }
        firstJump = true;
        speed = 1.1f;
        timerUtil.reset();
        jumpTimer = 0;
        y = 80;
        super.onEnable();
    }

    public void renderCounterBlur() {
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        float blockWH = heldItem != null ? 15 : -2;
        int spacing = 3;
        String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
        float textWidth = FluidFont18.getStringWidth(text);

        float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
        x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
        y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
        float height = 20;
        RenderUtil.scissorStart(x - 1.5, y - 1.5 + 150, totalWidth + 3, height + 3);

        RoundedUtil.drawRound(x, y + 150, totalWidth, height, 5, Color.BLACK);
        RenderUtil.scissorEnd();
    }

    public void renderCounter() {
        anim.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        float blockWH = heldItem != null ? 15 : -2;
        int spacing = 3;
        String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
        float textWidth = FluidFont18.getStringWidth(text);

        float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
        x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
        y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
        float height = 20;
        RenderUtil.scissorStart(x - 1.5, y - 1.5 + 150, totalWidth + 3, height + 3);

        RoundedUtil.drawRound(x, y + 150, totalWidth, height, 5, ColorUtil.tripleColor(20, .45f));

        FluidFont18.drawString(text, x + 3 + blockWH + spacing, y + FluidFont18.getMiddleOfBox(height) + .5f + 150, -1);

        if (heldItem != null) {
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x + 3, (int) (y + 10 + 150 - (blockWH / 2)));
            RenderHelper.disableStandardItemLighting();
        }
        RenderUtil.scissorEnd();

    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (e.getPacket() instanceof C0BPacketEntityAction
                && ((C0BPacketEntityAction) e.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING
                && !sprintMode.is("None") && !sprintMode.is("Vanilla") && !sprintMode.is("Legit")) {
            e.cancel();
        }
        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            e.cancel();
        }

        if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement.setStack(mc.thePlayer.inventory.getStackInSlot(slot));
        }

    }

    @Override
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if (safewalk.isEnabled() || ScaffoldUtils.getBlockCount() == 0) {
            event.setSafe(true);
        }
    }


}