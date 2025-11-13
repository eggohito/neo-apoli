package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

public record ConstantStringProvider(String value) implements StringProvider {

	public static final MapCodec<ConstantStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("value").forGetter(ConstantStringProvider::value)
	).apply(instance, ConstantStringProvider::new));

	public static final Codec<ConstantStringProvider> INLINE_CODEC = Codec.STRING.xmap(
		ConstantStringProvider::new,
		ConstantStringProvider::value
	);

	public static final PacketCodec<RegistryByteBuf, ConstantStringProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.STRING, ConstantStringProvider::value,
		ConstantStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull String next(Context context) {
		return value();
	}

}
