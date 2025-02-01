package dev.Fall.ui.mainmenu.particles;

import dev.Fall.utils.tuples.Pair;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;

/**
 * @author cedo
 * @since 05/23/2022
 */
@Getter
public class ParticleImage {
    private final Pair<Integer, Integer> dimensions;
    private final ResourceLocation location;
    private final ParticleType particleType;

    public ParticleImage(int particleNumber, Pair<Integer, Integer> dimensions) {
        this.dimensions = dimensions;
        particleType = dimensions.getFirst() > 350 ? ParticleType.BIG : ParticleType.SMALL;
        location = new ResourceLocation("Fluid/MainMenu/particles" + particleNumber + ".png");
    }

}
