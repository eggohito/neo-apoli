package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ReferenceBlockCondition extends BlockCondition implements ReferenceMetaCondition<BlockCondition> {

	public static final MapCodec<ReferenceBlockCondition> CODEC = ReferenceMetaCondition.codec(ReferenceBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBlockCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceBlockCondition::new);

	private final Identifier value;

	public ReferenceBlockCondition(Identifier value) {
		this.value = value;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.REFERENCE;
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
