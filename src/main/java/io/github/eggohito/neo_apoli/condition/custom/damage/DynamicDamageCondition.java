package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IDynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicDamageCondition(BooleanProvider value) implements DamageCondition, IDynamicMetaCondition {

	public static final MapCodec<DynamicDamageCondition> MAP_CODEC = IDynamicMetaCondition.mapCodec(DynamicDamageCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicDamageCondition> STREAM_CODEC = IDynamicMetaCondition.streamCodec(DynamicDamageCondition::new);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.DYNAMIC;
	}

}
