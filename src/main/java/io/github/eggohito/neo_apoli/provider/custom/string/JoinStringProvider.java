package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.context.ContextParameter;

import java.util.List;
import java.util.Set;

public record JoinStringProvider(List<StringProvider> strings, StringProvider separator) implements StringProvider {

	public static final MapCodec<JoinStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.listOf().fieldOf("strings").forGetter(JoinStringProvider::strings),
		StringProvider.CODEC.fieldOf("separator").forGetter(JoinStringProvider::separator)
	).apply(instance, JoinStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, JoinStringProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, StringProvider.PACKET_CODEC), JoinStringProvider::strings,
		StringProvider.PACKET_CODEC, JoinStringProvider::separator,
		JoinStringProvider::new
	);

	@Override
	public String get(ValueProviderContext context) {

		StringBuilder result = new StringBuilder();
		String separator = this.separator().get(context);

		for (var string : strings()) {
			result.append(string.get(context)).append(separator);
		}

		return result.toString();

	}

	@Override
	public Type<?> getType() {
		return StringProviderTypes.JOIN;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {

		Set<ContextParameter<?>> params = new ObjectOpenHashSet<>();
		for (var string : strings()) {
			params.addAll(string.getAllowedParameters());
		}

		params.addAll(separator().getAllowedParameters());
		return params;

	}

}
