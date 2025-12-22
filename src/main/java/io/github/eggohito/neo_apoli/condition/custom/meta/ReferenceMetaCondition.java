package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceMetaCondition(ResourceLocation value) implements IReferenceMetaCondition<Condition> {

	public static final MapCodec<ReferenceMetaCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceMetaCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceMetaCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceMetaCondition::new);

	@Override
	public Pair<Class<Condition>, String> classAndName() {
		return Pair.of(Condition.class, "Condition");
	}

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.REFERENCE;
	}

}
