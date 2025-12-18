package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record UuidStringProvider(TypedContextKey<Entity> entity) implements StringProvider {

	public static final MapCodec<UuidStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(UuidStringProvider::entity)
	).apply(instance, UuidStringProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, UuidStringProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, UuidStringProvider::entity,
		UuidStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.UUID;
	}

	@Override
	public @NotNull String next(Context context) {

		if (!context.hasParameter(entity())) {
			context.getValidator().report("Couldn't get and provide UUID of entity from parameter \"" + entity().name() + "\", as it doesn't exist!");
		}

		return context.optional(entity())
			.map(Entity::getStringUUID)
			.orElse("");

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
