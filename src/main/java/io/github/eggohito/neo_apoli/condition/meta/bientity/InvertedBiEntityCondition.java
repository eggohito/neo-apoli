package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class InvertedBiEntityCondition extends BiEntityCondition implements InvertedMetaCondition<BiEntityCondition> {

	public static final MapCodec<InvertedBiEntityCondition> CODEC = NeoApoliMapCodecs.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(BiEntityCondition.CODEC, InvertedBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedBiEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, InvertedBiEntityCondition::new));

	private final BiEntityCondition condition;

	public InvertedBiEntityCondition(BiEntityCondition condition) {
		this.condition = condition;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.INVERTED;
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
