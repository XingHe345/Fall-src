package dev.Fall.module.impl.misc;

import dev.Fall.event.impl.player.AttackEvent;
import dev.Fall.event.impl.player.ChatReceivedEvent;
import dev.Fall.event.impl.player.MotionEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.impl.combat.KillAura;
import dev.Fall.module.settings.impl.ModeSetting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StringUtils;


public class AutoL extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Ez", "Ez", "");
    public AutoL() {
        super("AutoL",Category.MISC,"");
        this.addSettings(mode);
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
        mc.thePlayer.sendChatMessage(
                        KillAura.target.getName() + "你已被击败")
    ;}
    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(this.mode.getMode());
       }
    }

