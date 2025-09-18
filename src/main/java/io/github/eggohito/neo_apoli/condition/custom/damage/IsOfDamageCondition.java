package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;

@EqualsAndHashCode
@Data
public final class IsOfDamageCondition extends DamageCondition {

	public static final MapCodec<IsOfDamageCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.ENTRY_CODEC.fieldOf("damage_type").forGetter(IsOfDamageCondition::damageType)
	).apply(instance, IsOfDamageCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOfDamageCondition> PACKET_CODEC = DamageType.ENTRY_PACKET_CODEC.xmap(
		IsOfDamageCondition::new,
		IsOfDamageCondition::damageType
	);

	private final RegistryEntry<DamageType> damageType;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.IS_OF;
	}

	@Override
	protected boolean impl(Context context) {
		return damageType().matches(context.required(ContextParameters.DAMAGE_SOURCE)::isOf);
	}

}
