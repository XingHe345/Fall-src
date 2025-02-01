package dev.Fall.event.impl.player;

import dev.Fall.event.Event;
import dev.Fall.utils.player.MovementUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static dev.Fall.utils.Utils.mc;

@Getter
@Setter
@AllArgsConstructor
public class PlayerMoveUpdateEvent extends Event {

    private float strafe, forward, friction, yaw;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        MovementUtils.stop();
    }

}