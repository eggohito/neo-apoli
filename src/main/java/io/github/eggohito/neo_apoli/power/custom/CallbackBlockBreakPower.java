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
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

	public static final PacketCodec<RegistryByteBuf, CallbackBlockBreakPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Action.PACKET_CODEC, CallbackBlockBreakPower::getOnBreakAction,
		BooleanProvider.PACKET_CODEC, CallbackBlockBreakPower::getOnlyWhenHarvested,
		PacketCodecs.INTEGER, CallbackBlockBreakPower::getPriority,
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
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getOnBreakAction().validate(reporter.makeChild(".on_break_action"));
		getOnlyWhenHarvested().validate(reporter.makeChild(".only_when_harvested"));

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

		Entity holder = context.required(NeoApoliContextParameters.THIS_ENTITY);
		InstanceCollection<Instance> instances = new InstanceCollection<>(holder, Instance.class);

		execute(context, instances, harvested);

	}

	public static void execute(Context context, InstanceCollection<Instance> instances, boolean harvested) {

		for (var instance : instances) {

			ErrorReporter reporter = instance.createReporter();
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

	public static Context createContext(PlayerEntity player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
		return PowerTypes.CALLBACK_BLOCK_BREAK.contextBuilder()
			.add(NeoApoliContextParameters.BLOCK_POS, blockPos)
			.add(NeoApoliContextParameters.BLOCK_STATE, blockState)
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, blockEntity)
			.addNullable(NeoApoliContextParameters.DIRECTION, direction)
			.add(NeoApoliContextParameters.THIS_ENTITY, player)
			.add(NeoApoliContextParameters.ENTITY_POS, player.getPos())
			.build(player.getWorld());
	}

}
