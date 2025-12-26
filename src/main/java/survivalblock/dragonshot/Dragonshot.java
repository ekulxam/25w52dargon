package survivalblock.dragonshot;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Dragonshot implements ModInitializer {
	public static final String MOD_ID = "25w52dargon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String CRYSTAL_POSITIONS_ID = id("crystal_positions").toString();
    public static final RenderStateDataKey<List<Vec3>> CRYSTAL_POSITIONS = RenderStateDataKey.create(() -> CRYSTAL_POSITIONS_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("It's dargon time");
	}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}