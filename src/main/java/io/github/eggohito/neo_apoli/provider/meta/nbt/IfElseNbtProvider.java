package io.github.eggohito.neo_apoli.provider.meta.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.meta.IfElseMetaValueProvider;
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

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class IfElseNbtProvider extends NbtProvider implements IfElseMetaValueProvider<NbtProvider, NbtElement> {

	public static final MapCodec<IfElseNbtProvider> CODEC = MapCodecUtil.lazy(IfElseNbtProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.codec(NbtProvider.CODEC, IfElseNbtProvider::new));
	public static final PacketCodec<RegistryByteBuf, IfElseNbtProvider> PACKET_CODEC = PacketCodecUtil.lazy(IfElseNbtProvider.class.getSimpleName(), () -> IfElseMetaValueProvider.packetCodec(NbtProvider.PACKET_CODEC, IfElseNbtProvider::new));

	private final Condition condition;

	private final NbtProvider ifValue;
	private final Optional<NbtProvider> elseValue;

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.IF_ELSE;
	}

	@Override
	protected NbtElement impl(Context context) {
		return IfElseMetaValueProvider.super.internalImpl(context, NbtCompound::new);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaValueProvider.super.validate(reporter);
	}

}
