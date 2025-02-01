package dev.Fall.utils;


import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

@Getter
public class EnumFacingUtils {
    public EnumFacing enumFacing;
    private final Vec3 offset;
    public EnumFacing getEnumFacing() {
        return this.enumFacing;
    }
    public EnumFacingUtils(final EnumFacing enumFacing, final Vec3 offset) {
        this.enumFacing = enumFacing;
        this.offset = offset;
    }
    public Vec3 getOffset() {
        return this.offset;
    }
}

