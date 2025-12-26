package survivalblock.dragonshot.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import survivalblock.dragonshot.Dragonshot;

import java.util.concurrent.CompletableFuture;

public class DragonshotEntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
    public DragonshotEntityTypeTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        valueLookupBuilder(Dragonshot.PROJECTILE_CHANGES_OWNER)
                .add(EntityType.ARROW)
                .add(EntityType.SPECTRAL_ARROW);
    }
}
