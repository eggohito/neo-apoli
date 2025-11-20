package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public record BlockActionAtEntityAction(BlockAction blockAction) implements EntityAction {

	public static final MapCodec<BlockActionAtEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("block_action").forGetter(BlockActionAtEntityAction::blockAction)
	).apply(instance, BlockActionAtEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, BlockActionAtEntityAction> PACKET_CODEC = PacketCodec.tuple(
		BlockAction.PACKET_CODEC, BlockActionAtEntityAction::blockAction,
		BlockActionAtEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.BLOCK_ACTION_AT;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasParameter(NeoApoliContextParameters.ENTITY_POS)) {
			return;
		}

		World world = context.getWorld();
		BlockPos blockPos = BlockPos.ofFloored(context.required(NeoApoliContextParameters.ENTITY_POS));

		Context blockContext = ContextImpl.of(context, builder -> builder
			.withContextType(ContextTypeUtil.merge(context.getType(), NeoApoliContextTypes.BLOCK))
			.add(NeoApoliContextParameters.BLOCK_POS, blockPos)
			.add(NeoApoliContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

		blockAction().execute(blockContext.makeChild(".block_action"));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.ENTITY_POS);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityAction.super.validate(reporter);
		blockAction().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.BLOCK))
			.makeChild(".block_action"));
	}

}
