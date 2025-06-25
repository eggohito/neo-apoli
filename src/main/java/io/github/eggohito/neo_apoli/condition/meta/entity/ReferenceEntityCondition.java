package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ReferenceEntityCondition extends EntityCondition implements ReferenceMetaCondition<EntityCondition> {

	public static final MapCodec<ReferenceEntityCondition> CODEC = ReferenceMetaCondition.codec(ReferenceEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceEntityCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceEntityCondition::new);

	private final Identifier value;

	public ReferenceEntityCondition(Identifier value) {
		this.value = value;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.REFERENCE;
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
