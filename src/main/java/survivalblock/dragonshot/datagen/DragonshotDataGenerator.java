package survivalblock.dragonshot.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import survivalblock.dragonshot.Dragonshot;

public class DragonshotDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(DragonshotEnUsLangGenerator::new);
        pack.addProvider(DragonshotDynamicRegistriesGenerator::new);
        DragonshotTagGenerators.addAll(pack);
    }

    @SuppressWarnings("CodeBlock2Expr")
    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.DAMAGE_TYPE, context -> {
            context.register(Dragonshot.DRAGON_RAM, new DamageType("dragonshot.dragon_ram", 0.5F));
        });
    }
}
