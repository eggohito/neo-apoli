package io.github.eggohito.neo_apoli.provider.meta.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

@EqualsAndHashCode
@Data
public final class ConstantNbtProvider extends NbtProvider {

	public static final MapCodec<ConstantNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.REGULAR_OR_STRINGIFIED_NBT_ELEMENT.fieldOf("value").forGetter(ConstantNbtProvider::value)
	).apply(instance, ConstantNbtProvider::new));

	public static final Codec<ConstantNbtProvider> INLINE_CODEC = NeoApoliCodecs.REGULAR_OR_STRINGIFIED_NBT_ELEMENT.xmap(
		ConstantNbtProvider::new,
		ConstantNbtProvider::value
	);

	public static final PacketCodec<RegistryByteBuf, ConstantNbtProvider> PACKET_CODEC = PacketCodecs.UNLIMITED_NBT_ELEMENT.xmap(
		ConstantNbtProvider::new,
		ConstantNbtProvider::value
	).cast();

	private final NbtElement value;

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.CONSTANT;
	}

	@Override
	protected NbtElement impl(Context context) {
		return value();
	}

}
