package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;

public interface BoxProvider extends ValueProvider<AABB> {

	Codec<BoxProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BoxProviderType.CODEC.dispatch(BoxProvider::getType, BoxProviderType::mapCodec), ConstantBoxProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, BoxProvider> STREAM_CODEC = BoxProviderType.STREAM_CODEC.dispatch(BoxProvider::getType, BoxProviderType::packetCodec);

	@Override
	BoxProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "Box provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.BOX_PROVIDER_TYPE, this.getType()) + "\"";
	}

	default CollisionContext getShapeContext(Context context) {
		return CollisionContext.empty();
	}

}
