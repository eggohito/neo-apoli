package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;

public record JoinStringProvider(List<StringProvider> strings, StringProvider separator) implements StringProvider {

	public static final MapCodec<JoinStringProvider> CODEC = MapCodecUtil.lazy(JoinStringProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codecs.nonEmptyList(StringProvider.CODEC.listOf()).fieldOf("strings").forGetter(JoinStringProvider::strings),
		StringProvider.CODEC.fieldOf("separator").forGetter(JoinStringProvider::separator)
	).apply(instance, JoinStringProvider::new)));

	public static final PacketCodec<RegistryByteBuf, JoinStringProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, StringProvider.PACKET_CODEC), JoinStringProvider::strings,
		StringProvider.PACKET_CODEC, JoinStringProvider::separator,
		JoinStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.JOIN;
	}

	@Override
	public @NotNull String next(Context context) {

		ListIterator<StringProvider> listIterator = strings().listIterator();
		StringBuilder result = new StringBuilder();

		boolean init = false;
		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			StringProvider provider = listIterator.next();

			Context stringContext = context.makeChild(".strings[" + index + "]");
			String string = provider.next(stringContext);

			if (!stringContext.hasErrors()) {

				if (init) {

					Context separatorContext = context.makeChild(".separator");
					String separator = separator().next(separatorContext);

					if (!separatorContext.hasErrors()) {
						result.append(separator).append(string);
					}

				}

				else {
					result.append(string);
					init = true;
				}

			}

		}

		return result.toString();

	}

	@Override
	public void validate(ErrorReporter reporter) {

		StringProvider.super.validate(reporter);
		ListIterator<StringProvider> listIterator = strings.listIterator();

		while (listIterator.hasNext()) {
			int index = listIterator.nextIndex();
			listIterator.next().validate(reporter.makeChild(".strings[" + index + "]"));
		}

		separator().validate(reporter.makeChild(".separator"));

	}
}
