package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;

public record JoinStringProvider(List<StringProvider> strings, StringProvider separator) implements StringProvider {

	public static final MapCodec<JoinStringProvider> MAP_CODEC = MapCodecUtil.lazy(JoinStringProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ExtraCodecs.nonEmptyList(StringProvider.CODEC.listOf()).fieldOf("strings").forGetter(JoinStringProvider::strings),
		StringProvider.CODEC.fieldOf("separator").forGetter(JoinStringProvider::separator)
	).apply(instance, JoinStringProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, JoinStringProvider> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.collection(ObjectArrayList::new, StringProvider.STREAM_CODEC), JoinStringProvider::strings,
		StringProvider.STREAM_CODEC, JoinStringProvider::separator,
		JoinStringProvider::new
	);

	@Override
	public @NotNull StringProviderType<?> getType() {
		return StringProviderTypes.JOIN;
	}

	@Override
	public @NotNull String nextString(Context context) {

		ListIterator<StringProvider> listIterator = strings().listIterator();
		StringBuilder result = new StringBuilder();

		boolean init = false;
		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			StringProvider provider = listIterator.next();

			Context stringContext = context.forChild(".strings[" + index + "]");
			String string = provider.nextString(stringContext);

			if (!stringContext.hasErrors()) {

				if (init) {

					Context separatorContext = context.forChild(".separator");
					String separator = separator().nextString(separatorContext);

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
	public void validate(Context.Validator validator) {

		StringProvider.super.validate(validator);
		ListIterator<StringProvider> listIterator = strings.listIterator();

		while (listIterator.hasNext()) {
			int index = listIterator.nextIndex();
			listIterator.next().validate(validator.forChild(".strings[" + index + "]"));
		}

		separator().validate(validator.forChild(".separator"));

	}
}
