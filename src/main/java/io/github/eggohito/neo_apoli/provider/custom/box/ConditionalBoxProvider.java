package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record ConditionalBoxProvider(Condition condition, BoxProvider ifValue, BoxProvider elseValue) implements BoxProvider, ConditionalValueProvider<BoxProvider, Box> {

	public static final MapCodec<ConditionalBoxProvider> CODEC = MapCodecUtil.lazy(ConditionalBoxProvider.class.getSimpleName(), () -> ConditionalValueProvider.codec(BoxProvider.CODEC, ConditionalBoxProvider::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalBoxProvider> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalBoxProvider.class.getSimpleName(), () -> ConditionalValueProvider.packetCodec(BoxProvider.PACKET_CODEC, ConditionalBoxProvider::new));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull Box next(Context context) {
		return internalNextOrElse(context, () -> new Box(Vec3d.ZERO, Vec3d.ZERO));
	}

}
