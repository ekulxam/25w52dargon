package survivalblock.dragonshot.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import survivalblock.dragonshot.Dragonshot;

import java.util.concurrent.CompletableFuture;

public class DragonshotTagGenerators {
    public static void addAll(FabricDataGenerator.Pack pack) {
        pack.addProvider(EntityTypeTagGenerator::new);
        pack.addProvider(DamageTypeTagGenerator::new);
    }

    private static class EntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
        public EntityTypeTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            valueLookupBuilder(Dragonshot.PROJECTILE_CHANGES_OWNER)
                    .add(EntityType.ARROW)
                    .add(EntityType.SPECTRAL_ARROW);
        }
    }

    private static class DamageTypeTagGenerator extends FabricTagProvider<DamageType> {
        public DamageTypeTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            builder(DamageTypeTags.BYPASSES_ARMOR).add(Dragonshot.DRAGON_RAM);
            builder(DamageTypeTags.BYPASSES_COOLDOWN).add(Dragonshot.DRAGON_RAM);
        }
    }
}
