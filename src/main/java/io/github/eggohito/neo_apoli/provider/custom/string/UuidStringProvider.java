package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliStringProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record UuidStringProvider(Context.Parameter<Entity> entity) implements StringProvider {

	public static final MapCodec<UuidStringProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(UuidStringProvider::entity)
	).apply(instance, UuidStringProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, UuidStringProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliContextParams.StreamCodecs.ENTITY, UuidStringProvider::entity,
		UuidStringProvider::new
	);

	@Override
	public @NotNull StringProvider.Type<?> getType() {
		return NeoApoliStringProviderTypes.UUID;
	}

	@Override
	public @NotNull String nextString(Context context) {

		if (!context.hasParameter(entity())) {
			context.forChild(".entity").reportProblem("Couldn't get and provide UUID of non-existent entity!");
		}

		return context.getOptional(entity())
			.map(Entity::getStringUUID)
			.orElse("");

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
