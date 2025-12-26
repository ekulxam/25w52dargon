package survivalblock.dragonshot.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DragonshotEnUsLangGenerator extends FabricLanguageProvider {
    protected DragonshotEnUsLangGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
        translationBuilder.add("event.dragonshot.crystals", "Crystals");
        translationBuilder.add("event.dragonshot.crystals.remaining", "Remaining: %s");
        translationBuilder.add("event.dragonshot.crystals.destroyed", "All Crystals Destroyed");
    }
}
