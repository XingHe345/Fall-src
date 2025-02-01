package dev.Fall.module.impl.misc;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.vialoadingbase.ViaLoadingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import dev.Fall.viamcp.ViaMCP;
public class PacketFix extends Module {

    public PacketFix() {
        super("PacketFix", Category.MISC,"");
    }

    public static float fixC08Packet() {
            if (ViaLoadingBase.getInstance().getNativeVersion() > ProtocolVersion.v1_8.getVersion()) {
            return 1.0F;
        } else {
            return 16.0F;
        }
    }

    public static Double fixMotion() {
        if (ViaLoadingBase.getInstance().getNativeVersion() > ProtocolVersion.v1_8.getVersion()) {
            return 0.003D;
        } else {
            return 0.005D;
        }
    }

    public static float fixHitbox() {
        if (ViaLoadingBase.getInstance().getNativeVersion() > ProtocolVersion.v1_8.getVersion()) {
            return -0.1F;
        } else {
            return 0F;
        }
    }

    public static float fixLadder() {
        if (ViaLoadingBase.getInstance().getNativeVersion() > ProtocolVersion.v1_8.getVersion()) {
            return 0.1875F;
        } else {
            return 0.125F;
        }
    }

    public static AxisAlignedBB fixLilyPad(BlockPos pos, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (ViaLoadingBase.getInstance().getNativeVersion() > ProtocolVersion.v1_8.getVersion()) {
            return new AxisAlignedBB((double) pos.getX() + 0.0625D,
                    (double) pos.getY() + 0.0D,
                    (double) pos.getZ() + 0.0625D,
                    (double) pos.getX() + 0.9375D,
                    (double) pos.getY() + 0.09375D,
                    (double) pos.getZ() + 0.9375D);
        } else {
            return new AxisAlignedBB((double) pos.getX() + minX,
                    (double) pos.getY() + minY,
                    (double) pos.getZ() + minZ,
                    (double) pos.getX() + maxX,
                    (double) pos.getY() + maxY,
                    (double) pos.getZ() + maxZ);
        }
    }
}
