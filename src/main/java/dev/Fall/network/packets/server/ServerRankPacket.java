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
@PacketID(2)
public class ServerRankPacket implements Packet {
    private String rank;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.rank = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(rank);
    }
}