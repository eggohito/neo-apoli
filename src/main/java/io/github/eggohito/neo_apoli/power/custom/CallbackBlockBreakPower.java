package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
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

@Getter
public class CallbackBlockBreakPower extends Power implements Prioritized<CallbackBlockBreakPower> {

	public static final MapCodec<CallbackBlockBreakPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
		return new io.github.eggohito.neo_apoli.power.custom.CallbackBlockBreakPower.Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		super.validate(reporter);

		getOnBreakAction().validate(reporter.forChild(".on_break_action"));
		getOnlyWhenHarvested().validate(reporter.forChild(".only_when_harvested"));

	}

	public static class Instance extends Power.Instance<CallbackBlockBreakPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackBlockBreakPower power) {
			super(holder, power);
		}

		public void execute(Context context) {
			power.getOnBreakAction().execute(context.makeChild(".on_break_action"));
		}

		public boolean doesApply(Context context, boolean harvested) {
			return this.isActive(context)
				&& (!power.getOnlyWhenHarvested().next(context.makeChild(".only_when_harvested")) || harvested);
		}

	}

	public static void execute(Context context, boolean harvested) {

		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		InstanceCollection<io.github.eggohito.neo_apoli.power.custom.CallbackBlockBreakPower.Instance> instances = new InstanceCollection<>(holder, io.github.eggohito.neo_apoli.power.custom.CallbackBlockBreakPower.Instance.class);

		execute(context, instances, harvested);

	}

	public static void execute(Context context, InstanceCollection<io.github.eggohito.neo_apoli.power.custom.CallbackBlockBreakPower.Instance> instances, boolean harvested) {

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && instance.doesApply(instanceContext, harvested)) {
					instance.execute(instanceContext);
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

	}

	public static Context createContext(Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
		return PowerTypes.CALLBACK_BLOCK_BREAK.contextBuilder()
			.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, blockState)
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, blockEntity)
			.addNullable(NeoApoliContextKeys.DIRECTION, direction)
			.add(NeoApoliContextKeys.THIS_ENTITY, player)
			.add(NeoApoliContextKeys.THIS_POS, player.position())
			.build(player.level());
	}

}
