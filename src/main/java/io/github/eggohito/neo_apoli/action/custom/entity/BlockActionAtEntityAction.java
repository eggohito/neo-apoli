package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;

import java.util.Set;

public record BlockActionAtEntityAction(BlockAction blockAction) implements EntityAction {

	public static final MapCodec<BlockActionAtEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("block_action").forGetter(BlockActionAtEntityAction::blockAction)
	).apply(instance, BlockActionAtEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockActionAtEntityAction> STREAM_CODEC = StreamCodec.composite(
		BlockAction.STREAM_CODEC, BlockActionAtEntityAction::blockAction,
		BlockActionAtEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.BLOCK_ACTION_AT;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasParameter(NeoApoliContextKeys.THIS_POS)) {
			return;
		}

		Level world = context.getWorld();
		BlockPos blockPos = BlockPos.containing(context.required(NeoApoliContextKeys.THIS_POS));

		Context blockContext = ContextImpl.of(context, builder -> builder
			.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, world.getBlockState(blockPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

		blockAction().execute(blockContext.makeChild(".block_action"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.THIS_POS);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		EntityAction.super.validate(reporter);
		blockAction().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.forChild(".block_action"));
	}

}
