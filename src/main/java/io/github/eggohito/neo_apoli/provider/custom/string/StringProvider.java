package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface StringProvider extends ValueProvider {

	Codec<StringProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(StringProviderType.CODEC.dispatch(StringProvider::getType, StringProviderType::mapCodec), ConstantStringProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, StringProvider> STREAM_CODEC = StringProviderType.STREAM_CODEC.dispatch(StringProvider::getType, StringProviderType::packetCodec);

	@NotNull
	StringProviderType<?> getType();

	@NotNull
	String nextString(Context context);

}
