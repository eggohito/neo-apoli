package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.entity.FuelValues;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record FuelAmountNumberProvider() implements NumberProvider {

	public static final MapCodec<FuelAmountNumberProvider> CODEC = MapCodec.unit(FuelAmountNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, FuelAmountNumberProvider> STREAM_CODEC = StreamCodecUtil.unit(FuelAmountNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.FUEL_AMOUNT;
	}

	@Override
	public @NotNull Number next(Context context) {
		FuelValues fuelRegistry = context.getWorld().fuelValues();
		return context.optional(NeoApoliContextKeys.ITEM_STACK)
			.map(fuelRegistry::burnDuration)
			.orElse(0);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.ITEM_STACK);
	}

}
