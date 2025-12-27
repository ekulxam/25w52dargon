package survivalblock.dragonshot.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import survivalblock.dragonshot.injected_interface.Reflectable;

@Mixin(Projectile.class)
public class ProjectileMixin implements Reflectable {

    @Unique
    private boolean dragonshot$reflected = false;

    @Override
    public void dragonshot$markReflected() {
        this.dragonshot$reflected = true;
    }

    @Override
    public boolean dragonshot$hasBeenReflected() {
        return this.dragonshot$reflected;
    }
}
