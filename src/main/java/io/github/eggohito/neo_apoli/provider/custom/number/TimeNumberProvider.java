package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;

import java.util.Optional;

public record TimeNumberProvider(Optional<Long> modulo) implements NumberProvider {

	public static final MapCodec<TimeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.LONG.optionalFieldOf("modulo").forGetter(TimeNumberProvider::modulo)
	).apply(instance, TimeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, TimeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(PacketCodecs.LONG), TimeNumberProvider::modulo,
		TimeNumberProvider::new
	);

	@Override
	public Number get(ErrorReporter reporter, ValueProviderContext context) {

		World world = context.getWorld();
		long time = world.getTime();

		if (modulo().isPresent()) {
			time %= modulo().get();
		}

		return time;

	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.TIME;
	}

}
