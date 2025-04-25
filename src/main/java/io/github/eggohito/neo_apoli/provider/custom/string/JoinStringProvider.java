package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

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
	public Type<?> getType() {
		return StringProviderTypes.JOIN;
	}

	@Override
	public String get(Context context) {

		StringBuilder result = new StringBuilder();
		String separator = this.separator().get(context);

		for (var string : strings()) {
			result.append(string.get(context)).append(separator);
		}

		return result.toString();

	}

	@Override
	public void validate(ErrorReporter reporter) {

		for (int i = 0; i < strings().size(); i++) {
			strings().get(i).validate(reporter.makeChild("strings[" + i + "]"));
		}

		separator().validate(reporter.makeChild("separator"));

	}

}
