package survivalblock.dragonshot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DragonshotClient implements ClientModInitializer {
    public static final String CRYSTAL_POSITIONS_ID = Dragonshot.id("crystal_positions").toString();
    public static final RenderStateDataKey<List<Vec3>> CRYSTAL_POSITIONS = RenderStateDataKey.create(() -> CRYSTAL_POSITIONS_ID);

    @Override
    public void onInitializeClient() {

    }
}
