package io.github.eggohito.neo_apoli.provider.meta.bool;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class NbtBooleanProvider extends BooleanProvider {

	public static final MapCodec<NbtBooleanProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("source").forGetter(NbtBooleanProvider::source),
		NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(NbtBooleanProvider::path)
	).apply(instance, NbtBooleanProvider::new));

	public static final PacketCodec<RegistryByteBuf, NbtBooleanProvider> PACKET_CODEC = PacketCodec.tuple(
		NbtProvider.PACKET_CODEC, NbtBooleanProvider::source,
		NeoApoliPacketCodecs.NBT_PATH, NbtBooleanProvider::path,
		NbtBooleanProvider::new
	);

	private final NbtProvider source;
	private final NbtPathArgumentType.NbtPath path;

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.NBT;
	}

	@Override
	protected boolean impl(Context context) {

		Context sourceContext = context.makeChild(".source");
		NbtElement source = source().next(sourceContext);

		if (sourceContext.hasErrors()) {
			return false;
		}

		try {

			List<NbtElement> elements = path().get(source);
			int size = elements.size();

			if (size == 1) {
				NbtElement element = elements.getFirst();
				return element.asBoolean().orElseGet(() -> path().count(element) > 0);
			}

			else {
				return path().count(source) > 0;
			}

		}

		catch (CommandSyntaxException cse) {
			context.getReporter().report("Error trying to get boolean in NBT path \"" + this.path() + "\" from NBT \"" + source + "\": " + cse.getMessage());
		}

		return false;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		source().validate(reporter.makeChild(".source"));
	}

}
