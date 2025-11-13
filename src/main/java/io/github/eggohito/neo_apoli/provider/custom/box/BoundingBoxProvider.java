package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record BoundingBoxProvider(EntityTarget entity) implements BoxProvider {

	public static final MapCodec<BoundingBoxProvider> CODEC = MapCodecUtil.lazy(BoundingBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("entity").forGetter(BoundingBoxProvider::entity)
	).apply(instance, BoundingBoxProvider::new)));

	public static final PacketCodec<RegistryByteBuf, BoundingBoxProvider> PACKET_CODEC = PacketCodecUtil.lazy(BoundingBoxProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		EntityTarget.PACKET_CODEC, BoundingBoxProvider::entity,
		BoundingBoxProvider::new
	));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.BOUNDING_BOX;
	}

	@Override
	public @NotNull Box next(Context context) {

		ContextParameter<Entity> parameter = entity().getParameter();
		Optional<Entity> entity = context.optional(parameter);

		return entity
			.map(Entity::getBoundingBox)
			.orElseGet(() -> Box.from(Vec3d.ZERO));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

	@Override
	public ShapeContext getShapeContext(Context context) {

		ContextParameter<Entity> parameter = entity().getParameter();
		Optional<Entity> entity = context.optional(parameter);

		if (entity.isEmpty()) {
			context.getReporter().report("Couldn't get shape context of entity from parameter \"%s\", as it doesn't exist!");
		}

		return entity
			.map(ShapeContext::of)
			.orElseGet(ShapeContext::absent);

	}

}
