package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.dragonshot.injected_interface.MultiCrystalDevourer;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob implements MultiCrystalDevourer {
    @Unique
    private final List<EndCrystal> dragonshot$crystals = new ArrayList<>();

    protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "createAttributes", at = @At(value = "CONSTANT", args = "doubleValue=200.0"))
    private static double modifyHealth(double original) {
        return original * 2;
    }

    // https://mojira.dev/MC-272431 fix
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "doubleValue=0.01", ordinal = 0))
    private double yVelocityFix(double original) {
        return original * 10;
    }

    @Override
    public List<EndCrystal> dragonshot$getCrystals() {
        return this.dragonshot$crystals;
    }

    @Inject(method = "checkCrystals", at = @At("HEAD"))
    private void checkAllCrystals(CallbackInfo ci) {
        this.dragonshot$crystals.removeIf(crystal -> crystal == null || crystal.isRemoved());
    }

    @ModifyExpressionValue(method = "checkCrystals", at = @At(value = "CONSTANT", args = "floatValue=1.0"))
    private float multiplyHealthIncreaseByCrystalChains(float original) {
        return original * Math.min(1, this.dragonshot$crystals.size());
    }

    @Inject(method = "checkCrystals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private void clearCrystalsForAdding(CallbackInfo ci) {
        this.dragonshot$crystals.clear();
    }

    @WrapOperation(method = "checkCrystals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"))
    private double addToCrystals(EndCrystal instance, Entity entity, Operation<Double> original) {
        this.dragonshot$crystals.add(instance);
        return original.call(instance, entity);
    }
}