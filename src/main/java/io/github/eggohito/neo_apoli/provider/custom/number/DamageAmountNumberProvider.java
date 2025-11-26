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
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record DamageAmountNumberProvider() implements NumberProvider {

	public static final MapCodec<DamageAmountNumberProvider> CODEC = MapCodec.unit(DamageAmountNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DamageAmountNumberProvider> STREAM_CODEC = StreamCodecUtil.unit(DamageAmountNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DAMAGE_AMOUNT;
	}

	@Override
	public @NotNull Number next(Context context) {
		return context.optional(NeoApoliContextKeys.DAMAGE_AMOUNT).orElse(0.0f);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.DAMAGE_AMOUNT);
	}

}
