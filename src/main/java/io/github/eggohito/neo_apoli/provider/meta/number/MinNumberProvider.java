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

public record MinNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<MinNumberProvider> CODEC = NeoApoliCodecs.lazyMap("MinNumberProvider", () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(MinNumberProvider::numbers)
	).apply(instance, MinNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, MinNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy("MinNumberProvider", () -> PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), MinNumberProvider::numbers,
		MinNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.MIN;
	}

	@Override
	public String getPath() {
		return "numbers";
	}

	@Override
	public double doubleValue(Context context) {
		return iterateAndProcess(context, NumberProvider::doubleValue, Math::min, 0.0D);
	}

	@Override
	public long longValue(Context context) {
		return iterateAndProcess(context, NumberProvider::longValue, Math::min, 0L);
	}

}
