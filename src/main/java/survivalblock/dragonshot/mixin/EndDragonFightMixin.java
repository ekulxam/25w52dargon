package survivalblock.dragonshot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EndDragonFight.class)
public class EndDragonFightMixin {
    @Shadow
    private int crystalsAlive;
    @Unique
    private static final Component dragonshot$CRYSTAL_EVENT_DESTROYED_NAME = Component.translatable("event.dragonshot.crystals.destroyed");
    @Unique
    private static final Component dragonshot$CRYSTAL_EVENT_NAME = Component.translatable("event.dragonshot.crystals");
    @Unique
    private final ServerBossEvent dragonshot$crystalEvent = new ServerBossEvent(dragonshot$CRYSTAL_EVENT_NAME, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10);

    @WrapOperation(method = {"tick", "setDragonKilled"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;setVisible(Z)V"))
    private void setCrystalEventVisibility(ServerBossEvent instance, boolean visible, Operation<Void> original) {
        original.call(this.dragonshot$crystalEvent, visible);
        original.call(instance, visible);
    }

    @WrapOperation(method = "updatePlayers", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;addPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;removePlayer(Lnet/minecraft/server/level/ServerPlayer;)V")
    })
    private void setCrystalEventPlayers(ServerBossEvent instance, ServerPlayer serverPlayer, Operation<Void> original) {
        original.call(this.dragonshot$crystalEvent, serverPlayer);
        original.call(instance, serverPlayer);
    }

    @ModifyExpressionValue(method = "updateCrystalCount", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/SpikeFeature;getSpikesForLevel(Lnet/minecraft/world/level/WorldGenLevel;)Ljava/util/List;"))
    private List<SpikeFeature.EndSpike> shareSpikeCount(List<SpikeFeature.EndSpike> original, @Share("spikeCount")LocalIntRef localIntRef) {
        localIntRef.set(original.size());
        return original;
    }

    @Inject(method = "updateCrystalCount", at = @At("RETURN"))
    private void updateCrystalEvent(CallbackInfo ci, @Share("spikeCount")LocalIntRef localIntRef) {
        if (this.crystalsAlive < 0) {
            this.dragonshot$crystalEvent.setName(dragonshot$CRYSTAL_EVENT_DESTROYED_NAME);
        } else if (this.crystalsAlive <= 2) {
            this.dragonshot$crystalEvent.setName(dragonshot$CRYSTAL_EVENT_NAME.copy().append(" - ").append(Component.translatable("event.dragonshot.crystals.remaining", this.crystalsAlive)));
        } else {
            this.dragonshot$crystalEvent.setName(dragonshot$CRYSTAL_EVENT_NAME);
        }
        int spikes = localIntRef.get();
        BossEvent.BossBarOverlay overlay = BossEvent.BossBarOverlay.PROGRESS;
        if (spikes == 6) {
            overlay = BossEvent.BossBarOverlay.NOTCHED_6;
        } else if (spikes == 10) {
            overlay = BossEvent.BossBarOverlay.NOTCHED_10;
        } else if (spikes == 12) {
            overlay = BossEvent.BossBarOverlay.NOTCHED_12;
        } else if (spikes == 20) {
            overlay = BossEvent.BossBarOverlay.NOTCHED_20;
        }
        this.dragonshot$crystalEvent.setOverlay(overlay);
        this.dragonshot$crystalEvent.setProgress((float) this.crystalsAlive / spikes);
    }
}
