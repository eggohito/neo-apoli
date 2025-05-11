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

public record AddNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<AddNumberProvider> CODEC = NeoApoliCodecs.lazyMap("AddNumberProvider", () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(AddNumberProvider::numbers)
	).apply(instance, AddNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, AddNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy("AddNumberProvider", () -> PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), AddNumberProvider::numbers,
		AddNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ADD;
	}

	@Override
	public String getPath() {
		return "numbers";
	}

	@Override
	public double doubleValue(Context context) {
		return iterateAndProcess(context, NumberProvider::doubleValue, Double::sum, 0.0D);
	}

}
