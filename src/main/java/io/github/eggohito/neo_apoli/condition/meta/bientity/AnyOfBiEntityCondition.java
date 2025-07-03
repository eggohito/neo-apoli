package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class AnyOfBiEntityCondition extends BiEntityCondition implements AnyOfMetaCondition<BiEntityCondition> {

	public static final MapCodec<AnyOfBiEntityCondition> CODEC = NeoApoliMapCodecs.lazy(AnyOfBiEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(BiEntityCondition.CODEC, AnyOfBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfBiEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AnyOfBiEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, AnyOfBiEntityCondition::new));

	private final List<BiEntityCondition> conditions;

	public AnyOfBiEntityCondition(List<BiEntityCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.ANY_OF;
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
