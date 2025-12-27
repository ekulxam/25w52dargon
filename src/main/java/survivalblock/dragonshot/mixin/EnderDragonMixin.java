package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.dragonshot.Dragonshot;
import survivalblock.dragonshot.injected_interface.MultiCrystalDevourer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob implements MultiCrystalDevourer {
    @Shadow
    @Final
    private BinaryHeap openSet;
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
        return original * Math.max(1, this.dragonshot$crystals.size());
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

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntitySelector;NO_CREATIVE_OR_SPECTATOR:Ljava/util/function/Predicate;", opcode = Opcodes.GETSTATIC))
    private Predicate<Entity> noHitEndermen(Predicate<Entity> original) {
        return (entity -> !(entity instanceof EnderMan) && original.test(entity));
    }

    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void noBedDamage(ServerLevel serverLevel, EnderDragonPart enderDragonPart, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.is(DamageTypes.BAD_RESPAWN_POINT)) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;mobAttack(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource enderDragonRam(DamageSources instance, LivingEntity livingEntity, Operation<DamageSource> original) {
        return livingEntity.registryAccess()
                .get(Dragonshot.DRAGON_RAM)
                .map(damageType -> new DamageSource(damageType, livingEntity))
                .orElseGet(() -> original.call(instance, livingEntity));
    }

    @Inject(method = "findPath", at = @At("HEAD"))
    private void beginDebugPaths(int i, int j, Node node, CallbackInfoReturnable<Path> cir, @Share("shouldDebug")LocalBooleanRef localBooleanRef, @Share("popNodes")LocalRef<Set<Node>> localRef) {
        localBooleanRef.set(Dragonshot.debugPaths(this.level()));
        localRef.set(localBooleanRef.get() ? new HashSet<>() : Set.of());
    }

    @ModifyExpressionValue(method = "findPath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/pathfinder/BinaryHeap;pop()Lnet/minecraft/world/level/pathfinder/Node;"))
    private Node captureOnPop(Node original, @Share("shouldDebug")LocalBooleanRef localBooleanRef, @Share("popNodes")LocalRef<Set<Node>> localRef) {
        if (!localBooleanRef.get()) {
            return original;
        }
        localRef.get().add(original);
        return original;
    }

    @WrapOperation(method = "findPath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;reconstructPath(Lnet/minecraft/world/level/pathfinder/Node;Lnet/minecraft/world/level/pathfinder/Node;)Lnet/minecraft/world/level/pathfinder/Path;"))
    private Path debugPaths(EnderDragon instance, Node node, Node node2, Operation<Path> original, @Share("shouldDebug")LocalBooleanRef localBooleanRef, @Share("popNodes")LocalRef<Set<Node>> localRef) {
        if (!localBooleanRef.get()) {
            return original.call(instance, node, node2);
        }

        Path path = original.call(instance, node, node2);
        if (path != null) {
            ((PathAccessor) (Object) path).dragonshot$invokeSetDebug(this.openSet.getHeap(), localRef.get().toArray(Node[]::new), Stream.of(node, node2).map(Target::new).collect(Collectors.toSet()));
        }
        //noinspection ConstantValue
        return path;
    }
}