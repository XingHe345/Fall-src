package dev.Fall.network.packets.server.handlers;


import dev.Fall.Fall;
import dev.Fall.network.packets.PacketHandler;
import dev.Fall.network.packets.server.ServerHandshakePacket;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
public class HandshakePacketHandler implements PacketHandler<ServerHandshakePacket> {

    @Override
    public void handle(ServerHandshakePacket packet) {
        if (packet.getStatus() == 1) {
            Fall.INSTANCE.logger.info("Handshake successful: " + packet.getMessage());
        } else {
            Fall.INSTANCE.logger.error("Handshake failed: " + packet.getMessage());
        }
    }
}
