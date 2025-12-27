package survivalblock.dragonshot.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DragonFireball.class)
public class DragonFireballMixin extends AbstractHurtingProjectileMixin {
    @Override
    protected boolean noHitDragon(boolean original, Entity entity) {
        return original && !(entity instanceof EnderDragon);
    }
}
