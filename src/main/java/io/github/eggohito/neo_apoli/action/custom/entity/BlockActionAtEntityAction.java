package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;

import java.util.Set;

public record BlockActionAtEntityAction(BlockAction blockAction) implements EntityAction {

	public static final ContextKeySet ACTION_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	public static final MapCodec<BlockActionAtEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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

		if (!context.hasParameter(NeoApoliContextParams.THIS_POS)) {
			return;
		}

		Level level = context.level();
		BlockPos blockPos = BlockPos.containing(context.getRequired(NeoApoliContextParams.THIS_POS));

		Context blockContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
			.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(blockPos))
			.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(blockPos))
			.build(level);

		blockAction().execute(blockContext.forChild(".block_action"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_POS);
	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		blockAction().validate(validator.withAdditionalKeysFromSets(ACTION_PARAMS).forChild(".block_action"));
	}

}
