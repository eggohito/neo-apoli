package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;

public record IsOfDamageCondition(RegistryEntry<DamageType> damageType) implements DamageCondition {

	public static final MapCodec<IsOfDamageCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.ENTRY_CODEC.fieldOf("damage_type").forGetter(IsOfDamageCondition::damageType)
	).apply(instance, IsOfDamageCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOfDamageCondition> PACKET_CODEC = PacketCodec.tuple(
		DamageType.ENTRY_PACKET_CODEC, IsOfDamageCondition::damageType,
		IsOfDamageCondition::new
	);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextParameters.DAMAGE_SOURCE)
			.map(source -> this.damageType().matches(source::isOf))
			.orElse(false);
	}

}
