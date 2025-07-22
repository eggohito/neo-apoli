package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@EqualsAndHashCode
@Data
public final class IsOnBlockEntityCondition extends EntityCondition {

	public static final MapCodec<IsOnBlockEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(IsOnBlockEntityCondition::blockCondition)
	).apply(instance, IsOnBlockEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOnBlockEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, IsOnBlockEntityCondition::blockCondition,
		IsOnBlockEntityCondition::new
	);

	private final BlockCondition blockCondition;

	public IsOnBlockEntityCondition(BlockCondition blockCondition) {
		this.blockCondition = blockCondition;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_ON_BLOCK;
	}

	@Override
	protected boolean impl(Context context) {

		Entity entity = context.required(ContextParameters.ENTITY);
		BlockPos steppingPos = entity.getSteppingPos();

		World world = context.getWorld();
		Context blockContext = context.copy(builder -> builder
			.withContextType(ContextTypes.merge(context.getType(), ContextTypes.BLOCK))
			.add(ContextParameters.BLOCK_POS, steppingPos)
			.add(ContextParameters.BLOCK_STATE, world.getBlockState(steppingPos))
			.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(steppingPos)));

		return blockCondition().test(blockContext.makeChild(".block_condition"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		blockCondition().validate(reporter
			.withContextType(ContextTypes.merge(reporter.getContextType(), ContextTypes.BLOCK))
			.makeChild(".block_condition"));
	}

}
