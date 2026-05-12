package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NbtNumberProvider(NbtProvider source, NbtPathArgument.NbtPath path) implements NumberProvider {

	public static final MapCodec<NbtNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("source").forGetter(NbtNumberProvider::source),
		NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(NbtNumberProvider::path)
	).apply(instance, NbtNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NbtNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NbtProvider.STREAM_CODEC, NbtNumberProvider::source,
		NeoApoliStreamCodecs.NBT_PATH, NbtNumberProvider::path,
		NbtNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.NBT;
	}

	@Override
	public double nextDouble(Context context) {

		Context sourceContext = context.forChild(".source");
		Tag source = source().nextTag(sourceContext);

		if (sourceContext.hasErrors()) {
			return 0;
		}

		try {

			List<Tag> tags = path().get(source);
			int size = tags.size();

			if (size == 1) {
				return switch (tags.getFirst()) {
					case NumericTag numericTag ->
						numericTag.doubleValue();
					case CollectionTag collectionTag ->
						collectionTag.size();
					case CompoundTag compoundTag ->
						compoundTag.size();
					case StringTag(String value) ->
						value.length();
					default ->
						throw MiscUtil.createCommandException(Component.translatableEscape("commands.data.get.unknown", this.path()));
				};
			}

			else if (size > 1) {
				return path().countMatching(source);
			}

			else {
				return 0;
			}

		}

		catch (CommandSyntaxException e) {
			context.reportProblem("Error trying to get a numeric value in NBT path \"" + this.path() + " from NBT \"" + source + "\": " + e.getMessage());
		}

		return 0;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		source().validate(validator.forChild(".source"));
	}

}
