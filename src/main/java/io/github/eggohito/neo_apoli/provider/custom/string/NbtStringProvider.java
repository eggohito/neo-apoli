package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NbtStringProvider(NbtProvider source, NbtPathArgument.NbtPath path) implements StringProvider {

	public static final MapCodec<NbtStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("source").forGetter(NbtStringProvider::source),
		NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(NbtStringProvider::path)
	).apply(instance, NbtStringProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NbtStringProvider> STREAM_CODEC = StreamCodec.composite(
		NbtProvider.STREAM_CODEC, NbtStringProvider::source,
		NeoApoliStreamCodecs.NBT_PATH, NbtStringProvider::path,
		NbtStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.NBT;
	}

	@Override
	public @NotNull String next(Context context) {

		Context sourceContext = context.forChild(".source");
		Tag source = source().next(sourceContext);

		if (sourceContext.hasErrors()) {
			return "";
		}

		try {

			List<Tag> elements = path().get(source);
			int size = elements.size();

			if (size == 1) {
				Tag element = elements.getFirst();
				return element.asString().orElseGet(element::toString);
			}

			else if (size > 1) {
				return Integer.toString(path().countMatching(source));
			}

			else {
				return "";
			}

		}

		catch (CommandSyntaxException e) {
			context.getValidator().report("Error trying to get string in NBT path \"" + this.path() + "\" from NBT \"" + source + "\": " + e.getMessage());
		}

		return "";

	}

	@Override
	public void validate(Context.Validator validator) {
		StringProvider.super.validate(validator);
		source().validate(validator.forChild(".source"));
	}

}
