package io.github.eggohito.neo_apoli.provider.custom.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

public interface EffectProvider extends ValueProvider {

	Codec<EffectProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(EffectProvider::getType, Type::mapCodec), ContextEffectProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, EffectProvider> STREAM_CODEC = Type.STREAM_CODEC.dispatch(EffectProvider::getType, Type::streamCodec);

	@Override
	EffectProvider.Type<?> getType();

	Optional<MobEffectInstance> nextEffect(Context context);

	record Type<P extends EffectProvider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) implements ValueProvider.Type<P> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.EFFECT_PROVIDER_TYPE);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.EFFECT_PROVIDER_TYPE);

	}

}
