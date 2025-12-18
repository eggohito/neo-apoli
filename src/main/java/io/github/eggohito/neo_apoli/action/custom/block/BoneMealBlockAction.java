package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

public record BoneMealBlockAction(BooleanProvider showEffects) implements BlockAction {

	public static final MapCodec<BoneMealBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BooleanProvider.CODEC.optionalFieldOf("show_effects", new ConstantBooleanProvider(true)).forGetter(BoneMealBlockAction::showEffects))
		.apply(instance, BoneMealBlockAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BoneMealBlockAction> STREAM_CODEC = StreamCodec.composite(
		BooleanProvider.STREAM_CODEC, BoneMealBlockAction::showEffects,
		BoneMealBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.BONE_MEAL;
	}

	@Override
	public void execute(Context context) {

		BlockPos blockPos = context.nullable(NeoApoliContextKeys.BLOCK_POS);
		Direction direction = context.nullable(NeoApoliContextKeys.DIRECTION);

		if (context.getLevel() instanceof ServerLevel serverLevel && blockPos != null) {

			if (BoneMealItem.growCrop(ItemStack.EMPTY, serverLevel, blockPos)) {
				this.showEffects(context, blockPos);
			}

			else if (direction != null) {

				BlockState blockState = serverLevel.getBlockState(blockPos);

				if (blockState.isFaceSturdy(serverLevel, blockPos, direction) && BoneMealItem.growWaterPlant(ItemStack.EMPTY, serverLevel, blockPos.relative(direction), direction)) {
					this.showEffects(context, blockPos);
				}

			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		BlockAction.super.validate(validator);
		showEffects().validate(validator.forChild(".show_effects"));
	}

	private void showEffects(Context context, BlockPos blockPos) {

		Context showEffectsContext = context.forChild(".show_effects");
		boolean showEffects = showEffects().next(showEffectsContext);

		if (!showEffectsContext.hasErrors() && showEffects) {
			context.getLevel().levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 15);
		}

	}

}
