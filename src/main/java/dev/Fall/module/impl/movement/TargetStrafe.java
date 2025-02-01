package dev.Fall.module.impl.movement;

import dev.Fall.Fall;
import dev.Fall.event.impl.player.MoveEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.impl.combat.KillAura;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import dev.Fall.utils.player.MovementUtils;
import dev.Fall.utils.player.RotationUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import static net.minecraft.util.MathHelper.cos;
import static net.optifine.util.MathUtils.asin;

public class TargetStrafe extends Module {
    private final BooleanSetting onlyGround = new BooleanSetting("only OnGround", false);
    private final BooleanSetting onlyhurttime = new BooleanSetting("only Damage", false);
    private final NumberSetting radius = new NumberSetting("Radius", 3, 5, 0.1, .1);
    public TargetStrafe() {
        super("Target Strafe", Category.MOVEMENT, "Move Around the a target.");
        addSettings(onlyGround, onlyhurttime, radius);
    }

    public static void targetStrafe(EntityLivingBase target, float direction_, float radius, MoveEvent event, int mathRadius) {
        if(!MovementUtils.isMoving()) return;
        float forward_ = 0.0F;
        float strafe_ = 0.0F;
        float speed_ = MathHelper.sqrt_float((float) (event.getX() * event.getX() + event.getZ() * event.getZ()));
        float _direction = 0.0f;
        if(speed_ <= 0.0001) return;
        if(direction_ > 0.001) {
            _direction = 1.0F;
        }else if(direction_ < -0.001) {
            _direction = -1.0F;
        }
        float curDistance = 0.01F;
        if (mathRadius == 1) {
            curDistance = mc.thePlayer.getDistanceToEntity(target);
        }else if (mathRadius == 0) {
            curDistance = (float) Math.sqrt((mc.thePlayer.posX - target.posX) * (mc.thePlayer.posX - target.posX) + (mc.thePlayer.posZ - target.posZ) * (mc.thePlayer.posZ - target.posZ));
        }
        if(curDistance < radius - speed_) {
            forward_ = -1.0F;
        }else if(curDistance > radius + speed_) {
            forward_ = 1.0F;
        }else {
            forward_ = (curDistance - radius) / speed_;
        }
        if(curDistance < radius + speed_*2 && curDistance > radius - speed_*2) {
            strafe_ = 1.0F;
        }
        strafe_ *= _direction;
        float strafeYaw = RotationUtils.getRotationsNeeded(target)[0];
        float covert_ = (float) Math.sqrt(forward_ * forward_ + strafe_ * strafe_);

        forward_ /= covert_;
        strafe_ /= covert_;
        float turnAngle = (float) Math.toDegrees(asin(strafe_));
        if(turnAngle > 0) {
            if(forward_ < 0)
                turnAngle = 180F - turnAngle;
        }else {
            if(forward_ < 0)
                turnAngle = -180F - turnAngle;
        }
        strafeYaw = (float) Math.toRadians((strafeYaw + turnAngle));
        event.setX(-Math.sin(strafeYaw) * speed_);
        event.setZ(cos(strafeYaw) * speed_);
        mc.thePlayer.motionX = event.getX();
        mc.thePlayer.motionZ = event.getZ();
    }

    @Override
    public void onMoveEvent(MoveEvent event) {
        if(!onlyGround.isEnabled() || mc.thePlayer.isOnGround()) {
            if(!onlyhurttime.isEnabled() || mc.thePlayer.hurtTime >= 1) {
                if(Fall.INSTANCE.isEnabled(KillAura.class)) {
                    if(KillAura.target == null) return;
                    else {
                        targetStrafe(KillAura.target, -1, radius.getValue().floatValue(), event, radius.getValue().intValue());
                    }
                }
            }
        }
    }
}
