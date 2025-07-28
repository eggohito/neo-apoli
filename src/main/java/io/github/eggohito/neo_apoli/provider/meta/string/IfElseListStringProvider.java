package io.github.eggohito.neo_apoli.provider.meta.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseListMetaValueProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
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
public final class IfElseListStringProvider extends StringProvider implements IfElseListMetaValueProvider<StringProvider, String> {

	public static final MapCodec<IfElseListStringProvider> CODEC = MapCodecUtil.lazy(IfElseListStringProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.codec(StringProvider.CODEC, IfElseListStringProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListStringProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseListStringProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.packetCodec(StringProvider.PACKET_CODEC, IfElseListStringProvider::new));

	private final List<Entry<StringProvider>> entries;

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.IF_ELSE_LIST;
	}

	@Override
	protected String impl(Context context) {
		return IfElseListMetaValueProvider.super.internalImpl(context, () -> "");
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaValueProvider.super.validate(reporter);
	}

}
