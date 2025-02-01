package dev.Fall.module.impl.misc;

import dev.Fall.event.impl.player.AttackEvent;
import dev.Fall.event.impl.player.ChatReceivedEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.impl.combat.KillAura;
import dev.Fall.ui.notifications.NotificationManager;
import dev.Fall.ui.notifications.NotificationType;
import dev.Fall.utils.player.ChatUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StringUtils;


public class AutoReport extends Module {
    public AutoReport() {

        super("AutoReport",Category.MISC,"");
    }

    public EntityLivingBase targte = null;
    @Override
    public void onAttackEvent(AttackEvent event){
        targte = event.getTargetEntity();
    }
    @Override
    public void onChatReceivedEvent(ChatReceivedEvent e) {
        this.setSuffix("Ai");
        String message = StringUtils.stripControlCodes(e.message.getUnformattedText());
        if (message.contains(mc.thePlayer.getName()) && message.contains(KillAura.target.getName())){
            sendL("");
        }
    }
    public static void sendL(String string){
        mc.thePlayer.sendChatMessage("/report " + KillAura.target.getName());
        ChatUtil.print(false ,"You Reported Player");
        ;}
}
