package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldEvents;

public record BoneMealBlockAction(BooleanProvider showEffects) implements BlockAction {

	public static final MapCodec<BoneMealBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BooleanProvider.CODEC.optionalFieldOf("show_effects", new ConstantBooleanProvider(true)).forGetter(BoneMealBlockAction::showEffects))
		.apply(instance, BoneMealBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, BoneMealBlockAction> PACKET_CODEC = PacketCodec.tuple(
		BooleanProvider.PACKET_CODEC, BoneMealBlockAction::showEffects,
		BoneMealBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.BONE_MEAL;
	}

	@Override
	public void serverExecute(ServerContext context) {

		if (!context.hasParameter(ContextParameters.BLOCK_POS)) {
			return;
		}

		ServerWorld world = context.getWorld();
		BlockPos blockPos = context.required(ContextParameters.BLOCK_POS);

		if (BoneMealItem.useOnFertilizable(ItemStack.EMPTY, world, blockPos)) {
			this.showEffects(context, blockPos);
		}

		else if (context.hasParameter(ContextParameters.DIRECTION)) {

			Direction direction = context.required(ContextParameters.DIRECTION);
			BlockState blockState = world.getBlockState(blockPos);

			if (blockState.isSideSolidFullSquare(world, blockPos, direction) && BoneMealItem.useOnGround(ItemStack.EMPTY, world, blockPos.offset(direction), direction)) {
				this.showEffects(context, blockPos);
			}

		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		BlockAction.super.validate(reporter);
		showEffects().validate(reporter.makeChild(".show_effects"));
	}

	private void showEffects(ServerContext context, BlockPos blockPos) {

		Context showEffectsContext = context.makeChild(".show_effects");
		boolean showEffects = showEffects().next(showEffectsContext);

		if (!showEffectsContext.hasErrors() && showEffects) {
			context.getWorld().syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 15);
		}

	}

}
