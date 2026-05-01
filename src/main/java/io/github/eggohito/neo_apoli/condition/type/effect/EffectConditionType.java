package io.github.eggohito.neo_apoli.condition.type.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.effect.EffectCondition;
import io.github.eggohito.neo_apoli.condition.kind.custom.EffectConditionKind;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EffectConditionType<C extends EffectCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements ConditionType<C> {

	public static final FixedRegistryAlias<EffectConditionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.EFFECT_CONDITION_TYPE, ConditionType.ALIASES);

	public static final Codec<EffectConditionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, EffectConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.EFFECT_CONDITION_TYPE);

	@Override
	public EffectConditionKind kind() {
		return EffectConditionKind.INSTANCE;
	}

}
