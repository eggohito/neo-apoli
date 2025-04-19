package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

public record AddNumberProvider(List<NumberProvider> numbers) implements NumberProvider {

	public static final MapCodec<AddNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(AddNumberProvider::numbers)
	).apply(instance, AddNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AddNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), AddNumberProvider::numbers,
		AddNumberProvider::new
	);

	@Override
	public Number get(ErrorReporter reporter, ValueProviderContext context) {

		double value = 0.0D;
		for (int i = 0; i < numbers().size(); i++) {
			value += numbers().get(i).get(reporter.makeChild("numbers[" + i + "]"), context).doubleValue();
		}

		return value;

	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.ADD;
	}

	@Override
	public void validate(ErrorReporter reporter) {

		for (int i = 0; i < numbers().size(); i++) {
			numbers().get(i).validate(reporter.makeChild("numbers[" + i + "]"));
		}

	}

}
