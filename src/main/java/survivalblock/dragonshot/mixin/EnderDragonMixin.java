package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {
    @ModifyExpressionValue(method = "createAttributes", at = @At(value = "CONSTANT", args = "doubleValue=200.0"))
    private static double modifyHealth(double original) {
        return original * 2;
    }

    // https://mojira.dev/MC-272431 fix
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "doubleValue=0.01", ordinal = 0))
    private double yVelocityFix(double original) {
        return original * 10;
    }
}