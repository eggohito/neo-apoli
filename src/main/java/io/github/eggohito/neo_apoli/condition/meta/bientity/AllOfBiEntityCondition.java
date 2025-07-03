package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
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
public final class AllOfBiEntityCondition extends BiEntityCondition implements AllOfMetaCondition<BiEntityCondition> {

	public static final MapCodec<AllOfBiEntityCondition> CODEC = NeoApoliMapCodecs.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(BiEntityCondition.CODEC, AllOfBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfBiEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, AllOfBiEntityCondition::new));

	private final List<BiEntityCondition> conditions;

	public AllOfBiEntityCondition(List<BiEntityCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.ALL_OF;
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
