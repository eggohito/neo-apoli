package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public interface EffectCondition extends Condition {

	Codec<EffectCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(EffectCondition::getType, Type::mapCodec), ConstantEffectCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, EffectCondition> STREAM_CODEC = Type.STREAM_CODEC.dispatch(EffectCondition::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.EFFECT_INSTANCE);
	}

	enum Kind implements Condition.Kind<EffectCondition> {

		INSTANCE;

		@Override
		public @Nullable Function<String, CommandBuilder> commandBuilder() {
			return null;
		}

		@Override
		public ResourceKey<? extends Registry<EffectCondition>> registryKey() {
			return NeoApoliRegistryKeys.EFFECT_CONDITION;
		}

		@Override
		public Codec<EffectCondition> codec() {
			return EffectCondition.CODEC;
		}

		@Override
		public String asDisplayString() {
			return "Effect condition";
		}

	}

	record Type<C extends EffectCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.EFFECT_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.EFFECT_CONDITION_TYPE);

		@Override
		public EffectCondition.Kind kind() {
			return EffectCondition.Kind.INSTANCE;
		}

	}

}
