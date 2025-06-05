package io.github.eggohito.neo_apoli.provider.meta.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.misc.MultiStringProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;

public record JoinStringProvider(List<StringProvider> strings, StringProvider separator) implements StringProvider, MultiStringProvider {

	public static final MapCodec<JoinStringProvider> CODEC = NeoApoliMapCodecs.lazy(JoinStringProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.listOf().fieldOf("strings").forGetter(JoinStringProvider::strings),
		StringProvider.CODEC.fieldOf("separator").forGetter(JoinStringProvider::separator)
	).apply(instance, JoinStringProvider::new)));

	public static final PacketCodec<RegistryByteBuf, JoinStringProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy(JoinStringProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, StringProvider.PACKET_CODEC), JoinStringProvider::strings,
		StringProvider.PACKET_CODEC, JoinStringProvider::separator,
		JoinStringProvider::new
	));

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.JOIN;
	}

	@Override
	public String stringValue(Context context) {

		StringBuilder result = new StringBuilder();
		MutableBoolean init = new MutableBoolean(false);

		this.iterate((index, provider) -> {

			Context stringContext = context.makeChild("strings[" + index + "]");
			String string = provider.stringValue(stringContext);

			if (!stringContext.hasErrors()) {

				if (init.isTrue()) {

					Context separatorContext = context.makeChild("separator");
					String separator = this.separator().stringValue(separatorContext);

					if (!separatorContext.hasErrors()) {
						result.append(separator).append(string);
					}

				}

				else {
					result.append(string);
					init.setTrue();
				}

			}

		});

		return result.toString();

	}

	@Override
	public void validate(ErrorReporter reporter) {
		MultiStringProvider.super.validate(reporter);
		separator().validate(reporter.makeChild("separator"));
	}

}
