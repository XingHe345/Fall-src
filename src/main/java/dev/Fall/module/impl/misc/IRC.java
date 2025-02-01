package dev.Fall.module.impl.misc;



import dev.Fall.Fall;
import dev.Fall.event.impl.player.PlayerSendMessageEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.network.SocketClient;
import dev.Fall.network.packets.Packet;
import dev.Fall.network.packets.client.ClientChatMessagePacket;
import dev.Fall.network.user.User;
import dev.Fall.utils.player.ChatUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class IRC extends Module {
    private String ircPrefix = "@";
    private SocketClient client;
    private Thread thread;

    public IRC() {
        super("IRC", Category.MISC,"IRC");
      //  Fall.INSTANCE.userManager.setUser(new User("Shirena", "123", "Owner", Date.from(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), ""));
       // Fall.INSTANCE.userManager.setUser(new User("IDeal", "123", "Dev", Date.from(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), ""));
        client = new SocketClient();
    }

    @Override
    public void onEnable() {
        thread = new Thread(() -> {
            try {
                client.connect("221.131.165.85", 52611);
            } catch (IOException e) {
                ChatUtil.display(false, "§7[§b§lIRC§r§7]Error connecting server: " + e.getMessage());
            }
        });
        thread.start();
    }

    @Override
    public void onDisable() {
        if (client != null) {
            client.close(); // 关闭连接
        }
    }

    @Override
    public void onPlayerSendMessageEvent(PlayerSendMessageEvent event) {
        String message = event.getMessage();

        if (message.startsWith(ircPrefix)) {
            event.cancel();

            String msg = message.substring(ircPrefix.length());

            User user = Fall.INSTANCE.userManager.getUser();
            sendPacket(new ClientChatMessagePacket(msg, user.getUsername(), user.getRank()));
        }
    }

    public void sendPacket(Packet packet) {
        try {
            client.getPacketManager().sendPacket(client.getPacketBuffer(), packet, 3);
        } catch (IOException e) {
            ChatUtil.display(false, "§7[§b§lIRC§r§7]Error sending packet: " + e.getMessage());
        }
    }
}
