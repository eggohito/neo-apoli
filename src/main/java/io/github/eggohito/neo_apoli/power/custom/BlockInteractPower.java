package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.block.NothingBlockAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.action.meta.item.NothingItemAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.item.ConstantItemCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.BlockInteractionPhase;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockInteractPower extends Power implements Prioritized<BlockInteractPower> {

	public static final MapCodec<BlockInteractPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(Actions.CODEC.forGetter(BlockInteractPower::getActions))
		.and(Conditions.CODEC.forGetter(BlockInteractPower::getConditions))
		.and(BlockInteractionPhase.SET_CODEC.optionalFieldOf("interaction_phases", EnumSet.of(BlockInteractionPhase.BLOCK)).forGetter(BlockInteractPower::getInteractionPhases))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(BlockInteractPower::getPriority))
		.apply(instance, BlockInteractPower::new));

	public static final PacketCodec<RegistryByteBuf, BlockInteractPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, blockInteractPower) -> {
			Actions.PACKET_CODEC.encode(buf, blockInteractPower.getActions());
			Conditions.PACKET_CODEC.encode(buf, blockInteractPower.getConditions());
			BlockInteractionPhase.SET_PACKET_CODEC.encode(buf, blockInteractPower.getInteractionPhases());
			buf.writeVarInt(blockInteractPower.getPriority());
		},
		(buf, properties, condition) -> new BlockInteractPower(properties, condition,
			Actions.PACKET_CODEC.decode(buf),
			Conditions.PACKET_CODEC.decode(buf),
			BlockInteractionPhase.SET_PACKET_CODEC.decode(buf),
			buf.readVarInt()
		)
	);

	public static final Identifier ID = NeoApoli.id("block_interact");

	private final Actions actions;
	private final Conditions conditions;

	private final EnumSet<BlockInteractionPhase> interactionPhases;
	private final int priority;

	public BlockInteractPower(Properties properties, EntityCondition activeCondition, Actions actions, Conditions conditions, EnumSet<BlockInteractionPhase> interactionPhases, int priority) {
		super(properties, activeCondition);
		this.actions = actions;
		this.conditions = conditions;
		this.interactionPhases = interactionPhases;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.BLOCK_INTERACT;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
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

	public EnumSet<BlockInteractionPhase> getInteractionPhases() {
		return interactionPhases;
	}

	public static class Impl extends Power.Impl<BlockInteractPower> implements Prioritized<BlockInteractPower.Impl> {

		protected Impl(@NotNull Entity holder, @NotNull BlockInteractPower power) {
			super(holder, power);
		}

		@Override
		public int getPriority() {
			return this.getPower().getPriority();
		}

		public boolean shouldExecute(BlockInteractionPhase interactionPhase, PriorityPhase priorityPhase) {
			return this.getPower().getInteractionPhases().contains(interactionPhase)
				&& this.inPriorityPhase(priorityPhase);
		}

		public boolean doesApply(Hand hand, Context entityAndItemContext, Context blockContext) {
			Conditions conditions = this.getPower().getConditions();
			return blockContext.optional(ContextParameters.DIRECTION).map(conditions.directions()::contains).orElse(true)
				&& conditions.hands().contains(hand)
				&& this.isActive(entityAndItemContext)
				&& this.testAndReport("block_condition", conditions.blockCondition(), blockContext)
				&& this.testAndReport("item_condition", conditions.itemCondition(), entityAndItemContext);

		}

		public ActionResult execute(Context entityAndItemContext, Context blockContext) {

			Actions actions = this.getPower().getActions();
			ActionResult result = actions.result();

			this.executeAndReport("block_action", actions.blockAction(), blockContext);
			this.executeAndReport("entity_action", actions.entityAction(), entityAndItemContext);
			this.executeAndReport("item_action", actions.itemAction(), entityAndItemContext);

			return result;

		}

	}

	public record Actions(BlockAction blockAction, EntityAction entityAction, ItemAction itemAction, ActionResult result) {

		public static final MapCodec<Actions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockAction.CODEC.optionalFieldOf("block_action", new NothingBlockAction()).forGetter(Actions::blockAction),
			EntityAction.CODEC.optionalFieldOf("entity_action", new NothingEntityAction()).forGetter(Actions::entityAction),
			ItemAction.CODEC.optionalFieldOf("item_action", new NothingItemAction()).forGetter(Actions::itemAction),
			NeoApoliCodecs.ACTION_RESULT.optionalFieldOf("result", ActionResult.SUCCESS).forGetter(Actions::result)
		).apply(instance, Actions::new));

		public static final PacketCodec<RegistryByteBuf, Actions> PACKET_CODEC = PacketCodec.tuple(
			BlockAction.PACKET_CODEC, Actions::blockAction,
			EntityAction.PACKET_CODEC, Actions::entityAction,
			ItemAction.PACKET_CODEC, Actions::itemAction,
			NeoApoliPacketCodecs.ACTION_RESULT, Actions::result,
			Actions::new
		);

	}

	public record Conditions(BlockCondition blockCondition, ItemCondition itemCondition, EnumSet<Direction> directions, EnumSet<Hand> hands) {

		public static final MapCodec<Conditions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(Conditions::blockCondition),
			ItemCondition.CODEC.optionalFieldOf("item_condition", new ConstantItemCondition(true)).forGetter(Conditions::itemCondition),
			NeoApoliCodecs.DIRECTION_SET.optionalFieldOf("directions", EnumSet.allOf(Direction.class)).forGetter(Conditions::directions),
			NeoApoliCodecs.HAND_SET.optionalFieldOf("hands", EnumSet.allOf(Hand.class)).forGetter(Conditions::hands)
		).apply(instance, Conditions::new));

		public static final PacketCodec<RegistryByteBuf, Conditions> PACKET_CODEC = PacketCodec.tuple(
			BlockCondition.PACKET_CODEC, Conditions::blockCondition,
			ItemCondition.PACKET_CODEC, Conditions::itemCondition,
			NeoApoliPacketCodecs.DIRECTION_SET, Conditions::directions,
			NeoApoliPacketCodecs.HAND_SET, Conditions::hands,
			Conditions::new
		);

	}

	public static ActionResult execute(PlayerEntity player, Hand hand, BlockHitResult blockHitResult, BlockInteractionPhase interactionPhase, PriorityPhase priorityPhase, Consumer<ActionResult> zeroPriorityResultSetter, Supplier<ActionResult> zeroPriorityResultGetter, Supplier<ActionResult> defaultValueSupplier) {
		return switch (priorityPhase) {
			case BEFORE ->
				executeOnBeforeBlockUse(player, hand, blockHitResult, interactionPhase, zeroPriorityResultSetter, defaultValueSupplier);
			case AFTER ->
				executeOnAfterBlockUse(player, hand, blockHitResult, interactionPhase, zeroPriorityResultGetter, defaultValueSupplier);
			default ->
				defaultValueSupplier.get();
		};
	}

	private static ActionResult executeOnBeforeBlockUse(PlayerEntity player, Hand hand, BlockHitResult blockHitResult, BlockInteractionPhase interactionPhase, Consumer<ActionResult> zeroPriorityResultSetter, Supplier<ActionResult> defaultResultSupplier) {

		World world = player.getWorld();
		BlockPos blockPos = blockHitResult.getBlockPos();

		CallInstance<Impl> implInstances = CallInstance.create(player, Impl.class, impl -> impl.shouldExecute(interactionPhase, PriorityPhase.BEFORE));
		for (int priority = implInstances.getMaxPriority(); priority >= implInstances.getMinPriority(); priority--) {

			if (!implInstances.hasImpls(priority)) {
				continue;
			}

			List<Impl> impls = implInstances.getImpls(priority);
			ActionResult previousResult = ActionResult.PASS;

			for (var impl : impls) {

				Context.Builder builder = impl.getPowerType().contextBuilder()
					.add(ContextParameters.THIS_ENTITY, player)
					.add(ContextParameters.ITEM_STACK, player.getStackInHand(hand))
					.add(ContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
					.addNullable(ContextParameters.DIRECTION, blockHitResult.getSide())
					.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos));

				Context entityAndItemContext = builder
					.add(ContextParameters.POSITION, player.getPos())
					.build(world);
				Context blockContext = builder
					.add(ContextParameters.POSITION, blockPos.toCenterPos())
					.build(world);

				if (impl.doesApply(hand, entityAndItemContext, blockContext)) {
					previousResult = MiscUtil.overrideResult(previousResult, impl.execute(entityAndItemContext, blockContext));
				}

			}

			boolean previousPassed = previousResult == ActionResult.PASS;
			boolean zeroPriority = priority == 0;

			if (previousPassed || zeroPriority) {

				if (zeroPriority) {
					zeroPriorityResultSetter.accept(previousResult);
				}

				continue;

			}

			if (previousResult instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext itemContext) && swingSource != ActionResult.SwingSource.NONE) {
				player.swingHand(hand, swingSource == ActionResult.SwingSource.SERVER);
			}

			return previousResult;

		}

		return defaultResultSupplier.get();

	}

	private static ActionResult executeOnAfterBlockUse(PlayerEntity player, Hand hand, BlockHitResult blockHitResult, BlockInteractionPhase interactionPhase, Supplier<ActionResult> zeroPriorityResultGetter, Supplier<ActionResult> defaultResultSupplier) {

		ActionResult original = defaultResultSupplier.get();
		ActionResult modified = ActionResult.PASS;

		ActionResult zeroPriorityResult = zeroPriorityResultGetter.get();
		World world = player.getWorld();

		if (zeroPriorityResult != null && zeroPriorityResult != ActionResult.PASS) {
			modified = zeroPriorityResult;
		}

		else if (original == ActionResult.PASS) {

			CallInstance<Impl> implInstances = CallInstance.create(player, Impl.class, impl -> impl.shouldExecute(interactionPhase, PriorityPhase.AFTER));
			BlockPos blockPos = blockHitResult.getBlockPos();

			for (int priority = implInstances.getMaxPriority(); priority >= implInstances.getMinPriority(); priority--) {

				if (!implInstances.hasImpls(priority)) {
					continue;
				}

				List<Impl> impls = implInstances.getImpls(priority);
				ActionResult previousResult = ActionResult.PASS;

				for (var impl : impls) {

					Context.Builder builder = impl.getPowerType().contextBuilder()
						.add(ContextParameters.THIS_ENTITY, player)
						.add(ContextParameters.ITEM_STACK, player.getStackInHand(hand))
						.add(ContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
						.addNullable(ContextParameters.DIRECTION, blockHitResult.getSide())
						.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos));

					Context entityContext = builder
						.add(ContextParameters.POSITION, player.getPos())
						.build(world);
					Context blockContext = builder
						.add(ContextParameters.POSITION, blockPos.toCenterPos())
						.build(world);

					if (impl.doesApply(hand, entityContext, blockContext)) {
						previousResult = MiscUtil.overrideResult(previousResult, impl.execute(entityContext, blockContext));
					}

				}

				if (previousResult != ActionResult.PASS) {
					modified = previousResult;
					break;
				}

			}

		}

		if (modified instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext itemContext) && swingSource != ActionResult.SwingSource.NONE) {
			player.swingHand(hand, swingSource == ActionResult.SwingSource.SERVER);
		}

		return MiscUtil.overrideResult(original, modified);

	}

}
