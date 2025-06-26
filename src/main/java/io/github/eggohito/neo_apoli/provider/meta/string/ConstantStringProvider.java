package io.github.eggohito.neo_apoli.provider.meta.string;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ConstantStringProvider extends StringProvider {

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

	private final String value;

	public ConstantStringProvider(String value) {
		this.value = value;
	}

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.CONSTANT;
	}

	@Override
	protected String impl(Context context) {
		return value();
	}

}
