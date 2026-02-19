package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceKeyCondition(ResourceLocation value) implements KeyCondition, ReferenceMetaCondition<KeyCondition> {

	public static final MapCodec<ReferenceKeyCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceKeyCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.REFERENCE;
	}

	@Override
	public Pair<Class<KeyCondition>, String> classAndName() {
		return Pair.of(KeyCondition.class, "Key condition");
	}

}
