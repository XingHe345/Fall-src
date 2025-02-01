package dev.Fall.module.impl.movement;

import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.ModeSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoWeb extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Grim");
    public final BooleanSetting Web = new BooleanSetting("Web",true);
    public final BooleanSetting Liquiuds = new BooleanSetting("Liquids",true);
    public NoWeb() {
        super("NoWeb",Category.MOVEMENT,"NoWeb");
        addSettings(mode, Web, Liquiuds);
    }
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
    }
    @Override
    public void onMotionEvent(MotionEvent e) {
        setSuffix(this.mode.getMode());
        if (e.isPost()) {
            return;
        }
        if (!mc.thePlayer.isInWeb) {
            return;
        }
        switch (this.mode.getMode()) {
            case "Grim": {
                for (int i = -3;i < 3;i++) {
                    for (int i2 = -12 ;i2 < 12;i2++) {
                        final BlockPos playerPos = new BlockPos(mc.thePlayer);
                        BlockPos[] blockPoses = new BlockPos[]{playerPos.add(i2, i, 7), playerPos.add(i2, i, -7), playerPos.add(7, i, i2), playerPos.add(-7, i, i2)};
                        for (BlockPos blockPos : blockPoses) {
                            final IBlockState blockState = mc.theWorld.getBlockState(blockPos);
                            final Block block = blockState.getBlock();

                            if (block instanceof BlockLiquid && Liquiuds.isEnabled()) {
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                mc.theWorld.setBlockToAir(blockPos);
                            }

                            if (block instanceof BlockWeb && Web.isEnabled()) {
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                mc.theWorld.setBlockToAir(blockPos);
                            }
                        }
                    }
                }
            }
            break;
            case "Vanilla": {
                mc.thePlayer.isInWeb = false;
                break;
            }
        }
    }
}
