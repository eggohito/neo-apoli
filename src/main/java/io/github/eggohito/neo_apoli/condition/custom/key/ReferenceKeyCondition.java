package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceKeyCondition(ResourceLocation value) implements KeyCondition, IReferenceMetaCondition<KeyCondition> {

	public static final MapCodec<ReferenceKeyCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceKeyCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.REFERENCE;
	}

	@Override
	public Pair<Class<KeyCondition>, String> classAndName() {
		return Pair.of(KeyCondition.class, "Key condition");
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
