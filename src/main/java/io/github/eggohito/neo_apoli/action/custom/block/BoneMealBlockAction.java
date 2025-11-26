package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
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
	public void serverExecute(ServerContext context) {

		if (!context.hasParameter(NeoApoliContextKeys.BLOCK_POS)) {
			return;
		}

		ServerLevel world = context.getWorld();
		BlockPos blockPos = context.required(NeoApoliContextKeys.BLOCK_POS);

		if (BoneMealItem.growCrop(ItemStack.EMPTY, world, blockPos)) {
			this.showEffects(context, blockPos);
		}

		else if (context.hasParameter(NeoApoliContextKeys.DIRECTION)) {

			Direction direction = context.required(NeoApoliContextKeys.DIRECTION);
			BlockState blockState = world.getBlockState(blockPos);

			if (blockState.isFaceSturdy(world, blockPos, direction) && BoneMealItem.growWaterPlant(ItemStack.EMPTY, world, blockPos.relative(direction), direction)) {
				this.showEffects(context, blockPos);
			}

		}

	}

	@Override
	public void validate(ProblemReporter reporter) {
		BlockAction.super.validate(reporter);
		showEffects().validate(reporter.forChild(".show_effects"));
	}

	private void showEffects(ServerContext context, BlockPos blockPos) {

		Context showEffectsContext = context.makeChild(".show_effects");
		boolean showEffects = showEffects().next(showEffectsContext);

		if (!showEffectsContext.hasErrors() && showEffects) {
			context.getWorld().levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 15);
		}

	}

}
