package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.direction.DirectionProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record BoneMealAction(Vec3Provider position, BooleanProvider showEffects, Optional<DirectionProvider> offsetDirection) implements Action {

	public static final MapCodec<BoneMealAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("position").forGetter(BoneMealAction::position),
		BooleanProvider.CODEC.optionalFieldOf("show_effects", new ConstantBooleanProvider(true)).forGetter(BoneMealAction::showEffects),
		DirectionProvider.CODEC.optionalFieldOf("offset_direction").forGetter(BoneMealAction::offsetDirection)
	).apply(instance, BoneMealAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BoneMealAction> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, BoneMealAction::position,
		BooleanProvider.STREAM_CODEC, BoneMealAction::showEffects,
		ByteBufCodecs.optional(DirectionProvider.STREAM_CODEC), BoneMealAction::offsetDirection,
		BoneMealAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.BONE_MEAL;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		Context positionContext = context.forChild(".position");
		BlockPos pos = BlockPos.containing(position().getVec3(positionContext));

		if (!positionContext.hasErrors()) {

			if (BoneMealItem.growCrop(ItemStack.EMPTY, serverLevel, pos)) {
				this.showEffects(context, pos);
			}

			else {

				Direction offsetDirection = offsetDirection().flatMap(self -> self.nextDirection(context.forChild(".offset_direction"))).orElse(null);
				BlockState state = serverLevel.getBlockState(pos);

				if (offsetDirection != null && state.isFaceSturdy(serverLevel, pos, offsetDirection) && BoneMealItem.growWaterPlant(ItemStack.EMPTY, serverLevel, pos.relative(offsetDirection), offsetDirection)) {
					this.showEffects(context, pos);
				}

			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		position().validate(validator.forChild(".position"));
		showEffects().validate(validator.forChild(".show_effects"));
		offsetDirection().ifPresent(offsetDirection -> offsetDirection.validate(validator.forChild(".offset_direction")));
	}

	private void showEffects(Context context, BlockPos blockPos) {

		if (showEffects().getBoolean(context.forChild(".show_effects"))) {
			context.level().levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 15);
		}

	}

}
