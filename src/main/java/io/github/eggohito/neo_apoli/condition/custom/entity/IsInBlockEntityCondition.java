package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public record IsInBlockEntityCondition(BlockCondition condition) implements EntityCondition {

	public static final MapCodec<IsInBlockEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockCondition.CODEC.fieldOf("block_condition").forGetter(IsInBlockEntityCondition::condition))
		.apply(instance, IsInBlockEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsInBlockEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, IsInBlockEntityCondition::condition,
		IsInBlockEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_IN_BLOCK;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		World world = context.getWorld();
		BlockPos blockPos = BlockPos.ofFloored(context.required(ContextParameters.ENTITY_POS));

		if (!world.isChunkLoaded(blockPos)) {
			return false;
		}

		Context blockContext = ContextImpl.of(context, builder -> builder
			.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BLOCK))
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
			.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

		return condition().test(blockContext.makeChild(".block_condition"));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.ENTITY_POS);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityCondition.super.validate(reporter);
		condition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BLOCK))
			.makeChild(".block_condition"));
	}

}
