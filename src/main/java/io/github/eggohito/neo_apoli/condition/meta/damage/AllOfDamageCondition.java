package io.github.eggohito.neo_apoli.condition.meta.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
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
public final class AllOfDamageCondition extends DamageCondition implements AllOfMetaCondition<DamageCondition> {

	public static final MapCodec<AllOfDamageCondition> CODEC = MapCodecUtil.lazy(AllOfDamageCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(DamageCondition.CODEC, AllOfDamageCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfDamageCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfDamageCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(DamageCondition.PACKET_CODEC, AllOfDamageCondition::new));

	private final List<DamageCondition> conditions;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.ALL_OF;
	}

	@Override
	public boolean impl(Context context) {
		return AllOfMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		AllOfMetaCondition.super.validate(reporter);
	}

}
