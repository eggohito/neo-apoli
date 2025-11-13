package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NbtStringProvider(NbtProvider source, NbtPathArgumentType.NbtPath path) implements StringProvider {

	public static final MapCodec<NbtStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("source").forGetter(NbtStringProvider::source),
		NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(NbtStringProvider::path)
	).apply(instance, NbtStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, NbtStringProvider> PACKET_CODEC = PacketCodec.tuple(
		NbtProvider.PACKET_CODEC, NbtStringProvider::source,
		NeoApoliPacketCodecs.NBT_PATH, NbtStringProvider::path,
		NbtStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.NBT;
	}

	@Override
	public @NotNull String next(Context context) {

		Context sourceContext = context.makeChild(".source");
		NbtElement source = source().next(sourceContext);

		if (sourceContext.hasErrors()) {
			return "";
		}

		try {

			List<NbtElement> elements = path().get(source);
			int size = elements.size();

			if (size == 1) {
				NbtElement element = elements.getFirst();
				return element.asString().orElseGet(element::toString);
			}

			else if (size > 1) {
				return Integer.toString(path().count(source));
			}

			else {
				return "";
			}

		}

		catch (CommandSyntaxException e) {
			context.getReporter().report("Error trying to get string in NBT path \"" + this.path() + "\" from NBT \"" + source + "\": " + e.getMessage());
		}

		return "";

	}

	@Override
	public void validate(ErrorReporter reporter) {
		StringProvider.super.validate(reporter);
		source().validate(reporter.makeChild(".source"));
	}

}
