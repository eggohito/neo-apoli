package io.github.eggohito.neo_apoli.condition.meta.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class AnyOfDamageCondition extends DamageCondition implements AnyOfMetaCondition<DamageCondition> {

	public static final MapCodec<AnyOfDamageCondition> CODEC = MapCodecUtil.lazy(AnyOfDamageCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(DamageCondition.CODEC, AnyOfDamageCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfDamageCondition> PACKET_CODEC = PacketCodecUtil.lazy(AnyOfDamageCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(DamageCondition.PACKET_CODEC, AnyOfDamageCondition::new));

	private final List<DamageCondition> conditions;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.ANY_OF;
	}

	@Override
	public boolean impl(Context context) {
		return AnyOfMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		AnyOfMetaCondition.super.validate(reporter);
	}

}
