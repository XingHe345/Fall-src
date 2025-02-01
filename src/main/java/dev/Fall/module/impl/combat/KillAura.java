package dev.Fall.module.impl.combat;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.utils.PacketUtil;
import dev.Fall.Fall;
import dev.Fall.commands.impl.FriendCommand;
import dev.Fall.event.Event;
import dev.Fall.event.impl.player.*;
import dev.Fall.event.impl.render.Render3DEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.impl.movement.Scaffold;
import dev.Fall.module.impl.player.Blink;
import dev.Fall.module.impl.player.Teams;
import dev.Fall.module.impl.render.HUDMod;
import dev.Fall.module.settings.Setting;
import dev.Fall.module.settings.impl.*;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.player.*;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.server.PacketUtils;
import dev.Fall.utils.time.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

import static dev.Fall.utils.misc.MathUtils.interpolate;

public final class KillAura extends Module {
    public static boolean attacking;

    public static boolean blocking;

    public static boolean wasBlocking;

    public static float yaw = 0.0F;

    private int cps;

    public static EntityLivingBase target;

    public static final List<EntityLivingBase> targets = new ArrayList<>();

    private final TimerUtil attackTimer = new TimerUtil();

    private final TimerUtil switchTimer = new TimerUtil();

    private final MultipleBoolSetting targetsSetting = new MultipleBoolSetting("Targets", new BooleanSetting("Players", true), new BooleanSetting("Animals", false), new BooleanSetting("Mobs", false), new BooleanSetting("Invisibles", false), new BooleanSetting("Villagers", false));

