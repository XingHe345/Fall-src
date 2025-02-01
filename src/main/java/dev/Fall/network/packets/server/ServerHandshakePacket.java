package dev.Fall.network.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import dev.Fall.network.packets.Packet;
import dev.Fall.network.packets.PacketBuffer;
import dev.Fall.network.packets.PacketID;

import java.io.IOException;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PacketID(1)
public class ServerHandshakePacket implements Packet {
    private int status;
    private String message;

    public ServerHandshakePacket(int status) {
        this.status = status;
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.status = buffer.readInt();
        this.message = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(status);
        buffer.writeString(message);
    }
}