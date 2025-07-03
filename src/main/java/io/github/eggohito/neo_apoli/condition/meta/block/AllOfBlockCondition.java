package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class AllOfBlockCondition extends BlockCondition implements AllOfMetaCondition<BlockCondition> {

	public static final MapCodec<AllOfBlockCondition> CODEC = NeoApoliMapCodecs.lazy(AllOfBlockCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(BlockCondition.CODEC, AllOfBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfBlockCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AllOfBlockCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(BlockCondition.PACKET_CODEC, AllOfBlockCondition::new));

	private final List<BlockCondition> conditions;

	public AllOfBlockCondition(List<BlockCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ALL_OF;
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
