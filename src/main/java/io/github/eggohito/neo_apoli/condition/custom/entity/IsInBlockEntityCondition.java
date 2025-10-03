package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@EqualsAndHashCode
@Data
public final class IsInBlockEntityCondition extends EntityCondition {

	public static final MapCodec<IsInBlockEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(IsInBlockEntityCondition::blockCondition)
	).apply(instance, IsInBlockEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsInBlockEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, IsInBlockEntityCondition::blockCondition,
		IsInBlockEntityCondition::new
	);

	private final BlockCondition blockCondition;

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_IN_BLOCK;
	}

	@Override
	protected boolean impl(Context context) {

		Entity entity = context.required(ContextParameters.ENTITY);
		BlockPos blockPos = entity.getBlockPos();

		World world = context.getWorld();
		Context blockContext = new ContextImpl.Builder(context)
			.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BLOCK))
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
			.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos))
			.build(context.getWorld());

		return blockCondition().test(blockContext.makeChild(".block_condition"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		blockCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BLOCK))
			.makeChild(".block_condition"));
	}

}
