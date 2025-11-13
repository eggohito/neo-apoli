package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.block.ShapeContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Box;

public interface BoxProvider extends ValueProvider<Box> {

	Codec<BoxProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BoxProviderType.CODEC.dispatch(BoxProvider::getType, BoxProviderType::mapCodec), ConstantBoxProvider.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, BoxProvider> PACKET_CODEC = BoxProviderType.PACKET_CODEC.dispatch(BoxProvider::getType, BoxProviderType::packetCodec);

	@Override
	BoxProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "Box provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.BOX_PROVIDER_TYPE, this.getType()) + "\"";
	}

	default ShapeContext getShapeContext(Context context) {
		return ShapeContext.absent();
	}

}
