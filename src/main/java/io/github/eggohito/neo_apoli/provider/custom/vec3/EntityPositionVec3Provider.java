package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntityPositionVec3Provider(Context.Parameter<Entity> entity, EntityAnchorArgument.Anchor anchor) implements Vec3Provider {

	public static final MapCodec<EntityPositionVec3Provider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(EntityPositionVec3Provider::entity),
		NeoApoliCodecs.ENTITY_ANCHOR.optionalFieldOf("anchor", EntityAnchorArgument.Anchor.FEET).forGetter(EntityPositionVec3Provider::anchor)
	).apply(instance, EntityPositionVec3Provider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityPositionVec3Provider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliContextParams.StreamCodecs.ENTITY, EntityPositionVec3Provider::entity,
		NeoApoliStreamCodecs.ENTITY_ANCHOR, EntityPositionVec3Provider::anchor,
		EntityPositionVec3Provider::new
	);

	@Override
	public @NotNull Vec3ProviderType<?> getType() {
		return Vec3ProviderTypes.ENTITY_POSITION;
	}

	@Override
	public @NotNull Vec3 nextVec3(Context context) {
		return context.getOptional(entity())
			.map(anchor()::apply)
			.orElse(Vec3.ZERO);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
