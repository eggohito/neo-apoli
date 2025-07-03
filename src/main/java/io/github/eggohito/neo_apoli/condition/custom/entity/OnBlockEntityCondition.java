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
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@EqualsAndHashCode
@Data
public final class OnBlockEntityCondition extends EntityCondition {

	public static final MapCodec<OnBlockEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(OnBlockEntityCondition::blockCondition)
	).apply(instance, OnBlockEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, OnBlockEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, OnBlockEntityCondition::blockCondition,
		OnBlockEntityCondition::new
	);

	private final BlockCondition blockCondition;

	public OnBlockEntityCondition(BlockCondition blockCondition) {
		this.blockCondition = blockCondition;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ON_BLOCK;
	}

	@Override
	protected boolean impl(Context context) {

		Entity entity = context.required(ContextParameters.THIS_ENTITY);
		BlockPos steppingPos = entity.getSteppingPos();

		World world = context.getWorld();
		Context blockConditionContext = context.copy(builder -> builder
			.add(ContextParameters.POSITION, steppingPos.toCenterPos())
			.add(ContextParameters.BLOCK_STATE, world.getBlockState(steppingPos))
			.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(steppingPos)));

		return blockCondition().test(blockConditionContext.makeChild(".block_condition"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		blockCondition().validate(reporter.makeChild(".block_condition"));
	}

}
