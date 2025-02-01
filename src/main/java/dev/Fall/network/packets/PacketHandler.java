package dev.Fall.network.packets;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
public interface PacketHandler<T extends Packet> {
    void handle(T packet);
}
