package dev.Fall.event.impl.player;

import dev.Fall.event.Event;
import dev.Fall.utils.player.MovementUtils;
import net.minecraft.util.MovementInputFromOptions;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class MoveEvent extends Event {

    private double x, y, z;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getX() {
        return x;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setX(double x) {
        this.x = x;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getY() {
        return y;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setY(double y) {
        this.y = y;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getZ() {
        return z;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setZ(double z) {
        this.z = z;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setSpeed(double speed) {
        MovementUtils.setSpeed(this, speed);
    }
    @Exclude({Strategy.NAME_REMAPPING})
    public void setSneakSlowDownMultiplier(double SneakSlowDownMultiplier) {
        MovementInputFromOptions.sneakMultiplier = SneakSlowDownMultiplier;
    }
}
