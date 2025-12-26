package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dragonshot.Dragonshot;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyReturnValue(method = "deflection", at = @At("RETURN"))
    private ProjectileDeflection dragonReverseProjectiles(ProjectileDeflection original) {
        if (!((Entity) (Object) this instanceof EnderDragon enderDragon)) {
            return original;
        }
        DragonPhaseInstance phase = enderDragon.getPhaseManager().getCurrentPhase();
        if (phase == null && !phase.isSitting()) {
            return original;
        }
        return Dragonshot.REFLECTION;
    }
}
