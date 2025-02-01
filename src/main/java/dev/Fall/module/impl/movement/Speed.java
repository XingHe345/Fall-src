package dev.Fall.module.impl.movement;

import dev.Fall.Fall;
import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.event.impl.player.JumpEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.ParentAttribute;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.ModeSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.player.MovementUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import dev.Fall.utils.player.PlayerUtil;
import net.minecraft.potion.Potion;

@SuppressWarnings("unused")
public final class Speed extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "Custom", "Custom", "Entity", "Watchdog");
    public static ModeSetting watchdogmode = new ModeSetting("Watchdog Mode", "Ground", "Ground", "Glide Strafe", "Full Strafe");
    public static ModeSetting autojump = new ModeSetting("Jump", "When Move", "When Move", "Always", "No Jump", "none");
    public static BooleanSetting fallStrafe = new BooleanSetting("Fall Strafe", true);
    public static BooleanSetting extraStrafe = new BooleanSetting("Extra Strafe", true);
    public static BooleanSetting frictionOverride = new BooleanSetting("Friction Override", true);
    public static BooleanSetting onlyinAir = new BooleanSetting("Only in air", false);
    public static BooleanSetting onlyonGround = new BooleanSetting("Only on ground", false);
    public static NumberSetting inAirSpeed = new NumberSetting("In air speed", 1, 5, 0.1, .1);
    public static NumberSetting onGroundSpeed = new NumberSetting("On Ground speed", 1, 5, 0.1, .1);
    public static BooleanSetting speed = new BooleanSetting("Speed", false);
    public static BooleanSetting air = new BooleanSetting("OnlyAir", false);
    public static BooleanSetting keepsprint = new BooleanSetting("Sprint", false);
    public static BooleanSetting sprintup = new BooleanSetting("SprintUP", false);
    public static BooleanSetting okstrafe = new BooleanSetting("Strafe", false);
    public static BooleanSetting hurtcancle = new BooleanSetting("HurtCancel", false);
    public static NumberSetting Speed = new NumberSetting("Speed-value", 0.1, 5, 0.1, .1);
    public static NumberSetting Speed2 = new NumberSetting("Speed2-value", 0.1, 5, 0.1, .1);
    public static NumberSetting range = new NumberSetting("Speed-range", 0.1, 3, 0.1, .1);
    public static NumberSetting strafe = new NumberSetting("Strafe-value", 0.1, 3, 0.1, .1);
    public static BooleanSetting fastStop = new BooleanSetting("Fast Stop", false);
    int onGroundticks = 0;
    boolean sprint = false;
    boolean strgo = false;
    int speedf = 0;
    int f = 0;
    private boolean disable;
    private boolean disable3;
    public boolean couldStrafe;
    private boolean recentlyCollided;
    private boolean ice;
    private boolean slab;
    private int boostTicks;


    public Speed() {
        super("Speed", Category.MOVEMENT, "Makes you go faster");
        onlyinAir.addParent(mode, modeSetting -> mode.is("Custom"));
        onlyonGround.addParent(mode, modeSetting -> mode.is("Custom"));
        onGroundSpeed.addParent(onlyonGround, ParentAttribute.BOOLEAN_CONDITION);
        inAirSpeed.addParent(onlyinAir, ParentAttribute.BOOLEAN_CONDITION);
        autojump.addParent(mode, modeSetting -> modeSetting.is("Custom"));
        fallStrafe.addParent(watchdogmode, modeSetting -> modeSetting.is("Full Strafe"));
        extraStrafe.addParent(watchdogmode, modeSetting -> modeSetting.is("Full Strafe"));
        frictionOverride.addParent(watchdogmode, modeSetting -> modeSetting.is("Full Strafe"));
        Speed.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        sprintup.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        speed.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        air.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        keepsprint.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        hurtcancle.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        okstrafe.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        Speed2.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        range.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        strafe.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        watchdogmode.addParent(mode, modeSetting -> modeSetting.is("Watchdog"));
        addSettings(mode, autojump, fallStrafe, extraStrafe, frictionOverride, speed, air, keepsprint, sprintup, okstrafe, hurtcancle, Speed, Speed2, range, strafe, onlyonGround, onGroundSpeed, onlyinAir, inAirSpeed, watchdogmode, fastStop);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (mc.thePlayer.onGround) {
            onGroundticks++;
            if (mode.is("Custom")) {
                switch (autojump.getMode()) {
                    case "When Move": {
                        if (MovementUtils.isMoving()) mc.thePlayer.jump();
                    }
                    break;

                    case "Always": {
                        mc.thePlayer.jump();
                    }
                    break;
                }
            }
        } else onGroundticks = 0;

        if (fastStop.isEnabled() && !MovementUtils.isMoving()) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        }

        if (mc.thePlayer.isInWeb || mc.thePlayer.isInLava() || mc.thePlayer.isInWater()) return;
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Custom": {
                if (MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround && onlyonGround.isEnabled())
                        MovementUtils.strafe(onGroundSpeed.getValue() / 10);

                    if (!mc.thePlayer.onGround && onlyinAir.isEnabled())
                        MovementUtils.strafe(inAirSpeed.getValue() / 10);
                }
            }
            break;

            case "Watchdog":
                mc.gameSettings.keyBindJump.pressed = false;
                if (watchdogmode.is("Ground")) {
                    if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                        mc.thePlayer.jump();
                        MovementUtils.strafe(0.45);
                    }
                }
                else if(watchdogmode.is("Glide Strafe")) {
                    if(MovementUtils.isMoving()) {
                        if((mc.thePlayer.offGroundTicks == 10) && MovementUtils.isOnGround(0.769)) {
                            mc.thePlayer.motionY = 0;
                        }

                        if(MovementUtils.isOnGround(0.769) && mc.thePlayer.offGroundTicks >= 9) {
                            MovementUtils.strafe(0.29);
                        }

                        if(mc.thePlayer.onGround) {
                            if(mc.gameSettings.keyBindForward.pressed) MovementUtils.strafe(0.28);
                            else MovementUtils.strafe(0.45);
                            mc.thePlayer.jump();
                        }
                    }
                }
                 else if (watchdogmode.is("Full Strafe")) {
                    MovementUtils.useDiagonalSpeed();
                    Scaffold scaffold = Fall.INSTANCE.getModuleCollection().getModule(Scaffold.class);
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.onGround) {
                        if (!(scaffold.isEnabled()) && !recentlyCollided) {
                            MovementUtils.strafe(fallStrafe.isEnabled() ? MovementUtils.getAllowedHorizontalDistance() : MovementUtils.getAllowedHorizontalDistance() * 0.994);
                            couldStrafe = true;
                            mc.thePlayer.jump();
                        } else if ((scaffold.isEnabled()) && !recentlyCollided) {
                            MovementUtils.strafe(0.29);
                            mc.thePlayer.jump();
                        } else {
                            MovementUtils.strafe(0.29);
                            couldStrafe = true;
                            mc.thePlayer.jump();
                        }


                    } else if (mc.thePlayer.onGround) {

                        if (!recentlyCollided) {
                            MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance());

                        } else if ((scaffold.isEnabled())) {
                            MovementUtils.strafe(.23);
                        } else {
                            //  ChatUtil.display("s");
                            MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
                        }
                        mc.thePlayer.jump();
                    }

                    if (mc.thePlayer.offGroundTicks == 1 && !disable) {
                        mc.thePlayer.motionY += 0.057f;

                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && !(scaffold.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 >= 2 && !disable && !recentlyCollided) {
                            MovementUtils.strafe(0.48);
                            couldStrafe = true;

                        } else if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 >= 2) {
                            MovementUtils.strafe(0.4);
                            couldStrafe = true;

                        } else if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 1) {
                            MovementUtils.strafe(0.405);
                            couldStrafe = true;
                        } else {
                            MovementUtils.strafe(0.33);
                            couldStrafe = true;
                        }
                    }


                    if (mc.thePlayer.offGroundTicks == 2 && !disable && extraStrafe.isEnabled()) {
                        double motionX3 = mc.thePlayer.motionX;
                        double motionZ3 = mc.thePlayer.motionZ;
                        mc.thePlayer.motionZ = (mc.thePlayer.motionZ * 1 + motionZ3 * 2) / 3;
                        mc.thePlayer.motionX = (mc.thePlayer.motionX * 1 + motionX3 * 2) / 3;
                    }


                    if (mc.thePlayer.offGroundTicks == 3 && !disable) {
                        mc.thePlayer.motionY -= 0.1309f;
                    }

                    if (mc.thePlayer.offGroundTicks == 4 && !disable) {
                        mc.thePlayer.motionY -= 0.2;
                    }

                    if (mc.thePlayer.offGroundTicks == 6 && !disable && (PlayerUtil.blockRelativeToPlayer(0, mc.thePlayer.motionY * 3, 0) != Blocks.air && fallStrafe.isEnabled())) {
                        mc.thePlayer.motionY += 0.075;
                        MovementUtils.strafe();
                        double hypotenuse = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
                        if ((hypotenuse < MovementUtils.getAllowedHorizontalDistance() || mc.thePlayer.motionX == 0 || mc.thePlayer.motionZ == 0) && !disable && (!recentlyCollided && mc.thePlayer.isPotionActive(Potion.moveSpeed)) && !scaffold.isEnabled()) {
                            MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance() - 0.01);
                            couldStrafe = true;

                        } else if (!disable && !scaffold.isEnabled() && (hypotenuse < MovementUtils.getAllowedHorizontalDistance() || mc.thePlayer.motionX == 0 || mc.thePlayer.motionZ == 0)) {
                            MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance() - 0.05);
                            couldStrafe = true;
                        }
                    }

                    if (mc.thePlayer.offGroundTicks < 7 && (PlayerUtil.blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) != Blocks.air) && mc.thePlayer.isPotionActive(Potion.moveSpeed) && !slab) {
                        boostTicks = mc.thePlayer.ticksExisted + 9;
                        recentlyCollided = true;
                    }

                    /*if (mc.thePlayer.offGroundTicks == 7 && !disable && (PlayerUtils.blockRelativeToPlayer(0, mc.thePlayer.motionY * 2, 0) != Blocks.air) && !getModule(Scaffold.class).isEnabled()) {
                        MovementUtils.strafe(fallStrafe.get() ? MovementUtils.getSpeed() : MovementUtils.getAllowedHorizontalDistance() * 1.1);
                        couldStrafe = true;
                    }*/


                    if (PlayerUtil.blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) != Blocks.air && !disable && fallStrafe.isEnabled() && (mc.thePlayer.offGroundTicks > 7) && !disable3) {
                        MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance() * 1.079);
                        couldStrafe = true;
                        disable3 = true;
                    } else if (PlayerUtil.blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) != Blocks.air && !disable && mc.thePlayer.offGroundTicks > 6 && !disable3) {
                        MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance());
                        couldStrafe = true;
                        disable3 = true;
                    } else if (PlayerUtil.blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) != Blocks.air && mc.thePlayer.offGroundTicks > 5 && !disable3) {
                        MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance());
                        couldStrafe = true;
                        disable3 = true;
                    }


                    double speed2 = Math.hypot((mc.thePlayer.motionX - (mc.thePlayer.lastTickPosX - mc.thePlayer.lastLastTickPosX)), (mc.thePlayer.motionZ - (mc.thePlayer.lastTickPosZ - mc.thePlayer.lastLastTickPosZ)));
                    if (speed2 < .0125 && frictionOverride.isEnabled()) {
                        MovementUtils.strafe();
                        couldStrafe = true;
                    }
                }
                break;

            case "Entity": {
                for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                    if (entity.getEntityId() != mc.thePlayer.getEntityId() && mc.thePlayer.getDistanceToEntity(entity) <= range.getValue() && (!air.getConfigValue() || !mc.thePlayer.onGround)) {
                        EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                        if (mc.thePlayer.hurtTime > 0 && hurtcancle.getConfigValue())
                            return;
                        if (speed.getConfigValue()) {
                            speedf = (int) (Speed.getValue() + Speed2.getValue() * (1 - mc.thePlayer.getDistanceToEntity(entity) / range.getValue()));
                        }
                        if (keepsprint.getConfigValue()) {
                            sprint = true;
                        }
                        if (sprintup.getConfigValue()) {
                            mc.thePlayer.motionX *= (1 + (speedf * 0.01));
                            mc.thePlayer.motionZ *= (1 + (speedf * 0.01));
                        }
                        if (okstrafe.getConfigValue() && mc.thePlayer.getDistanceToEntity(entity) <= strafe.getValue()) {
                            strgo = true;
                        }
                        return;
                    }
                }
                sprint = false;
                strgo = false;
            }
            break;

        }
    }
    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook){
            f = 10;
        }
    }

    @Override
    public void onTickEvent(TickEvent event) {
    }


    @Override
    public void onDisable() {
        onGroundticks = 0;
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }


    @Override
    public void onJumpEvent(JumpEvent event) {
        if (mode.is("Custom") || (mode.is("Watchdog"))) {
            if (autojump.is("No Jump")) {
                event.cancel();
            }
        }
    }

}
