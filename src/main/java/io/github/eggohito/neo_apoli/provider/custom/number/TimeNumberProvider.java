package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
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
	public Type<?> getType() {
		return NumberProviderTypes.TIME;
	}

	@Override
	public Number get(Context context) {

		World world = context.getWorld();
		long time = world.getTime();

		if (modulo().isPresent()) {
			time %= modulo().get().get(context.makeChild("modulo")).longValue();
		}

		return time;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		modulo().ifPresent(modulo -> modulo.validate(reporter.makeChild("modulo")));
	}

}
