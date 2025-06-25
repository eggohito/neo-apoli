package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public final class AnyOfBlockCondition extends BlockCondition implements AnyOfMetaCondition<BlockCondition> {

	public static final MapCodec<AnyOfBlockCondition> CODEC = NeoApoliMapCodecs.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(BlockCondition.CODEC, AnyOfBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfBlockCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(BlockCondition.PACKET_CODEC, AnyOfBlockCondition::new));

	private final List<BlockCondition> conditions;

	public AnyOfBlockCondition(List<BlockCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ANY_OF;
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
