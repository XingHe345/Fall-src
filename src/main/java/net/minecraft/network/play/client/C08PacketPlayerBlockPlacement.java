package net.minecraft.network.play.client;

import dev.Fall.module.impl.misc.PacketFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;

import java.io.IOException;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer> {
    private static final BlockPos NEGATIVE = new BlockPos(-1, -1, -1);
    private BlockPos position;
    private int placedBlockDirection;
    private static ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;

    public C08PacketPlayerBlockPlacement() {
    }

    public C08PacketPlayerBlockPlacement(ItemStack stackIn) {
        this(NEGATIVE, 255, stackIn, 0.0F, 0.0F, 0.0F);
    }

    public C08PacketPlayerBlockPlacement(BlockPos positionIn, int placedBlockDirectionIn, ItemStack stackIn, float facingXIn, float facingYIn, float facingZIn) {
        this.position = positionIn;
        this.placedBlockDirection = placedBlockDirectionIn;
        this.stack = stackIn != null ? stackIn.copy() : null;
        this.facingX = facingXIn;
        this.facingY = facingYIn;
        this.facingZ = facingZIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.position = buf.readBlockPos();
        this.placedBlockDirection = buf.readUnsignedByte();
        this.stack = buf.readItemStackFromBuffer();
        this.facingX = (float) buf.readUnsignedByte() / 16.0F;
        this.facingY = (float) buf.readUnsignedByte() / 16.0F;
        this.facingZ = (float) buf.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.position);
        buf.writeByte(this.placedBlockDirection);
        buf.writeItemStackToBuffer(this.stack);
        float i = PacketFix.fixC08Packet();
        buf.writeByte((int) (this.facingX * i));
        buf.writeByte((int) (this.facingY * i));
        buf.writeByte((int) (this.facingZ * i));
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processPlayerBlockPlacement(this);
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public void setPosition(BlockPos pos) {
        position = pos;
    }

    public int getPlacedBlockDirection() {
        return this.placedBlockDirection;
    }

    public void setPlacedBlockDirection(int value) {
        placedBlockDirection = value;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public static void setStack(ItemStack itemStack) {
        stack = itemStack;
    }

    /**
     * Returns the offset from xPosition where the actual click took place.
     */
    public float getPlacedBlockOffsetX() {
        return this.facingX;
    }

    /**
     * Returns the offset from yPosition where the actual click took place.
     */
    public float getPlacedBlockOffsetY() {
        return this.facingY;
    }

    /**
     * Returns the offset from zPosition where the actual click took place.
     */
    public float getPlacedBlockOffsetZ() {
        return this.facingZ;
    }


    @Override
    public int getID() {
        return 14;
    }

}
