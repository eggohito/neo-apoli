package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BoundingBoxProvider(TypedContextKey<Entity> entity) implements BoxProvider {

	public static final MapCodec<BoundingBoxProvider> CODEC = MapCodecUtil.lazy(BoundingBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(BoundingBoxProvider::entity)
	).apply(instance, BoundingBoxProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, BoundingBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(BoundingBoxProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, BoundingBoxProvider::entity,
		BoundingBoxProvider::new
	));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.BOUNDING_BOX;
	}

	@Override
	public @NotNull AABB next(Context context) {

		if (!context.hasParameter(entity())) {
			context.getReporter().report("Couldn't get the bounding box of the non-existing entity from parameter \"" + entity().name() + "\"!");
		}

		return context.optional(entity())
			.map(Entity::getBoundingBox)
			.orElseGet(() -> AABB.unitCubeFromLowerCorner(Vec3.ZERO));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public CollisionContext getShapeContext(Context context) {

		if (!context.hasParameter(entity())) {
			context.getReporter().report("Couldn't get shape context from non-existnet entity from parameter \"" + entity().name() + "\"!");
		}

		return context.optional(entity())
			.map(CollisionContext::of)
			.orElseGet(CollisionContext::empty);

	}

}
