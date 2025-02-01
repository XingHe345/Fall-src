package dev.Fall.module.impl.render;

import dev.Fall.Fall;
import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.network.PacketReceiveEvent;
import dev.Fall.event.impl.player.AttackEvent;
import dev.Fall.event.impl.render.Render2DEvent;
import dev.Fall.event.impl.render.ShaderEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.objects.GradientColorWheel;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import lombok.var;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class PlayerInfo extends Module {
    private final Dragging dragging = Fall.INSTANCE.createDrag(this, "Info", 5, 150);
    private final GradientColorWheel colorWheel = new GradientColorWheel();
    public static int Wins, Kills, Dead = 0;
    public EntityLivingBase target;
    public PlayerInfo() {
        super("Game Info", Category.RENDER, "Show game info");
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if(mc.thePlayer == null || target == null) return;
        if(!mc.theWorld.loadedEntityList.contains(target)) {
            Kills += 1;
            target = null;
        }
    }


    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            var chatPacket = (S02PacketChat) event.getPacket();
            var chatMessage = chatPacket.getChatComponent().getUnformattedText();
            if (chatMessage.contains("1st Killer - " + mc.thePlayer.getName())) {
                Wins += 1;
            }
            else if(chatMessage.contains("You died!")) {
                Dead += 1;
            }
            else if (chatMessage.contains("取得了一场游戏的胜利")){
                Wins += 1;
            }
        }
    }

    @Override
    public void onAttackEvent(AttackEvent event) {
        target = event.getTargetEntity();
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), dragging.getHeight(), 2, colorWheel.getColor1());
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        dragging.setWidth(162);
        dragging.setHeight(75);

        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), dragging.getHeight(), 2, new Color(0, 0, 0, 150));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor_(dragging.getX() - 5, dragging.getY() - 5, dragging.getWidth() + 10, 20);
        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), 20, 2, new Color(0, 0, 0, 100));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        RenderUtil.drawImage(new ResourceLocation("Fluid/从Styles偷的icon不要告诉袋子月求求你了/你猜。.png"), dragging.getX() + 2, dragging.getY() + 1.5f, 11, 11);
        FluidFont18.drawString("Session Information", dragging.getX() + 15, dragging.getY() + 4, -1);
        RenderUtil.drawHead(mc.thePlayer.getLocationSkin(), dragging.getX() + 3, dragging.getY() + 17, 55, 55, 4, 255);

        FluidFont18.drawString("Kills - " + Kills, dragging.getX() + 60, dragging.getY() + 25, -1);
        FluidFont18.drawString("Dead - " + Dead, dragging.getX() + 60, dragging.getY() + 35, -1);
        FluidFont18.drawString("Won - " + Wins, dragging.getX() + 60, dragging.getY() + 45, -1);
    }

}
