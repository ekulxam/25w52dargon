package survivalblock.dragonshot.mixin;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(Path.class)
public interface PathAccessor {
    @Invoker("setDebug")
    void dragonshot$invokeSetDebug(Node[] nodes, Node[] nodes2, Set<Target> set);
}
