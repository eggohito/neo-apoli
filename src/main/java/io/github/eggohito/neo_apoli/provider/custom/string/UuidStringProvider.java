package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record UuidStringProvider(EntityTarget entity) implements StringProvider {

	public static final MapCodec<UuidStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("entity").forGetter(UuidStringProvider::entity)
	).apply(instance, UuidStringProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, UuidStringProvider> STREAM_CODEC = StreamCodec.composite(
		EntityTarget.STREAM_CODEC, UuidStringProvider::entity,
		UuidStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.UUID;
	}

	@Override
	public @NotNull String next(Context context) {

		ContextKey<Entity> parameter = entity().getParameter();
		Optional<Entity> optEntity = context.optional(parameter);

		if (optEntity.isEmpty()) {
			context.getReporter().report("Couldn't get UUID of entity from parameter \"" + parameter.name() + "\", as it doesn't exist!");
		}

		return optEntity
			.map(Entity::getStringUUID)
			.orElse("");

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
