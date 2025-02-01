package net.minecraft.util;

import lombok.Getter;

@Getter
public class Vector3d {
    public double x;
    public double y;
    public double z;

    public Vector3d(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d subtract(final Vector3d subtract) {
        this.x -= subtract.x;
        this.y -= subtract.y;
        this.z -= subtract.z;
        return this;
    }

    public Vector3d add(final double x, final double y, final double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d() {
        this.x = this.y = this.z = 0.0D;
    }
}