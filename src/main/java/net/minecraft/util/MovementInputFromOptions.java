package net.minecraft.util;

import dev.Fall.Fall;
import dev.Fall.event.impl.player.MoveInputEvent;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public static double sneakMultiplier = 0.3D;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;
        if (this.gameSettings.keyBindForward.isKeyDown())
            this.moveForward++;
        if (this.gameSettings.keyBindBack.isKeyDown())
            this.moveForward--;
        if (this.gameSettings.keyBindLeft.isKeyDown())
            this.moveStrafe++;
        if (this.gameSettings.keyBindRight.isKeyDown())
            this.moveStrafe--;
        this.jump = (this.gameSettings.keyBindJump.isKeyDown());
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        final MoveInputEvent moveInputEvent = new MoveInputEvent(moveForward, moveStrafe, jump, sneak, 0.3D);
        Fall.INSTANCE.getEventProtocol().handleEvent(moveInputEvent);

        if (this.sneak) {
            this.moveStrafe = (float)(this.moveStrafe * sneakMultiplier);
            this.moveForward = (float)(this.moveForward * sneakMultiplier);
        }
    }
}
