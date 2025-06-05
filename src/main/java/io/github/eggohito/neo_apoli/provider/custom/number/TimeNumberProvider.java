package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;

import java.util.Optional;

public record TimeNumberProvider(Optional<NumberProvider> modulo) implements NumberProvider {

	public static final MapCodec<TimeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("modulo").forGetter(TimeNumberProvider::modulo)
	).apply(instance, TimeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, TimeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(NumberProvider.PACKET_CODEC), TimeNumberProvider::modulo,
		TimeNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.TIME;
	}

	@Override
	public double doubleValue(Context context) {
		return this.longValue(context);
	}

	@Override
	public long longValue(Context context) {

		World world = context.getWorld();
		long time = world.getTime();

		if (modulo().isPresent()) {
			time %= modulo().get().longValue(context.makeChild("modulo"));
		}

		return time;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		modulo().ifPresent(modulo -> modulo.validate(reporter.makeChild("modulo")));
	}

}
