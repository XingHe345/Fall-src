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
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientChatMessagePacket implements Packet {
    private String message;
    private String username;
    private String rank;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.message = buffer.readString();
        this.username = buffer.readString();
        this.rank = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(message);
        buffer.writeString(username);
        buffer.writeString(rank);
    }
}
