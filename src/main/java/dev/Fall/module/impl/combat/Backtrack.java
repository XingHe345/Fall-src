package dev.Fall.module.impl.combat;

import dev.Fall.event.impl.game.TickEvent;
import dev.Fall.event.impl.player.AttackEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.settings.impl.NumberSetting;
import net.minecraft.entity.EntityLivingBase;

public class Backtrack extends Module {
    EntityLivingBase target;
    public final NumberSetting Ticks = new NumberSetting("Delay MS", 50, 200, 50, 50);
    public final NumberSetting Amount = new NumberSetting("Amount", 0.5, 0.7, 0.1, .05);
    public Backtrack() {
        super("Back track", Category.COMBAT, "Legit reach.");
        addSettings(Ticks, Amount);
    }

    @Override
    public void onAttackEvent(AttackEvent event) {
        target = event.getTargetEntity();
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if(target == null) return;
        if(target.getDistanceToEntity(mc.thePlayer) > 7) target = null;
        if(target == null) return;
        if(target.ticksExisted % (Ticks.getValue() / 50) == 0 && target.getDistanceToEntity(mc.thePlayer) <= 7) {
            if(target.posX > target.lastTickPosX) target.posX -= ((target.posX - target.lastTickPosX) * Amount.getValue());
            if(target.posZ > target.lastTickPosZ) target.posZ -= ((target.posZ - target.lastTickPosZ) * Amount.getValue());
            if(target.posY > target.lastTickPosY) target.posY -= ((target.posY - target.lastTickPosY) * 0.5);

            if(target.posX < target.lastTickPosX) target.posX += ((target.posX - target.lastTickPosX) * Amount.getValue());
            if(target.posZ < target.lastTickPosZ) target.posZ += ((target.posZ - target.lastTickPosZ) * Amount.getValue());
            if(target.posY < target.lastTickPosY) target.posY += ((target.posY - target.lastTickPosY) * Amount.getValue());
        }

    }
}