    private final ModeSetting mode = new ModeSetting("Mode", "Single", new String[]{"Single", "Multi", "Switch"});

    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 50.0D, 500.0D, 1.0D, 1.0D);

    private final NumberSetting maxTargetAmount = new NumberSetting("Max Target Amount", 3.0D, 50.0D, 2.0D, 1.0D);

    private final NumberSetting minCPS = new NumberSetting("Min CPS", 10.0D, 20.0D, 1.0D, 1.0D);

    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 10.0D, 20.0D, 1.0D, 1.0D);

    public final NumberSetting reach = new NumberSetting("Reach", 4.0D, 6.0D, 3.0D, 0.1D);
    private final NumberSetting maxrotationspeed = new NumberSetting("Max Rotation Speed", 100, 100, 1, 1);
    private final NumberSetting minrotationspeed = new NumberSetting("Min Rotation Speed", 100, 100, 1, 1);

    private final BooleanSetting autoblock = new BooleanSetting("Autoblock", false);

    private final ModeSetting autoblockMode = new ModeSetting("Autoblock Mode", "Sillydog", "Fake", "Verus", "Sillydog");

    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);

    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Vanilla", "Vanilla", "Custom");

    private final ModeSetting sortMode = new ModeSetting("Sort Mode", "Range", new String[]{"Range", "Hurt Time", "Health", "Armor"});

    private final MultipleBoolSetting addons = new MultipleBoolSetting("Addons", new BooleanSetting[]{new BooleanSetting("Keep Sprint", true), new BooleanSetting("Through Walls", true), new BooleanSetting("Allow Scaffold", false), new BooleanSetting("Movement Fix", false), new BooleanSetting("Ray Cast", false)});

    private final MultipleBoolSetting auraESP = new MultipleBoolSetting("Target ESP", new BooleanSetting[]{new BooleanSetting("Circle", true), new BooleanSetting("Tracer", false), new BooleanSetting("Box", false), new BooleanSetting("Custom Color", false), new BooleanSetting("QuarterCircleRect", true)});

    private final ColorSetting customColor = new ColorSetting("Custom Color", Color.WHITE);

    private EntityLivingBase auraESPTarget;
    private final Animation auraESPAnim;

    public KillAura() {
        super("KillAura", Category.COMBAT, "Automatically attacks players");
        this.auraESPAnim = (Animation) new DecelerateAnimation(300, 1.0D);
        this.autoblockMode.addParent((Setting) this.autoblock, a -> this.autoblock.isEnabled());
        this.rotationMode.addParent((Setting) this.rotations, r -> this.rotations.isEnabled());
        this.switchDelay.addParent((Setting) this.mode, m -> this.mode.is("Switch"));
        this.maxTargetAmount.addParent((Setting) this.mode, m -> this.mode.is("Multi"));
        this.customColor.addParent((Setting) this.auraESP, r -> auraESP.isEnabled("Custom Color"));
        maxrotationspeed.addParent(rotationMode, rotationMode -> rotationMode.is("Custom"));
        minrotationspeed.addParent(rotationMode, rotationMode -> rotationMode.is("Custom"));
        addSettings((Setting) this.targetsSetting, (Setting) this.mode, (Setting) this.maxTargetAmount, (Setting) this.switchDelay, (Setting) this.minCPS, (Setting) this.maxCPS, (Setting) this.reach, (Setting) this.autoblock, (Setting) this.autoblockMode, (Setting) this.rotations, maxrotationspeed, minrotationspeed,
                (Setting) this.rotationMode, (Setting) this.sortMode, (Setting) this.addons, this.auraESP, this.customColor);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }



    public void onDisable() {
        target = null;
        targets.clear();
        blocking = false;
        attacking = false;
        if (wasBlocking)
            PacketUtils.sendPacketNoEvent((Packet) new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        wasBlocking = false;
        super.onDisable();
    }

    public void onMotionEvent(MotionEvent event) {
        setSuffix(this.mode.getMode());
        if (this.minCPS.getValue().doubleValue() > this.maxCPS.getValue().doubleValue())
            this.minCPS.setValue(this.minCPS.getValue().doubleValue() - 1.0D);
        sortTargets();
        if(target == null) yaw = mc.thePlayer.rotationYaw;
        if (event.isPre()) {
            attacking = (!targets.isEmpty() && (this.addons.getSetting("Allow Scaffold").isEnabled() || !Fall.INSTANCE.isEnabled(Scaffold.class)));
            blocking = (this.autoblock.isEnabled() && attacking && InventoryUtils.isHoldingSword());
            if (attacking) {
                target = targets.get(0);
                if (this.rotations.isEnabled()) {
                    float[] rotations = {0.0F, 0.0F};
                    switch (this.rotationMode.getMode()) {
                        case "Vanilla":
                            rotations = RotationUtils.getRotationsNeeded((Entity) target);
                            break;
                        case "Custom":
                            rotations = RotationUtils.getCustomSmoothRotations(target, maxrotationspeed.getValue().floatValue() / 50, minrotationspeed.getValue().floatValue() / 30);
                            break;
                    }
                    //this.yaw = event.getYaw();
                    event.setRotations(rotations[0], rotations[1]);
                    RotationUtils.setVisualRotations(rotations[0], rotations[1]);
                    yaw = event.getYaw();
                }
                if (this.addons.getSetting("Ray Cast").isEnabled() && !RotationUtils.isMouseOver(event.getYaw(), event.getPitch(), (Entity) target, this.reach.getValue().floatValue()))
                    return;
                Random getCPS = new Random();
                cps = getCPS.nextInt(maxCPS.getValue().intValue() - minCPS.getValue().intValue() + 1) + minCPS.getValue().intValue();
                if (attackTimer.hasTimeElapsed(1000 / cps, true)) {
                    if (this.mode.is("Multi")) {
                        for (EntityLivingBase entityLivingBase : targets) {
                            AttackEvent attackEvent = new AttackEvent(entityLivingBase);
                            Fall.INSTANCE.getEventProtocol().handleEvent((Event) attackEvent);
                            if (!attackEvent.isCancelled()) {
                                dev.Fall.viamcp.fixes.AttackOrder.sendFixedAttack((EntityPlayer) mc.thePlayer, (Entity) entityLivingBase);
                            }
                        }
                    } else {
                        AttackEvent attackEvent = new AttackEvent(target);
                        Fall.INSTANCE.getEventProtocol().handleEvent((Event) attackEvent);
                        if (!attackEvent.isCancelled()) {
                            dev.Fall.viamcp.fixes.AttackOrder.sendFixedAttack((EntityPlayer) mc.thePlayer, (Entity) target);
                        }
                    }
                }
            } else {
                target = null;
                this.switchTimer.reset();
            }
        }
        if (blocking) {
            switch (this.autoblockMode.getMode()) {
                case "Sillydog":
                    if (event.isPre()) {
                        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                        PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                        useItem.write(Type.VAR_INT, 1);
                        PacketUtil.sendToServer(useItem, Protocol1_8TO1_9.class, true, true);
                        wasBlocking = true;
                    }
                    break;
                case "Verus":
                    if (event.isPre()) {
                        if (wasBlocking)
                            PacketUtils.sendPacketNoEvent((Packet) new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        PacketUtils.sendPacketNoEvent((Packet) new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        wasBlocking = true;
                    }
                    break;
            }
        } else if (wasBlocking && this.autoblockMode.is("Sillydog") && event.isPre()) {
            PacketUtils.sendPacketNoEvent((Packet) new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            wasBlocking = false;
        }
    }


    private void sortTargets() {
        targets.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (mc.thePlayer.getDistanceToEntity(entity) <= this.reach.getValue().doubleValue() && isValid(entity) && mc.thePlayer != entityLivingBase && !FriendCommand.isFriend(entityLivingBase.getName()))
                    targets.add(entityLivingBase);
            }
        }
        switch (this.sortMode.getMode()) {
            case "Range":
                targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
                break;
            case "Hurt Time":
                targets.sort(Comparator.comparingInt(EntityLivingBase::getHurtTime));
                break;
            case "Health":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "Armor":
                targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
                break;
        }
    }

    public boolean isValid(Entity entity) {
        if(entity instanceof EntityPlayer && !targetsSetting.getSetting("Players").isEnabled()) return false;
        if(entity instanceof EntityVillager && !targetsSetting.getSetting("Villagers").isEnabled()) return false;
        if(entity instanceof EntityAnimal && !targetsSetting.getSetting("Animals").isEnabled()) return false;
        if(entity instanceof EntityMob && !targetsSetting.getSetting("Mobs").isEnabled()) return false;
        if(entity.isInvisible() && !targetsSetting.getSetting("Invisibles").isEnabled()) return false;
        if(!mc.thePlayer.canEntityBeSeen(entity) && !addons.getSetting("Through Walls").isEnabled()) return false;
        if(Fall.INSTANCE.isEnabled(Teams.class) && Teams.isOnSameTeam(entity)) return false;
        if(Fall.INSTANCE.isEnabled(Blink.class) && Blink.addPlayer.isEnabled() && entity.getEntityId() == Blink.blinkEntity.getEntityId()) return false;
        return true;
    }

    public double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
    public static void customRotatedObject2D(float oXpos, float oYpos, float oWidth, float oHeight, float rotate) {
        GL11.glTranslated((double)(oXpos + oWidth / 2.0F), (double)(oYpos + oHeight / 2.0F), 0.0);
        GL11.glRotated((double)rotate, 0.0, 0.0, 1.0);
        GL11.glTranslated((double)(-oXpos - oWidth / 2.0F), (double)(-oYpos - oHeight / 2.0F), 0.0);
    }
    public void drawTargetESP2D(float x, float y, Color color, Color color2, Color color3, Color color4, float scale, int index) {
        long millis = System.currentTimeMillis() + (long) index * 400L;
        double angle = MathHelper.clamp_double((Math.sin((double) millis / 150.0) + 1.0) / 2.0 * 30.0, 0.0, 30.0);
        double scaled = MathHelper.clamp_double((Math.sin((double) millis / 500.0) + 1.0) / 2.0, 0.8, 1.0);
        double rotate = MathHelper.clamp_double((Math.sin((double) millis / 1000.0) + 1.0) / 2.0 * 360.0, 0.0, 360.0);
        rotate += 45.0 - (angle - 15.0);
        float size = 128.0F * scale * (float) scaled;
        float x2 = (x -= size / 2.0F) + size;
        float y2 = (y -= size / 2.0F) + size;
        GlStateManager.pushMatrix();
        customRotatedObject2D(x, y, size, size, (float) rotate);
        GL11.glDisable(3008);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        if (this.auraESP.getSetting("QuarterCircleRect").isEnabled()) {
            drawESPImage(new ResourceLocation("Fluid/rectangle.png"), x, y, x2, y2, HUDMod.color1.getColor(), HUDMod.color2.getColor(), HUDMod.color1.getColor(), HUDMod.color2.getColor());

        }
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GL11.glEnable(3008);
        GlStateManager.popMatrix();
    }
    private static void drawESPImage(ResourceLocation resource, double x, double y, double x2, double y2, Color c, Color c2, Color c3, Color c4) {
        mc.getTextureManager().bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(9, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x, y2, 0.0).tex(0.0, 1.0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        bufferbuilder.pos(x2, y2, 0.0).tex(1.0, 1.0).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        bufferbuilder.pos(x2, y, 0.0).tex(1.0, 0.0).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha()).endVertex();
        GlStateManager.shadeModel(7425);
        GlStateManager.depthMask(false);
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.shadeModel(7424);
    }
    public void onPlayerMoveUpdateEvent(PlayerMoveUpdateEvent event) {
        if (this.addons.getSetting("Movement Fix").isEnabled() && target != null) {
            event.setYaw(yaw);
        }
    }

    public void onJumpFixEvent(JumpFixEvent event) {
        if (this.addons.getSetting("Movement Fix").isEnabled() && target != null)
            event.setYaw(yaw);
    }

    public void onKeepSprintEvent(KeepSprintEvent event) {
        if (this.addons.getSetting("Keep Sprint").isEnabled())
            event.cancel();
    }
    private static Vector3d project2D(int scaleFactor, double x, double y, double z) {
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(   2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, vector) ? new Vector3d((double)(vector.get(0) / (float)scaleFactor), (double)(((float) Display.getHeight() - vector.get(1)) / (float)scaleFactor), (double)vector.get(2)) : null;
    }
    public static Vector2f targetESPSPos(EntityLivingBase entity) {
        EntityRenderer entityRenderer = mc.entityRenderer;
        float partialTicks = mc.timer.renderPartialTicks;
        int scaleFactor = (new ScaledResolution(mc)).getScaleFactor();
        double x = interpolate(entity.posX, entity.prevPosX, (double)partialTicks);
        double y = interpolate(entity.posY, entity.prevPosY, (double)partialTicks);
        double z = interpolate(entity.posZ, entity.prevPosZ, (double)partialTicks);
        double height = (double)(entity.height / (entity.isChild() ? 1.75F : 1.0F) / 2.0F);
        double width = 0.0;
        AxisAlignedBB aabb = new AxisAlignedBB(x - 0.0, y, z - 0.0, x + 0.0, y + height, z + 0.0);
        Vector3d[] vectors = new Vector3d[]{new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)};
        entityRenderer.setupCameraTransform(partialTicks, 0);
        Vector4d position = null;
        Vector3d[] vecs3 = vectors;
        int vecLength = vectors.length;

        for(int vecI = 0; vecI < vecLength; ++vecI) {
            Vector3d vector = vecs3[vecI];
            vector = project2D(scaleFactor, vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
            if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                if (position == null) {
                    position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                }

                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
        }

        entityRenderer.setupOverlayRendering();
        if (position != null) {
            return new Vector2f((float)position.x, (float)position.y);
        } else {
            return null;
        }
    }
    public void onRender3DEvent(Render3DEvent event) {
        this.auraESPAnim.setDirection((target != null) ? Direction.FORWARDS : Direction.BACKWARDS);
        if (target != null)
            this.auraESPTarget = target;
        if (this.auraESPAnim.finished(Direction.BACKWARDS))
            this.auraESPTarget = null;
        Color color = (Color) HUDMod.getClientColors().getFirst();
        if (this.auraESP.isEnabled("Custom Color"))
            color = this.customColor.getColor();
        if (this.auraESPTarget != null) {
            if (this.auraESP.getSetting("Box").isEnabled())
                RenderUtil.renderBoundingBox(this.auraESPTarget, color, this.auraESPAnim.getOutput().floatValue());
            if (this.auraESP.getSetting("Circle").isEnabled())
                RenderUtil.drawCircle((Entity) this.auraESPTarget, event.getTicks(), 0.75D, color.getRGB(), this.auraESPAnim.getOutput().floatValue());
            if (this.auraESP.getSetting("Tracer").isEnabled()) {
                RenderUtil.drawTracerLine((Entity) this.auraESPTarget, 4.0F, Color.BLACK, this.auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine((Entity) this.auraESPTarget, 2.5F, color, this.auraESPAnim.getOutput().floatValue());
            }
            if (this.auraESP.getSetting("QuarterCircleRect").isEnabled()) {
                float dst = mc.thePlayer.getDistanceToEntity(target);
                drawTargetESP2D((Objects.requireNonNull(targetESPSPos(target))).x, (Objects.requireNonNull(targetESPSPos(target))).y, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, (1.0F - MathHelper.clamp_float(Math.abs(dst - 6.0F) / 60.0F, 0.0F, 0.75F)) * 1.0F, 1);
            }
        }
    }
}
