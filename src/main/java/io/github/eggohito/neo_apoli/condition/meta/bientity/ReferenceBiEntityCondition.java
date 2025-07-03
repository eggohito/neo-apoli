package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode
@Data
public final class ReferenceBiEntityCondition extends BiEntityCondition implements ReferenceMetaCondition<BiEntityCondition> {

	public static final MapCodec<ReferenceBiEntityCondition> CODEC = ReferenceMetaCondition.codec(ReferenceBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBiEntityCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceBiEntityCondition::new);

	private final Identifier value;

	public ReferenceBiEntityCondition(Identifier value) {
		this.value = value;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.REFERENCE;
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
