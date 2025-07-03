package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@EqualsAndHashCode
@Data
public final class AreaOfEffectBlockAction extends BlockAction {

	public static final MapCodec<AreaOfEffectBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("block_action").forGetter(AreaOfEffectBlockAction::blockAction),
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(AreaOfEffectBlockAction::blockCondition),
		Shape.CODEC.fieldOf("shape").forGetter(AreaOfEffectBlockAction::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(AreaOfEffectBlockAction::radius)
	).apply(instance, AreaOfEffectBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, AreaOfEffectBlockAction> PACKET_CODEC = PacketCodec.tuple(
		BlockAction.PACKET_CODEC, AreaOfEffectBlockAction::blockAction,
		BlockCondition.PACKET_CODEC, AreaOfEffectBlockAction::blockCondition,
		Shape.PACKET_CODEC, AreaOfEffectBlockAction::shape,
		NumberProvider.PACKET_CODEC, AreaOfEffectBlockAction::radius,
		AreaOfEffectBlockAction::new
	);

	private final BlockAction blockAction;
	private final BlockCondition blockCondition;

	private final Shape shape;
	private final NumberProvider radius;

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.AREA_OF_EFFECT;
	}

	@Override
	protected void impl(ServerContext context) {

		ServerWorld world = context.getWorld();
		BlockPos originPos = this.getBlockPos(context);

		ServerContext radiusContext = context.makeChild(".radius");
		int radius = radius().nextInt(radiusContext);

		if (radiusContext.hasErrors() || radius <= 0) {
			return;
		}

		for (BlockPos pos : shape().getBlockPositions(originPos, radius)) {

			ServerContext blockContext = context.copy(builder -> builder
				.add(ContextParameters.POSITION, pos.toCenterPos())
				.add(ContextParameters.BLOCK_STATE, world.getBlockState(pos))
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos)));

			if (blockCondition().test(blockContext.makeChild(".block_condition"))) {
				blockAction().execute(blockContext.makeChild(".block_action"));
			}

		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		blockAction().validate(reporter.makeChild(".block_action"));
		blockCondition().validate(reporter.makeChild(".block_condition"));

		radius().validate(reporter.makeChild(".radius"));

	}

}
