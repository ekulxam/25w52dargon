package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractHurtingProjectile.class)
public class AbstractHurtingProjectileMixin {

    @ModifyReturnValue(method = "canHitEntity", at = @At("RETURN"))
    protected boolean noHitDragon(boolean original, Entity entity) {
        return original;
    }
}
