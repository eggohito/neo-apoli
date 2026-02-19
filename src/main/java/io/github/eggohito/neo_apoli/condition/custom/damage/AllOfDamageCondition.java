package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfDamageCondition(List<DamageCondition> conditions) implements DamageCondition, AllOfMetaCondition<DamageCondition> {

	public static final MapCodec<AllOfDamageCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfDamageCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(DamageCondition.CODEC, AllOfDamageCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfDamageCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfDamageCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(DamageCondition.STREAM_CODEC, AllOfDamageCondition::new));

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.ALL_OF;
	}

}
