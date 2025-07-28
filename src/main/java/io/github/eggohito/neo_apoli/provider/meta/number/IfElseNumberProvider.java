package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseMetaValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class IfElseNumberProvider extends NumberProvider implements IfElseMetaValueProvider<NumberProvider, Number> {

	public static final MapCodec<IfElseNumberProvider> CODEC = MapCodecUtil.lazy(IfElseNumberProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.codec(NumberProvider.CODEC, IfElseNumberProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseNumberProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.packetCodec(NumberProvider.PACKET_CODEC, IfElseNumberProvider::new));

	private final Condition condition;

	private final NumberProvider ifValue;
	private final Optional<NumberProvider> elseValue;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.IF_ELSE;
	}

	@Override
	protected Number impl(Context context) {
		return IfElseMetaValueProvider.super.internalImpl(context, () -> 0.0D);
	}

	@Override
	protected long longImpl(Context context) {
		return IfElseMetaValueProvider.super.internalImpl(context, NumberProvider::nextLong, () -> 0L);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaValueProvider.super.validate(reporter);
	}

}
