package io.github.eggohito.neo_apoli.condition.custom.damage;

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

public interface DamageCondition extends Condition {

	Codec<DamageCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(DamageCondition::getType, Type::mapCodec), ConstantDamageCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, DamageCondition> STREAM_CODEC = Type.STREAM_CODEC.dispatch(DamageCondition::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.DAMAGE_SOURCE);
	}

	enum Kind implements Condition.Kind<DamageCondition> {

		INSTANCE;

		@Override
		public @Nullable Function<String, CommandBuilder> commandBuilder() {
			return null;
		}

		@Override
		public ResourceKey<? extends Registry<DamageCondition>> registryKey() {
			return NeoApoliRegistryKeys.DAMAGE_CONDITION;
		}

		@Override
		public Codec<DamageCondition> codec() {
			return DamageCondition.CODEC;
		}

		@Override
		public String asDisplayString() {
			return "Damage condition";
		}

	}

	record Type<C extends DamageCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.DAMAGE_CONDITION_TYPE);

		@Override
		public DamageCondition.Kind kind() {
			return DamageCondition.Kind.INSTANCE;
		}

	}

}
