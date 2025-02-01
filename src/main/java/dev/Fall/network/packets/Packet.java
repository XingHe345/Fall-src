package dev.Fall.network.packets;

import java.io.IOException;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
public interface Packet {
    void read(PacketBuffer buffer) throws IOException;
    void write(PacketBuffer buffer) throws IOException;
}