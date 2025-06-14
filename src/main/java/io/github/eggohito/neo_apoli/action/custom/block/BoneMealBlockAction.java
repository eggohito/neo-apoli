package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.Optional;

public record BoneMealBlockAction(boolean showEffects) implements BlockAction {

	public static final MapCodec<BoneMealBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("show_effects", true).forGetter(BoneMealBlockAction::showEffects)
	).apply(instance, BoneMealBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, BoneMealBlockAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.BOOLEAN, BoneMealBlockAction::showEffects,
		BoneMealBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.BONE_MEAL;
	}

	@Override
	public void execute(Context context) {

		World world = context.getWorld();
		BlockPos blockPos = this.getBlockPos(context);

		Optional<Direction> optDirection = context.optional(ContextParameters.DIRECTION);

		if (BoneMealItem.useOnFertilizable(ItemStack.EMPTY, world, blockPos)) {
			this.showBoneMealEffect(world, blockPos);
		}

		else if (optDirection.isPresent()) {

			Direction direction = optDirection.get();
			BlockState blockState = this.getBlockState(context);

			if (blockState.isSideSolidFullSquare(world, blockPos, direction) && BoneMealItem.useOnGround(ItemStack.EMPTY, world, blockPos.offset(direction), direction)) {
				this.showBoneMealEffect(world, blockPos);
			}

		}

	}

	private void showBoneMealEffect(World world, BlockPos pos) {

		if (this.showEffects() && !world.isClient()) {
			world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 15);
		}

	}

}
