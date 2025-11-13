package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record TimeNumberProvider(Optional<NumberProvider> period) implements NumberProvider {

	public static final MapCodec<TimeNumberProvider> CODEC = MapCodecUtil.lazy(TimeNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("period").forGetter(TimeNumberProvider::period)
	).apply(instance, TimeNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, TimeNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(TimeNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		PacketCodecs.optional(NumberProvider.PACKET_CODEC), TimeNumberProvider::period,
		TimeNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.TIME;
	}

	@Override
	public @NotNull Number next(Context context) {

		World world = context.getWorld();
		long time = world.getTime();

		if (period().isPresent()) {

			Context periodContext = context.makeChild(".period");
			long period = period().get().nextLong(periodContext);

			if (!periodContext.hasErrors()) {
				time %= period;
			}

		}

		return time;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		period().ifPresent(period -> period.validate(reporter.makeChild(".period")));
	}

}
