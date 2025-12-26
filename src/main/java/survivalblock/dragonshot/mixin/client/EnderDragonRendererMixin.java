package survivalblock.dragonshot.mixin.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.dragonshot.DragonshotClient;

import java.util.List;

@Mixin(EnderDragonRenderer.class)
public class EnderDragonRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;F)V", at = @At("RETURN"))
    private void extractCrystalPositions(EnderDragon enderDragon, EnderDragonRenderState state, float tickProgress, CallbackInfo ci) {
        List<EndCrystal> crystals = enderDragon.dragonshot$getCrystals();
        if (crystals.isEmpty()) {
            state.setData(DragonshotClient.CRYSTAL_POSITIONS, ImmutableList.of());
            return;
        }
        ImmutableList.Builder<Vec3> builder = ImmutableList.builderWithExpectedSize(crystals.size());
        Vec3 dragonPos = enderDragon.getPosition(tickProgress);
        for (EndCrystal crystal : crystals) {
            if (crystal == null) {
                continue;
            }
            Vec3 vec3 = crystal.getPosition(tickProgress).add(0.0, EndCrystalRenderer.getY(crystal.time + tickProgress), 0.0);
            builder.add(vec3.subtract(dragonPos));
        }
        state.setData(DragonshotClient.CRYSTAL_POSITIONS, builder.build());
    }

    @WrapOperation(method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer;submitCrystalBeams(FFFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"))
    private void submitAllCrystals(float x, float y, float z, float age, PoseStack matrices, SubmitNodeCollector renderQueue, int light, Operation<Void> original, @Local(argsOnly = true) EnderDragonRenderState state) {
        List<Vec3> crystals = state.getDataOrDefault(DragonshotClient.CRYSTAL_POSITIONS, ImmutableList.of());
        if (crystals.isEmpty()) {
            original.call(x, y, z, age, matrices, renderQueue, light);
            return;
        }

        for (Vec3 vec3 : crystals) {
            original.call((float) vec3.x, (float) vec3.y, (float) vec3.z, age, matrices, renderQueue, light);
        }
    }
}
