package survivalblock.dragonshot.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.dragonshot.Dragonshot;

@Mixin(DragonStrafePlayerPhase.class)
public abstract class DragonStrafePlayerPhaseMixin extends AbstractDragonPhaseInstance {

    @Shadow
    private @Nullable LivingEntity attackTarget;

    public DragonStrafePlayerPhaseMixin(EnderDragon enderDragon) {
        super(enderDragon);
    }

    @Inject(method = "doServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void scheduleMoreFireballs(ServerLevel serverLevel, CallbackInfo ci) {
        Runnable fireballCreator = () -> {
            if (this.attackTarget == null) {
                return;
            }

            Vec3 view = this.dragon.getViewVector(1.0F);
            double fireballX = this.dragon.head.getX() - view.x;
            double fireballY = this.dragon.head.getY(0.5) + 0.5;
            double fireballZ = this.dragon.head.getZ() - view.z;
            double velocityX = this.attackTarget.getX() - fireballX;
            double velocityY = this.attackTarget.getY(0.5) - fireballY;
            double velocityZ = this.attackTarget.getZ() - fireballZ;
            Vec3 velocity = new Vec3(velocityX, velocityY, velocityZ);
            if (!this.dragon.isSilent()) {
                serverLevel.levelEvent(null, LevelEvent.SOUND_DRAGON_FIREBALL, this.dragon.blockPosition(), 0);
            }

            DragonFireball dragonFireball = new DragonFireball(serverLevel, this.dragon, velocity.normalize());
            dragonFireball.snapTo(fireballX, fireballY, fireballZ, 0.0F, 0.0F);
            serverLevel.addFreshEntity(dragonFireball);
        };
        for (int i = 1; i < this.dragon.getRandom().nextInt(5); i++) {
            Dragonshot.SCHEDULER.get().add(new Dragonshot.TimerTask(i * 5, fireballCreator));
        }
    }
}
