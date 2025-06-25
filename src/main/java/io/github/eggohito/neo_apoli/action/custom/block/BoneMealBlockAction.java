package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

@EqualsAndHashCode(callSuper = false)
@Data
public final class BoneMealBlockAction extends BlockAction {

	public static final MapCodec<BoneMealBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("show_effects", true).forGetter(BoneMealBlockAction::showEffects)
	).apply(instance, BoneMealBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, BoneMealBlockAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.BOOLEAN, BoneMealBlockAction::showEffects,
		BoneMealBlockAction::new
	);

	private final boolean showEffects;

	public BoneMealBlockAction(boolean showEffects) {
		this.showEffects = showEffects;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.BONE_MEAL;
	}

	@Override
	protected void impl(Context context) {

		World world = context.getWorld();
		BlockPos blockPos = this.getBlockPos(context);

		if (BoneMealItem.useOnFertilizable(ItemStack.EMPTY, world, blockPos)) {
			this.showBoneMealEffect(world, blockPos);
		}

		else if (context.hasParameter(ContextParameters.DIRECTION)) {

			Direction direction = context.required(ContextParameters.DIRECTION);
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
