package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class InvertedEntityCondition extends EntityCondition implements InvertedMetaCondition<EntityCondition> {

	public static final MapCodec<InvertedEntityCondition> CODEC = NeoApoliMapCodecs.lazy(InvertedEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(EntityCondition.CODEC, InvertedEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(InvertedEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, InvertedEntityCondition::new));

	private final EntityCondition condition;

	public InvertedEntityCondition(EntityCondition condition) {
		this.condition = condition;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.INVERTED;
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
