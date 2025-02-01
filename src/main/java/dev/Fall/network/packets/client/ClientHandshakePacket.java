package dev.Fall.network.packets.client;

import dev.Fall.network.packets.Packet;
import dev.Fall.network.packets.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.IOException;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientHandshakePacket implements Packet {
    private int protocolVersion;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.protocolVersion = buffer.readInt();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(protocolVersion);
    }
}