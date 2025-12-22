package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IDynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicKeyCondition(BooleanProvider value) implements KeyCondition, IDynamicMetaCondition {

	public static final MapCodec<DynamicKeyCondition> CODEC = IDynamicMetaCondition.createCodec(DynamicKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicKeyCondition> STREAM_CODEC = IDynamicMetaCondition.createStreamCodec(DynamicKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.DYNAMIC;
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
