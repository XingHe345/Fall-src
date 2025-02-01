package dev.Fall.network.packets.server.handlers;


import dev.Fall.Fall;
import dev.Fall.network.packets.PacketHandler;
import dev.Fall.network.packets.server.ServerRankPacket;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
public class RankPacketHandler implements PacketHandler<ServerRankPacket> {
    @Override
    public void handle(ServerRankPacket packet) {
        Fall.INSTANCE.logger.info("Received rank: " + packet.getRank());
        Fall.INSTANCE.userManager.update(packet.getRank());
    }
}