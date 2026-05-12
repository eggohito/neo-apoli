package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliDamageConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicDamageCondition(BooleanProvider value) implements DamageCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicDamageCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicDamageCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicDamageCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicDamageCondition::new);

	@Override
	public DamageCondition.Type<?> getType() {
		return NeoApoliDamageConditionTypes.DYNAMIC;
	}

}
