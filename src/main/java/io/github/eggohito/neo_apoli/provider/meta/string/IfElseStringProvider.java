package io.github.eggohito.neo_apoli.provider.meta.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseMetaValueProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
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
public final class IfElseStringProvider extends StringProvider implements IfElseMetaValueProvider<StringProvider, String> {

	public static final MapCodec<IfElseStringProvider> CODEC = MapCodecUtil.lazy(IfElseStringProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.codec(StringProvider.CODEC, IfElseStringProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseStringProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseStringProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.packetCodec(StringProvider.PACKET_CODEC, IfElseStringProvider::new));

	private final Condition condition;

	private final StringProvider ifValue;
	private final Optional<StringProvider> elseValue;

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.IF_ELSE;
	}

	@Override
	protected String impl(Context context) {
		return IfElseMetaValueProvider.super.internalImpl(context, () -> "");
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaValueProvider.super.validate(reporter);
	}

}
