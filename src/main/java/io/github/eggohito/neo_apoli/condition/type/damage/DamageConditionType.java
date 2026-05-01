package io.github.eggohito.neo_apoli.condition.type.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.kind.custom.DamageConditionKind;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DamageConditionType<C extends DamageCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements ConditionType<C> {

	public static final FixedRegistryAlias<DamageConditionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, ConditionType.ALIASES);

	public static final Codec<DamageConditionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.DAMAGE_CONDITION_TYPE);

	@Override
	public DamageConditionKind kind() {
		return DamageConditionKind.INSTANCE;
	}

}
