package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractHurtingProjectile.class)
public abstract class AbstractHurtingProjectileMixin extends Projectile {

    public AbstractHurtingProjectileMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "canHitEntity", at = @At("RETURN"))
    protected boolean noHitDragon(boolean original, Entity entity) {
        if (!original) {
            return false;
        }
        if (entity instanceof EnderDragon) {
            return !this.ownedBy(entity);
        }
        if (entity instanceof EnderDragonPart part) {
            return !this.ownedBy(part.parentMob);
        }
        return true;
    }
}
