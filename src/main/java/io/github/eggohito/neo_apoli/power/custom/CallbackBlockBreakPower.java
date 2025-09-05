package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.block.NothingBlockAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class CallbackBlockBreakPower extends Power implements Prioritized<CallbackBlockBreakPower> {

	public static final MapCodec<CallbackBlockBreakPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(Actions.CODEC.forGetter(CallbackBlockBreakPower::getActions))
		.and(Conditions.CODEC.forGetter(CallbackBlockBreakPower::getConditions))
		.and(Codec.BOOL.optionalFieldOf("only_when_harvested", false).forGetter(CallbackBlockBreakPower::onlyWhenHarvested))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(CallbackBlockBreakPower::getPriority))
		.apply(instance, CallbackBlockBreakPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackBlockBreakPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			Actions.PACKET_CODEC.encode(buf, power.getActions());
			Conditions.PACKET_CODEC.encode(buf, power.getConditions());
			buf.writeBoolean(power.onlyWhenHarvested());
			buf.writeVarInt(power.getPriority());
		},
		(buf, properties, condition) -> new CallbackBlockBreakPower(properties, condition,
			Actions.PACKET_CODEC.decode(buf),
			Conditions.PACKET_CODEC.decode(buf),
			buf.readBoolean(),
			buf.readVarInt()
		)
	);

	@Getter
	private final Actions actions;
	@Getter
	private final Conditions conditions;

	private final boolean onlyWhenHarvested;
	@Getter
	private final int priority;

	public CallbackBlockBreakPower(Properties properties, Optional<EntityCondition> activeCondition, Actions actions, Conditions conditions, boolean onlyWhenHarvested, int priority) {
		super(properties, activeCondition);
		this.actions = actions;
		this.conditions = conditions;
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

		getActions().validate(reporter);
		getConditions().validate(reporter);

	}

	public boolean onlyWhenHarvested() {
		return onlyWhenHarvested;
	}

	public static class Instance extends Power.Instance<CallbackBlockBreakPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackBlockBreakPower power) {
			super(holder, power);
		}

		public void execute(Context context) {
			power.getActions().execute(context);
		}

		public boolean doesApply(Context context, boolean harvested) {
			return (!power.onlyWhenHarvested() || harvested)
				&& power.getConditions().test(context)
				&& this.isActive(context);
		}

	}

	public record Actions(BlockAction blockAction, EntityAction entityAction) {

		public static final MapCodec<Actions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockAction.CODEC.optionalFieldOf("block_action", new NothingBlockAction()).forGetter(Actions::blockAction),
			EntityAction.CODEC.optionalFieldOf("entity_action", new NothingEntityAction()).forGetter(Actions::entityAction)
		).apply(instance, Actions::new));

		public static final PacketCodec<RegistryByteBuf, Actions> PACKET_CODEC = PacketCodec.tuple(
			BlockAction.PACKET_CODEC, Actions::blockAction,
			EntityAction.PACKET_CODEC, Actions::entityAction,
			Actions::new
		);

		public void execute(Context context) {
			blockAction().execute(context.makeChild(".block_action"));
			entityAction().execute(context.makeChild(".entity_action"));
		}

		public void validate(ContextAware.ErrorReporter reporter) {
			blockAction().validate(reporter.makeChild(".block_action"));
			entityAction().validate(reporter.makeChild(".entity_action"));
		}

	}

	public record Conditions(BlockCondition blockCondition, EnumSet<Direction> directions) {

		public static final MapCodec<Conditions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(Conditions::blockCondition),
			NeoApoliCodecs.DIRECTION_SET.optionalFieldOf("directions", EnumSet.allOf(Direction.class)).forGetter(Conditions::directions)
		).apply(instance, Conditions::new));

		public static final PacketCodec<RegistryByteBuf, Conditions> PACKET_CODEC = PacketCodec.tuple(
			BlockCondition.PACKET_CODEC, Conditions::blockCondition,
			NeoApoliPacketCodecs.DIRECTION_SET, Conditions::directions,
			Conditions::new
		);

		public boolean test(Context context) {
			return context.optional(ContextParameters.DIRECTION).map(directions()::contains).orElse(false)
				&& blockCondition().test(context.makeChild(".block_condition"));
		}

		public void validate(ContextAware.ErrorReporter reporter) {
			blockCondition().validate(reporter.makeChild(".block_condition"));
		}

	}

	public static void execute(PlayerEntity player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, @Nullable Direction direction, boolean harvested) {

		for (var instance : new InstanceCollection<>(player, Instance.class, instance -> true)) {

			Context context = PowerTypes.CALLBACK_BLOCK_BREAK.contextBuilder()
				.add(ContextParameters.BLOCK_POS, blockPos)
				.add(ContextParameters.BLOCK_STATE, blockState)
				.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
				.addNullable(ContextParameters.DIRECTION, direction)
				.add(ContextParameters.ENTITY, player)
				.add(ContextParameters.ENTITY_POS, player.getPos())
				.build(player.getWorld());

			if (instance.doesApply(context, harvested)) {
				instance.execute(context);
			}

		}

	}

}
