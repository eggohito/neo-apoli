package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record NbtNumberProvider(NbtProvider source, NbtPathArgument.NbtPath path) implements NumberProvider {

	public static final MapCodec<NbtNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("source").forGetter(NbtNumberProvider::source),
		NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(NbtNumberProvider::path)
	).apply(instance, NbtNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NbtNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NbtProvider.STREAM_CODEC, NbtNumberProvider::source,
		NeoApoliStreamCodecs.NBT_PATH, NbtNumberProvider::path,
		NbtNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.NBT;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context sourceContext = context.forChild(".source");
		Tag source = source().next(sourceContext);

		if (sourceContext.hasErrors()) {
			return 0.0d;
		}

		else {
			return this.path().countMatching(source);
		}

	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		source().validate(reporter.forChild(".source"));
	}

}
