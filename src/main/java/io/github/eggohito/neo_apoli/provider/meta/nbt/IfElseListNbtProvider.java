package io.github.eggohito.neo_apoli.provider.meta.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseListMetaValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class IfElseListNbtProvider extends NbtProvider implements IfElseListMetaValueProvider<NbtProvider, NbtElement> {

	public static final MapCodec<IfElseListNbtProvider> CODEC = MapCodecUtil.lazy(IfElseListNbtProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.codec(NbtProvider.CODEC, IfElseListNbtProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListNbtProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseListNbtProvider.class.getSimpleName(), () -> IfElseListMetaValueProvider.packetCodec(NbtProvider.PACKET_CODEC, IfElseListNbtProvider::new));

	private final List<Entry<NbtProvider>> entries;

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.IF_ELSE_LIST;
	}

	@Override
	protected NbtElement impl(Context context) {
		return IfElseListMetaValueProvider.super.internalImpl(context, NbtCompound::new);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaValueProvider.super.validate(reporter);
	}

}
