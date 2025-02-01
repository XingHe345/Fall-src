package dev.Fall.network;


import dev.Fall.Fall;
import dev.Fall.module.impl.combat.KillAura;
import dev.Fall.module.impl.misc.IRC;
import dev.Fall.network.packets.Packet;
import dev.Fall.network.packets.PacketBuffer;
import dev.Fall.network.packets.PacketManager;
import dev.Fall.network.packets.PacketRegistry;
import dev.Fall.network.packets.client.ClientGetRankPacket;
import dev.Fall.network.packets.client.ClientHandshakePacket;
import dev.Fall.network.packets.server.ServerHandshakePacket;
import dev.Fall.utils.player.ChatUtil;
import lombok.Getter;
import lombok.Setter;




import java.io.IOException;
import java.net.Socket;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
//真奇怪你等着去试试本地连接能不能行
@Getter
@Setter
public class SocketClient {
    private Socket socket;
    private PacketBuffer packetBuffer;
    private PacketManager packetManager;
    private boolean isConnected = false;

    public void connect(String host, int port) throws IOException {
        try {
            socket = new Socket(host, port);
            isConnected = true;
            ChatUtil.display(false, "§7[§b§lIRC§r§7]Connected to server");

            packetBuffer = new PacketBuffer(socket.getInputStream(), socket.getOutputStream());
            packetManager = new PacketManager();
            PacketRegistry packetRegistry = new PacketRegistry(packetManager);
            packetRegistry.register();

            process();
        } catch (IOException e) {
            ChatUtil.display(false, "§7[§b§lIRC§r§7]Failed to connect to the server: " + e.getMessage());
        }
    }

    private void process() {
        try {
            packetManager.sendPacket(packetBuffer, new ClientHandshakePacket(1), 1);
            while (!socket.isClosed()) {
                Packet packet = packetManager.processPacket(packetBuffer);
                if (packet instanceof ServerHandshakePacket) {
                        if (((ServerHandshakePacket) packet).getStatus() == 1) {
                            ClientGetRankPacket getRankPacket = new ClientGetRankPacket(Fall.INSTANCE.userManager.getUser().getUsername());
                            packetManager.sendPacket(packetBuffer, getRankPacket, 2);
                        }
                    }
            }
        } catch (Exception e) {
            ChatUtil.display(false, "§7[§b§lIRC§r§7]Left the server.");
        }
    }

    public void close() {
        try {
            if (packetBuffer != null) {
                packetBuffer.close();
            }
            if (socket != null) {
                socket.close();
                isConnected = false;
            }
        } catch (IOException e) {
            ChatUtil.display(false, "§7[§b§lIRC§r§7]Error closing client resources: " + e.getMessage());
            IRC irc = Fall.INSTANCE.getModuleCollection().getModule(IRC.class);
            irc.onDisable();
        }
    }
}
