package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackBlockBreakPower extends Power implements Prioritized<CallbackBlockBreakPower> {

	public static final MapCodec<CallbackBlockBreakPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.fieldOf("on_break_action").forGetter(CallbackBlockBreakPower::getOnBreakAction))
		.and(BooleanProvider.CODEC.optionalFieldOf("only_when_harvested", new ConstantBooleanProvider(false)).forGetter(CallbackBlockBreakPower::getOnlyWhenHarvested))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(CallbackBlockBreakPower::getPriority))
		.apply(instance, CallbackBlockBreakPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackBlockBreakPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Action.STREAM_CODEC, CallbackBlockBreakPower::getOnBreakAction,
		BooleanProvider.STREAM_CODEC, CallbackBlockBreakPower::getOnlyWhenHarvested,
		ByteBufCodecs.INT, CallbackBlockBreakPower::getPriority,
		CallbackBlockBreakPower::new
	);

	private final Action onBreakAction;
	private final BooleanProvider onlyWhenHarvested;
	private final int priority;

	public CallbackBlockBreakPower(Optional<Condition> activeCondition, Action onBreakAction, BooleanProvider onlyWhenHarvested, int priority) {
		super(activeCondition);
		this.onBreakAction = onBreakAction;
		this.onlyWhenHarvested = onlyWhenHarvested;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_BLOCK_BREAK;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		getOnBreakAction().validate(validator.forChild(".on_break_action"));
		getOnlyWhenHarvested().validate(validator.forChild(".only_when_harvested"));

	}

	public static class Instance extends Power.Instance<CallbackBlockBreakPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackBlockBreakPower power) {
			super(holder, power);
		}

		public Context createContext(BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, @Nullable Direction side) {
			return this.createHolderContextBuilder()
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, blockState)
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, blockEntity)
				.withNullable(NeoApoliContextParams.DIRECTION, side)
				.buildWithRequirements(holder.level(), PowerTypes.CALLBACK_BLOCK_BREAK.keySet());
		}

		public boolean doesApply(Context context, boolean harvested) {
			return this.isActive(context)
				&& (!power.getOnlyWhenHarvested().nextBoolean(context.forChild(".only_when_harvested")) || harvested);
		}

		public void execute(Context context) {
			power.getOnBreakAction().execute(context.forChild(".on_break_action"));
		}

	}

	public static void execute(Player placer, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, @Nullable Direction side, boolean harvested) {

		for (var instance : new InstanceCollection<>(placer, Instance.class)) {

			Context context = instance.createContext(blockPos, blockState, blockEntity, side);

			if (instance.doesApply(context, harvested)) {
				instance.execute(context);
			}

		}

	}

}
