package dev.Fall.module.impl.misc;

import dev.Fall.event.impl.player.AttackEvent;
import dev.Fall.event.impl.player.ChatReceivedEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.impl.combat.KillAura;
import dev.Fall.ui.notifications.NotificationManager;
import dev.Fall.ui.notifications.NotificationType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StringUtils;

import java.util.Arrays;
import java.util.List;


public class KillHints extends Module {
    public KillHints() {
        super("KillHints",Category.MISC,"");
    }

    public EntityLivingBase targte = null;
    @Override
    public void onAttackEvent(AttackEvent event){
        targte = event.getTargetEntity();
    }
    @Override
    public void onChatReceivedEvent(ChatReceivedEvent e) {
        String message = StringUtils.stripControlCodes(e.message.getUnformattedText());
        if (message.contains(mc.thePlayer.getName()) && message.contains(KillAura.target.getName())){
            sendL("");
        }
    }
    public static void sendL(String string){
        NotificationManager.post(NotificationType.DISABLE, "Hints", "Kills +1");
        ;}
}
