package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

public record MultiplyNumberProvider(List<NumberProvider> numbers) implements NumberProvider {

	public static final MapCodec<MultiplyNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(MultiplyNumberProvider::numbers)
	).apply(instance, MultiplyNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, MultiplyNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), MultiplyNumberProvider::numbers,
		MultiplyNumberProvider::new
	);

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.MULTIPLY;
	}

	@Override
	public Number get(Context context) {

		if (numbers().isEmpty()) {
			return 0.0;
		}

		double result = 1.0;
		for (int i = 0; i < numbers().size(); i++) {
			result *= numbers().get(i).get(context.makeChild("numbers[" + i + "]")).doubleValue();
		}

		for (var number : numbers()) {
			result *= number.get(context).doubleValue();
		}

		return result;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		for (int i = 0; i < numbers().size(); i++) {
			numbers().get(i).validate(reporter.makeChild("numbers[" + i + "]"));
		}

	}

}
