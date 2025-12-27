package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dragonshot.Dragonshot;

@Mixin(DragonSittingFlamingPhase.class)
public class DragonSittingFlamingPhaseMixin {

    @WrapOperation(method = "doServerTick", at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/effect/MobEffectInstance;"))
    private MobEffectInstance increaseAmplifier(Holder<MobEffect> holder, Operation<MobEffectInstance> original) {
        return new MobEffectInstance(holder, 1, Dragonshot.BREATH_AMPLIFIER_INCREASE); // 0 + increase = increase
    }
}
