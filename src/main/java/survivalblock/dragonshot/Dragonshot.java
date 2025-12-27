package survivalblock.dragonshot;

import com.mojang.math.Constants;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dragonshot implements ModInitializer {
    /*
    begin credit
    Modified from Fallen Star, which is licensed as follows:

All Rights Reserved

Copyright (c) 2025-present ekulxam
     */
    public static final TagKey<EntityType<?>> PROJECTILE_CHANGES_OWNER = TagKey.create(Registries.ENTITY_TYPE, id("projectile_changes_owner"));

    public static final ProjectileDeflection REFLECTION = (projectile, hitEntity, random) -> {
        if (hitEntity == null) {
            return;
        }
        Vec3 vec3d = projectile.getDeltaMovement().multiply(-1.1, -1.1, -1.1);
        projectile.setDeltaMovement(vec3d);
        projectile.needsSync = true;

        projectile.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * Constants.RAD_TO_DEG));
        projectile.setXRot((float)(Mth.atan2(vec3d.y, vec3d.horizontalDistance()) * Constants.RAD_TO_DEG));
        projectile.yRotO = projectile.getYRot();
        projectile.xRotO = projectile.getXRot();

        if (projectile.getType().is(PROJECTILE_CHANGES_OWNER)) {
            projectile.setOwner(hitEntity);
        }
    };
    /*
    This will remain ARR until I, ekulxam, find a more suitable license for Fallen Star
    end credit
     */

	public static final String MOD_ID = "25w52dargon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("It's dargon time");
	}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static boolean debugPaths(Level level) {
        return level instanceof ServerLevel serverLevel && serverLevel.getServer().debugSubscribers().hasAnySubscriberFor(DebugSubscriptions.ENTITY_PATHS);
    }
}