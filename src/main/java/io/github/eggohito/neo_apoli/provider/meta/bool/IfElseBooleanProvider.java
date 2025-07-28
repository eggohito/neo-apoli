package io.github.eggohito.neo_apoli.provider.meta.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseMetaValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
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
public final class IfElseBooleanProvider extends BooleanProvider implements IfElseMetaValueProvider<BooleanProvider, Boolean> {

	public static final MapCodec<IfElseBooleanProvider> CODEC = MapCodecUtil.lazy(IfElseBooleanProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.codec(BooleanProvider.CODEC, IfElseBooleanProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseBooleanProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseBooleanProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.packetCodec(BooleanProvider.PACKET_CODEC, IfElseBooleanProvider::new));

	private final Condition condition;

	private final BooleanProvider ifValue;
	private final Optional<BooleanProvider> elseValue;

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.IF_ELSE;
	}

	@Override
	protected boolean impl(Context context) {
		return IfElseMetaValueProvider.super.internalImpl(context, () -> false);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaValueProvider.super.validate(reporter);
	}

}
