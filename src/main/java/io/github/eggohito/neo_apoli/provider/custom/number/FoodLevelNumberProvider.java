package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record FoodLevelNumberProvider(EntityProvider entity) implements NumberProvider {

	public static final MapCodec<FoodLevelNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(EntityProvider.CODEC.fieldOf("entity").forGetter(FoodLevelNumberProvider::entity))
		.apply(instance, FoodLevelNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FoodLevelNumberProvider> STREAM_CODEC = StreamCodec.composite(
		EntityProvider.STREAM_CODEC, FoodLevelNumberProvider::entity,
		FoodLevelNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.FOOD_LEVEL;
	}

	@Override
	public double getDouble(Context context) {
		return entity().getEntity(context.forChild(".entity"))
			.filter(Player.class::isInstance)
			.map(Player.class::cast)
			.map(player -> player.getFoodData().getFoodLevel())
			.orElse(0);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		entity().validate(validator.forChild(".entity"));
	}

}
