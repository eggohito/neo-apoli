package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntityPositionVec3Provider(TypedContextKey<Entity> entity) implements Vec3Provider {

	public static final MapCodec<EntityPositionVec3Provider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(EntityPositionVec3Provider::entity)
	).apply(instance, EntityPositionVec3Provider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityPositionVec3Provider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, EntityPositionVec3Provider::entity,
		EntityPositionVec3Provider::new
	);

	@Override
	public Vec3ProviderType<?> getType() {
		return Vec3dProviderTypes.ENTITY_POSITION;
	}

	@Override
	public @NotNull Vec3 next(Context context) {
		return context.optional(entity())
			.map(Entity::position)
			.orElse(Vec3.ZERO);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
