package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dragonshot.injected_interface.MultiCrystalDevourer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob implements MultiCrystalDevourer {
    @Shadow
    @Nullable
    public EndCrystal nearestCrystal;
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

    /**
     * @author Survivalblock
     * @reason too many changes to do nicely and I don't feel like doing this properly right now
     */
    @Overwrite
    private void checkCrystals() {
        if ()

        Iterator<EndCrystal> itr = this.dragonshot$crystals.iterator();
        while (itr.hasNext()) {
            EndCrystal crystal = itr.next();
            if (crystal == null || crystal.isRemoved()) {
                itr.remove();
            }
        }

        if (!this.dragonshot$crystals.isEmpty() && this.tickCount % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F * this.dragonshot$crystals.size());
        }

        if (this.random.nextInt(10) == 0) {
            this.dragonshot$crystals.clear();
            this.dragonshot$crystals.addAll(
                    this.level()
                            .getEntitiesOfClass(
                                    EndCrystal.class,
                                    this.getBoundingBox().inflate(48.0)
                            )
            );
        }
    }
}