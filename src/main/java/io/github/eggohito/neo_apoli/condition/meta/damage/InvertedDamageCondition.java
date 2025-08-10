package io.github.eggohito.neo_apoli.condition.meta.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class InvertedDamageCondition extends DamageCondition implements InvertedMetaCondition<DamageCondition> {

	public static final MapCodec<InvertedDamageCondition> CODEC = MapCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(DamageCondition.CODEC, InvertedDamageCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedDamageCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(DamageCondition.PACKET_CODEC, InvertedDamageCondition::new));

	private final DamageCondition condition;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.INVERTED;
	}

	@Override
	public boolean impl(Context context) {
		return InvertedMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		InvertedMetaCondition.super.validate(reporter);
	}

}
