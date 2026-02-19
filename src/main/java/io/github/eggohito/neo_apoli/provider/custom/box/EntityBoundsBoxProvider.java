package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.AABBUtil;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntityBoundsBoxProvider(ContextParameter<Entity> entity) implements BoxProvider {

	public static final MapCodec<EntityBoundsBoxProvider> MAP_CODEC = MapCodecUtil.lazy(EntityBoundsBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(EntityBoundsBoxProvider::entity)
	).apply(instance, EntityBoundsBoxProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityBoundsBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(EntityBoundsBoxProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NeoApoliContextParams.StreamCodecs.ENTITY, EntityBoundsBoxProvider::entity,
		EntityBoundsBoxProvider::new
	));

	@Override
	public @NotNull BoxProviderType<?> getType() {
		return BoxProviderTypes.ENTITY_BOUNDS;
	}

	@Override
	public @NotNull AABB nextBox(Context context) {

		if (!context.hasParameter(entity())) {
			context.forChild(".entity").reportProblem("Couldn't get the bounding box of a non-existing entity!");
		}

		return context.getOptional(entity())
			.map(Entity::getBoundingBox)
			.orElse(AABBUtil.EMPTY);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public CollisionContext getCollisionContext(Context context) {

		if (!context.hasParameter(entity())) {
			context.forChild(".entity").reportProblem("Couldn't get collision context from non-existent entity!");
		}

		return context.getOptional(entity())
			.map(CollisionContext::of)
			.orElseGet(CollisionContext::empty);

	}

}
