package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class NbtNumberProvider extends NumberProvider {

	public static final MapCodec<NbtNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("source").forGetter(NbtNumberProvider::source),
		NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(NbtNumberProvider::path)
	).apply(instance, NbtNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, NbtNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NbtProvider.PACKET_CODEC, NbtNumberProvider::source,
		NeoApoliPacketCodecs.NBT_PATH, NbtNumberProvider::path,
		NbtNumberProvider::new
	);

	private final NbtProvider source;
	private final NbtPathArgumentType.NbtPath path;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.NBT;
	}

	@Override
	protected Number impl(Context context) {

		Context sourceContext = context.makeChild(".source");
		NbtElement source = source().next(sourceContext);

		if (sourceContext.hasErrors()) {
			return 0.0D;
		}

		else {
			return this.path().count(source);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		source().validate(reporter.makeChild(".source"));
	}

}
