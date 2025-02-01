package dev.Fall.event.impl.player;

import dev.Fall.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.util.vector.Vector2f;

@Getter
@Setter
@AllArgsConstructor
public final class LookEvent extends Event {
    private Vector2f rotation;

}
