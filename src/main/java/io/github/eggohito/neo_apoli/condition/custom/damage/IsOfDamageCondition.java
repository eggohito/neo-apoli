package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageType;

public record IsOfDamageCondition(Holder<DamageType> damageType) implements DamageCondition {

	public static final MapCodec<IsOfDamageCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.CODEC.fieldOf("damage_type").forGetter(IsOfDamageCondition::damageType)
	).apply(instance, IsOfDamageCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOfDamageCondition> STREAM_CODEC = StreamCodec.composite(
		DamageType.STREAM_CODEC, IsOfDamageCondition::damageType,
		IsOfDamageCondition::new
	);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.DAMAGE_SOURCE)
			.map(source -> this.damageType().is(source::is))
			.orElse(false);
	}

}
