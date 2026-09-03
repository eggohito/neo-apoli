package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record TimeNumberProvider(Optional<NumberProvider> period) implements NumberProvider {

	public static final MapCodec<TimeNumberProvider> CODEC = MapCodecUtil.lazy(TimeNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("period").forGetter(TimeNumberProvider::period)
	).apply(instance, TimeNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, TimeNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(TimeNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), TimeNumberProvider::period,
		TimeNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.TIME;
	}

	@Override
	public double getDouble(Context context) {

		Level world = context.level();
		long time = world.getGameTime();

		if (period().isPresent()) {

			Context periodContext = context.forChild(".period");
			long period = period().get().getLong(periodContext);

			if (!periodContext.hasProblems()) {
				time %= period;
			}

		}

		return time;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		period().ifPresent(period -> period.validate(validator.forChild(".period")));
	}

}
