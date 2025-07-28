package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseListMetaValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class IfElseListNumberProvider extends NumberProvider implements IfElseListMetaValueProvider<NumberProvider, Number> {

	public static final MapCodec<IfElseListNumberProvider> CODEC = MapCodecUtil.lazy(IfElseListNumberProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.codec(NumberProvider.CODEC, IfElseListNumberProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseListNumberProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.packetCodec(NumberProvider.PACKET_CODEC, IfElseListNumberProvider::new));

	private final List<Entry<NumberProvider>> entries;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.IF_ELSE_LIST;
	}

	@Override
	protected Number impl(Context context) {
		return IfElseListMetaValueProvider.super.internalImpl(context, NumberProvider::next, () -> 0.0D);
	}

	@Override
	protected long longImpl(Context context) {
		return IfElseListMetaValueProvider.super.internalImpl(context, NumberProvider::nextLong, () -> 0L);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaValueProvider.super.validate(reporter);
	}

}
