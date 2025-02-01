package dev.Fall.module.impl.misc;

import dev.Fall.event.impl.network.PacketSendEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.ParentAttribute;
import dev.Fall.module.settings.impl.BooleanSetting;
import dev.Fall.module.settings.impl.NumberSetting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class Antibot extends Module {
    private static BooleanSetting ticksExisted = new BooleanSetting("ticksExisted", false);
    private static NumberSetting TicksExisted = new NumberSetting("ticks Existed", 10, 200, 1, 1);
    private static BooleanSetting unfairName = new BooleanSetting("unfairName", false);
    private static BooleanSetting health = new BooleanSetting("Health", false);
    public Antibot() {
        super("Anti Bot", Category.MISC, "Anti NPC-Check");
        addSettings(ticksExisted, health, TicksExisted, unfairName);
        TicksExisted.addParent(ticksExisted, ParentAttribute.BOOLEAN_CONDITION);
    }
    public static boolean botCheck(EntityLivingBase e){
        if(!ticksExisted.isEnabled() || (e.ticksExisted > TicksExisted.getValue())) {
            return true;
        }

        if(!unfairName.isEnabled() || (e.getName().length() < 16 && e.getName().replace(" ", "").length() >= 3)){
            return true;
        }
        if (health.isEnabled() && e.getHealth() == 0.0f) {
            return true;
        }
        return false;
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if(event.getPacket() instanceof C02PacketUseEntity) {
            if(((C02PacketUseEntity) event.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                if(!botCheck((EntityLivingBase) ((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld))) {
                    event.cancel();
                }
            }
        }
    }
}
