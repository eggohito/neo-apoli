package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

public record ConstantNbtProvider(NbtElement value) implements NbtProvider {

	public static final MapCodec<ConstantNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.REGULAR_OR_STRINGIFIED_NBT_ELEMENT.fieldOf("value").forGetter(ConstantNbtProvider::value)
	).apply(instance, ConstantNbtProvider::new));

	public static final Codec<ConstantNbtProvider> INLINE_CODEC = NeoApoliCodecs.REGULAR_OR_STRINGIFIED_NBT_ELEMENT.xmap(
		ConstantNbtProvider::new,
		ConstantNbtProvider::value
	);

	public static final PacketCodec<RegistryByteBuf, ConstantNbtProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.UNLIMITED_NBT_ELEMENT, ConstantNbtProvider::value,
		ConstantNbtProvider::new
	);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull NbtElement next(Context context) {
		return value();
	}

}
