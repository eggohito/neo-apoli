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

public record DivideNumberProvider(List<NumberProvider> numbers) implements NumberProvider {

	public static final MapCodec<DivideNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(DivideNumberProvider::numbers)
	).apply(instance, DivideNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, DivideNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), DivideNumberProvider::numbers,
		DivideNumberProvider::new
	);

	@Override
	public Number get(ErrorReporter reporter, Context context) {

		double result = 0.0D;
		for (int i = 0; i < numbers().size(); i++) {
			result /= numbers().get(i).get(reporter.makeChild("numbers[" + i + "]"), context).doubleValue();
		}

		return result;

	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.DIVIDE;
	}

	@Override
	public void validate(ErrorReporter reporter) {

		for (int i = 0; i < numbers().size(); i++) {
			numbers().get(i).validate(reporter.makeChild("numbers[" + i + "]"));
		}

	}

}
