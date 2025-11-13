package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalNbtProvider(Condition condition, NbtProvider ifValue, NbtProvider elseValue) implements NbtProvider, ConditionalValueProvider<NbtProvider, NbtElement> {

	public static final MapCodec<ConditionalNbtProvider> CODEC = MapCodecUtil.lazy(ConditionalNbtProvider.class.getSimpleName(), () -> ConditionalValueProvider.codec(NbtProvider.CODEC, ConditionalNbtProvider::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalNbtProvider> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalNbtProvider.class.getSimpleName(), () -> ConditionalValueProvider.packetCodec(NbtProvider.PACKET_CODEC, ConditionalNbtProvider::new));

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull NbtElement next(Context context) {
		return internalNextOrElse(context, NbtCompound::new);
	}

}
