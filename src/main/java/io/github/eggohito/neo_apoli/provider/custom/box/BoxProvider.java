package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

public interface BoxProvider extends ValueProvider {

	Codec<BoxProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BoxProviderType.CODEC.dispatch(BoxProvider::getType, BoxProviderType::mapCodec), ConstantBoxProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, BoxProvider> STREAM_CODEC = BoxProviderType.STREAM_CODEC.dispatch(BoxProvider::getType, BoxProviderType::packetCodec);

	@NotNull
	BoxProviderType<?> getType();

	@NotNull
	AABB nextBox(Context context);

	default CollisionContext getCollisionContext(Context context) {
		return CollisionContext.empty();
	}

}
