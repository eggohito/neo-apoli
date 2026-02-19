package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceDamageCondition(ResourceLocation value) implements DamageCondition, ReferenceMetaCondition<DamageCondition> {

	public static final MapCodec<ReferenceDamageCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceDamageCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceDamageCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceDamageCondition::new);

	@Override
	public Pair<Class<DamageCondition>, String> classAndName() {
		return Pair.of(DamageCondition.class, "Damage condition");
	}

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.REFERENCE;
	}

}
