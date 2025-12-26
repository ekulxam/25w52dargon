package survivalblock.dragonshot.injected_interface;

import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.List;

public interface MultiCrystalDevourer {
    default List<EndCrystal> dragonshot$getCrystals() {
        throw new UnsupportedOperationException("Injected interface");
    }
}
