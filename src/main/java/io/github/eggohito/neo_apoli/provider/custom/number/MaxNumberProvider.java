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

public record MaxNumberProvider(List<NumberProvider> numbers) implements NumberProvider {

	public static final MapCodec<MaxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf(2, Integer.MAX_VALUE).fieldOf("numbers").forGetter(MaxNumberProvider::numbers)
	).apply(instance, MaxNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, MaxNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), MaxNumberProvider::numbers,
		MaxNumberProvider::new
	);

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.MAX;
	}

	@Override
	public Number get(Context context) {

		if (numbers().isEmpty()) {
			return 0.0;
		}

		int index = 0;
		double result = numbers().get(index).get(context.makeChild("numbers[" + index + "]")).doubleValue();

		for (; index < numbers().size(); index++) {
			result = Math.max(result, numbers().get(index).get(context.makeChild("numbers[" + index + "]")).doubleValue());
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
