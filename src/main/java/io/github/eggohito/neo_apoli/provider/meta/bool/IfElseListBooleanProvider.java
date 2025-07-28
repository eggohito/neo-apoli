package io.github.eggohito.neo_apoli.provider.meta.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseListMetaValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
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
public final class IfElseListBooleanProvider extends BooleanProvider implements IfElseListMetaValueProvider<BooleanProvider, Boolean> {

	public static final MapCodec<IfElseListBooleanProvider> CODEC = MapCodecUtil.lazy(IfElseListBooleanProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.codec(BooleanProvider.CODEC, IfElseListBooleanProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListBooleanProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseListBooleanProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.packetCodec(BooleanProvider.PACKET_CODEC, IfElseListBooleanProvider::new));

	private final List<Entry<BooleanProvider>> entries;

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.IF_ELSE_LIST;
	}

	@Override
	protected boolean impl(Context context) {
		return IfElseListMetaValueProvider.super.internalImpl(context, () -> false);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaValueProvider.super.validate(reporter);
	}

}
