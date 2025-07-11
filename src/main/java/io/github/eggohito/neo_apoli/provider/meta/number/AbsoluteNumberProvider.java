package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class AbsoluteNumberProvider extends NumberProvider {

	public static final MapCodec<AbsoluteNumberProvider> CODEC = MapCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(AbsoluteNumberProvider::number)
	).apply(instance, AbsoluteNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, AbsoluteNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, AbsoluteNumberProvider::number,
		AbsoluteNumberProvider::new
	));

	private final NumberProvider number;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ABSOLUTE;
	}

	@Override
	protected Number impl(Context context) {
		return Math.abs(this.number().nextDouble(context.makeChild(".number")));
	}

	@Override
	protected long longImpl(Context context) {
		return Math.abs(this.number().nextLong(context.makeChild(".number")));
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		number().validate(reporter.makeChild(".number"));
	}

}
