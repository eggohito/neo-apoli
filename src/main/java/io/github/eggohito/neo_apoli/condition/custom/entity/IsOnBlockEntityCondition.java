package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record IsOnBlockEntityCondition(BlockCondition blockCondition) implements EntityCondition {

	public static final MapCodec<IsOnBlockEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(IsOnBlockEntityCondition::blockCondition))
		.apply(instance, IsOnBlockEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOnBlockEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, IsOnBlockEntityCondition::blockCondition,
		IsOnBlockEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_ON_BLOCK;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		World world = context.getWorld();
		Entity entity = context.required(NeoApoliContextParameters.THIS_ENTITY);

		try {

			if (context.markActive(this)) {

				if (!entity.isOnGround()) {
					return false;
				}

				BlockPos steppingPos = entity.getSteppingPos();
				Context blockContext = ContextImpl.of(context, builder -> builder
					.withContextType(ContextTypeUtil.merge(context.getType(), NeoApoliContextTypes.BLOCK))
					.add(NeoApoliContextParameters.BLOCK_POS, steppingPos)
					.add(NeoApoliContextParameters.BLOCK_STATE, world.getBlockState(steppingPos))
					.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(steppingPos)));

				return blockCondition().test(blockContext.makeChild(".block_condition"));

			}

			else {
				return false;
			}

		}

		finally {
			context.markInActive(this);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityCondition.super.validate(reporter);
		blockCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.BLOCK))
			.makeChild(".block_condition"));
	}

}
