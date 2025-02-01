package dev.Fall.module.impl.misc;


import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class GermMod extends Module {
    public GermMod() {
        super("GermMod", Category.MISC, "hyt-netease");
    }

    private static HashMap<String,byte[]> data = new HashMap<>();

    static{
        data.put("起床练习",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 48, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 48, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 100, 97, 108, 117, 34, 125});
        data.put("起床单人",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 49, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 49, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 115, 111, 108, 111, 34, 125});
        data.put("起床双人",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 50, 36, 123, 34, 101, 110, 116, 114, 121, 34, 58, 50, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 100, 111, 117, 98, 108, 101, 34, 125});
        data.put("起床四人",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 51, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 51, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 116, 101, 97, 109, 34, 125});
        data.put("空岛单人",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 48, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 48, 44, 34, 115, 105, 100, 34, 58, 34, 83, 75, 89, 87, 65, 82, 47, 110, 115, 107, 121, 119, 97, 114, 34, 125});
        data.put("空岛双人",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 49, 41, 123, 34, 101, 110, 116, 114, 121, 34, 58, 49, 44, 34, 115, 105, 100, 34, 58, 34, 83, 75, 89, 87, 65, 82, 47, 110, 115, 107, 121, 119, 97, 114, 45, 100, 111, 117, 98, 108, 101, 34, 125});
        data.put("ְ职业战争",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 50, 33, 123, 34, 101, 110, 116, 114, 121, 34, 58, 50, 44, 34, 115, 105, 100, 34, 58, 34, 70, 73, 71, 72, 84, 47, 107, 98, 45, 103, 97, 109, 101, 34, 125});
        data.put("天坑",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 52, 33, 123, 34, 101, 110, 116, 114, 121, 34, 58, 52, 44, 34, 115, 105, 100, 34, 58, 34, 70, 73, 71, 72, 84, 47, 116, 104, 101, 45, 112, 105, 116, 34, 125});

    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if(event.getPacket() instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload packet = (S3FPacketCustomPayload) event.getPacket();

            if(packet.getChannelName().equalsIgnoreCase("germplugin-netease")){

                PacketBuffer buffer = packet.getBufferData();
                byte[] data = new byte[buffer.readableBytes()];
                buffer.readBytes(data);
                if(new String(data, StandardCharsets.UTF_8).contains("h*0.5-min(h,w*9/16)*0.5")){
                    mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease",new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117}))));
                    for(String name : this.data.keySet()){
                        ChatComponentText textComponents = new ChatComponentText("");
                        textComponents.appendSibling(createClickableText(name,"/germ-btn-click "+name));
                        mc.thePlayer.addChatComponentMessage(textComponents);

                    }
                }

            }
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if(event.getPacket() instanceof C01PacketChatMessage){
            String message = ((C01PacketChatMessage) event.getPacket()).getMessage();
            if(message.startsWith("/germ-btn-click ")){
                String name = message.replace("/germ-btn-click ","");
                if(data.containsKey(name)){
                    mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease",new PacketBuffer(Unpooled.wrappedBuffer(data.get(name)))));
                }
            }
        }
    }

    private IChatComponent createClickableText(String text, String command) {
        ChatComponentText clickableText = new ChatComponentText(text);
        clickableText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return clickableText;
    }
}
