package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.AllOfBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
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
public final class AllOfEntityCondition extends EntityCondition implements AllOfMetaCondition<EntityCondition> {

	public static final MapCodec<AllOfEntityCondition> CODEC = MapCodecUtil.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(EntityCondition.CODEC, AllOfEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, AllOfEntityCondition::new));

	private final List<EntityCondition> conditions;

	public AllOfEntityCondition(List<EntityCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ALL_OF;
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
