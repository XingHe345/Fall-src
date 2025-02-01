package dev.Fall.event.impl.player;

import dev.Fall.event.Event;

public class ClickEvent extends Event
{
    boolean fake;

    public ClickEvent(boolean fake) { this.fake = fake; }

    public boolean isFake() { return fake; }
}
