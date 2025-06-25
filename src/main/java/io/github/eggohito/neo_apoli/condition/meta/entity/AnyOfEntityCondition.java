package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public final class AnyOfEntityCondition extends EntityCondition implements AnyOfMetaCondition<EntityCondition> {

	public static final MapCodec<AnyOfEntityCondition> CODEC = NeoApoliMapCodecs.lazy(AnyOfEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(EntityCondition.CODEC, AnyOfEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AnyOfEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, AnyOfEntityCondition::new));

	private final List<EntityCondition> conditions;

	public AnyOfEntityCondition(List<EntityCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ANY_OF;
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
