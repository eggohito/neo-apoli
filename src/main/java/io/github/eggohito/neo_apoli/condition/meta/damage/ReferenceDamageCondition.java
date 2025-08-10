package io.github.eggohito.neo_apoli.condition.meta.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode
@Data
public final class ReferenceDamageCondition extends DamageCondition implements ReferenceMetaCondition<DamageCondition> {

	public static final MapCodec<ReferenceDamageCondition> CODEC = MapCodecUtil.lazy(ReferenceDamageCondition.class.getSimpleName(), () -> ReferenceMetaCondition.codec(ReferenceDamageCondition::new));
	public static final PacketCodec<RegistryByteBuf, ReferenceDamageCondition> PACKET_CODEC = PacketCodecUtil.lazy(ReferenceDamageCondition.class.getSimpleName(), () -> ReferenceMetaCondition.packetCodec(ReferenceDamageCondition::new));

	private final Identifier value;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.REFERENCE;
	}

	@Override
	public boolean impl(Context context) {
		return ReferenceMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ReferenceMetaCondition.super.validate(reporter);
	}

}
