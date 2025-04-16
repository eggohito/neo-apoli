package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ConstantStringProvider(String value) implements StringProvider {

	public static final MapCodec<ConstantStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("value").forGetter(ConstantStringProvider::value)
	).apply(instance, ConstantStringProvider::new));

	public static final Codec<ConstantStringProvider> INLINE_CODEC = Codec.STRING.xmap(
		ConstantStringProvider::new,
		ConstantStringProvider::value
	);

	public static final PacketCodec<RegistryByteBuf, ConstantStringProvider> PACKET_CODEC = PacketCodecs.STRING.xmap(
		ConstantStringProvider::new,
		ConstantStringProvider::value
	).cast();

	@Override
	public String get(ErrorReporter reporter, ValueProviderContext context) {
		return this.value();
	}

	@Override
	public Type<?> getType() {
		return StringProviderTypes.CONSTANT;
	}

}
