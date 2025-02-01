package dev.Fall.module.impl.combat;

import dev.Fall.event.impl.player.KeepSprintEvent;
import dev.Fall.module.Category;
import dev.Fall.module.Module;

public final class KeepSprint extends Module {

    public KeepSprint() {
        super("Keep Sprint", Category.COMBAT, "Stops sprint reset after hitting");
    }

    @Override
    public void onKeepSprintEvent(KeepSprintEvent event) {
        event.cancel();
    }

}
