package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicKeyCondition(BooleanProvider value) implements KeyCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicKeyCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicKeyCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.DYNAMIC;
	}

}
