package io.github.eggohito.neo_apoli.provider.custom.ints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.IntValueProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.ints.IntValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.ints.IntValueProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;

import java.util.Optional;

public record TimeIntValueProvider(Optional<Long> modulo) implements IntValueProvider {

	public static final MapCodec<TimeIntValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.LONG.optionalFieldOf("modulo").forGetter(TimeIntValueProvider::modulo)
	).apply(instance, TimeIntValueProvider::new));

	public static final PacketCodec<RegistryByteBuf, TimeIntValueProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(PacketCodecs.LONG), TimeIntValueProvider::modulo,
		TimeIntValueProvider::new
	);

	@Override
	public IntValueProviderType<?> getType() {
		return IntValueProviderTypes.TIME;
	}

	@Override
	public int getInt(ValueProviderContext context) {

		World world = context.getWorld();
		long time = world.getTime();

		if (modulo().isPresent()) {
			time %= modulo().get();
		}

		return (int) time;

	}

}
