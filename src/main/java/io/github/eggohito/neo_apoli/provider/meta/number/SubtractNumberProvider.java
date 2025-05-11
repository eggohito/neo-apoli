package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.misc.MultiNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

public record SubtractNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<SubtractNumberProvider> CODEC = NeoApoliCodecs.lazyMap("SubtractNumberProvider", () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(SubtractNumberProvider::numbers)
	).apply(instance, SubtractNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, SubtractNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy("SubtractNumberProvider", () -> PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), SubtractNumberProvider::numbers,
		SubtractNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.SUBTRACT;
	}

	@Override
	public String getPath() {
		return "numbers";
	}

	@Override
	public double doubleValue(Context context) {
		return iterateAndProcess(context, NumberProvider::doubleValue, (a, b) -> a - b, 0.0D);
	}

}
