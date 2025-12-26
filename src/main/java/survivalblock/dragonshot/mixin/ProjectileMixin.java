package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public class ProjectileMixin {

    @ModifyExpressionValue(method = "hitTargetOrDeflectSelf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/EntityHitResult;getEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity doNotHitDragon(Entity original, @Cancellable CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (original == null) {
            return null;
        }
        if ((Projectile) (Object) this instanceof DragonFireball && original instanceof EnderDragon) {
            cir.setReturnValue(ProjectileDeflection.NONE);
            return original;
        }
        return original;
    }
}
