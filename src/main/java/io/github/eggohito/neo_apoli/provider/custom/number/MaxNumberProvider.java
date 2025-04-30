package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public record MaxNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<MaxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(MaxNumberProvider::numbers)
	).apply(instance, MaxNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, MaxNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC), MaxNumberProvider::numbers,
		MaxNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.MAX;
	}

	@Override
	public String getPath() {
		return "numbers";
	}

	@Override
	public double doubleValue(Context context) {
		return iterateAndProcess(context, NumberProvider::doubleValue, Math::max, 0.0D);
	}

	@Override
	public long longValue(Context context) {
		return iterateAndProcess(context, NumberProvider::longValue, Math::max, 0L);
	}

}
