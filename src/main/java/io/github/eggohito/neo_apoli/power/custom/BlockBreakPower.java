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
import java.util.List;

public class BlockBreakPower extends Power implements Prioritized<BlockBreakPower> {

	public static final MapCodec<BlockBreakPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(Actions.CODEC.forGetter(BlockBreakPower::getActions))
		.and(Conditions.CODEC.forGetter(BlockBreakPower::getConditions))
		.and(Codec.BOOL.optionalFieldOf("only_when_harvested", false).forGetter(BlockBreakPower::onlyWhenHarvested))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(BlockBreakPower::getPriority))
		.apply(instance, BlockBreakPower::new));

	public static final PacketCodec<RegistryByteBuf, BlockBreakPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, blockBreakPower) -> {
			Actions.PACKET_CODEC.encode(buf, blockBreakPower.getActions());
			Conditions.PACKET_CODEC.encode(buf, blockBreakPower.getConditions());
			buf.writeBoolean(blockBreakPower.onlyWhenHarvested());
			buf.writeVarInt(blockBreakPower.getPriority());
		},
		(buf, properties, condition) -> new BlockBreakPower(properties, condition,
			Actions.PACKET_CODEC.decode(buf),
			Conditions.PACKET_CODEC.decode(buf),
			buf.readBoolean(),
			buf.readVarInt()
		)
	);

	private final Actions actions;
	private final Conditions conditions;

	private final boolean onlyWhenHarvested;
	private final int priority;

	public BlockBreakPower(Properties properties, EntityCondition activeCondition, Actions actions, Conditions conditions, boolean onlyWhenHarvested, int priority) {
		super(properties, activeCondition);
		this.actions = actions;
		this.conditions = conditions;
		this.onlyWhenHarvested = onlyWhenHarvested;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.BLOCK_BREAK;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getActions().validate(reporter);
		getConditions().validate(reporter);

	}

	@Override
	public int getPriority() {
		return priority;
	}

	public Actions getActions() {
		return actions;
	}

	public Conditions getConditions() {
		return conditions;
	}

	public boolean onlyWhenHarvested() {
		return onlyWhenHarvested;
	}

	public static class Impl extends Power.Impl<BlockBreakPower> implements Prioritized<Impl> {

		protected Impl(@NotNull Entity holder, @NotNull BlockBreakPower power) {
			super(holder, power);
		}

		@Override
		public int getPriority() {
			return power.getPriority();
		}

		public void execute(Context blockContext, Context entityAndItemContext) {

			Actions actions = power.getActions();

			this.executeAndReport("block_action", actions.blockAction(), blockContext);
			this.executeAndReport("entity_action", actions.entityAction(), entityAndItemContext);

		}

		public boolean doesApply(Context blockContext, Context entityAndItemContext, boolean harvested) {
			Conditions conditions = power.getConditions();
			return (!power.onlyWhenHarvested() || harvested)
				&& (conditions.directions().isEmpty() || blockContext.optional(ContextParameters.DIRECTION).map(conditions.directions()::contains).orElse(true))
				&& this.testAndReport("block_condition", conditions.blockCondition(), blockContext)
				&& this.isActive(entityAndItemContext);
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

		public void validate(ContextAware.ErrorReporter reporter) {
			blockAction().validate(reporter.makeChild("block_action"));
			entityAction().validate(reporter.makeChild("entity_action"));
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

		public void validate(ContextAware.ErrorReporter reporter) {
			blockCondition().validate(reporter.makeChild("block_condition"));
		}

	}

	public static void execute(PlayerEntity player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction, boolean harvested) {

		CallInstance<Impl> implInstances = CallInstance.create(player, Impl.class, impl -> true);

		for (int priority = implInstances.getMaxPriority(); priority >= implInstances.getMinPriority(); priority--) {

			List<Impl> impls = implInstances.getImpls(priority);

			for (var impl : impls) {

				Context.Builder builder = impl.getPowerType().contextBuilder()
					.add(ContextParameters.THIS_ENTITY, player)
					.add(ContextParameters.BLOCK_STATE, state)
					.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
					.addNullable(ContextParameters.DIRECTION, direction);

				Context entityContext = builder
					.add(ContextParameters.POSITION, player.getPos())
					.build(player.getWorld());
				Context blockContext = builder
					.add(ContextParameters.POSITION, blockPos.toCenterPos())
					.build(player.getWorld());

				if (impl.doesApply(blockContext, entityContext, harvested)) {
					impl.execute(blockContext, entityContext);
				}

			}

		}

	}

}
