package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntityPositionVec3dProvider(EntityTarget entity) implements Vec3dProvider {

	public static final MapCodec<EntityPositionVec3dProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("entity").forGetter(EntityPositionVec3dProvider::entity)
	).apply(instance, EntityPositionVec3dProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityPositionVec3dProvider> STREAM_CODEC = StreamCodec.composite(
		EntityTarget.STREAM_CODEC, EntityPositionVec3dProvider::entity,
		EntityPositionVec3dProvider::new
	);

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.ENTITY_POSITION;
	}

	@Override
	public @NotNull Vec3 next(Context context) {
		return context.optional(entity().getParameter())
			.map(Entity::position)
			.orElse(Vec3.ZERO);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
