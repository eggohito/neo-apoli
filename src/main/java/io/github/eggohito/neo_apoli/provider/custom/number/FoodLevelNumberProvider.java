package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record FoodLevelNumberProvider(ContextParameter<Entity> entity) implements NumberProvider {

	public static final MapCodec<FoodLevelNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(FoodLevelNumberProvider::entity))
		.apply(instance, FoodLevelNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FoodLevelNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliContextParams.StreamCodecs.ENTITY, FoodLevelNumberProvider::entity,
		FoodLevelNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.FOOD_LEVEL;
	}

	@Override
	public double nextDouble(Context context) {
		return context.getOptional(entity())
			.filter(Player.class::isInstance)
			.map(Player.class::cast)
			.map(player -> player.getFoodData().getFoodLevel())
			.orElse(0);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
