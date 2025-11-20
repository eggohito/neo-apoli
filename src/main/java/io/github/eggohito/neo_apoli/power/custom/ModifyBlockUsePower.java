package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.item.NothingItemAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.BlockUsePhase;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class ModifyBlockUsePower extends Power implements Prioritized<ModifyBlockUsePower> {

	public static final MapCodec<ModifyBlockUsePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Actions.CODEC.forGetter(ModifyBlockUsePower::getActions))
		.and(Conditions.CODEC.forGetter(ModifyBlockUsePower::getConditions))
		.and(BlockUsePhase.SET_CODEC.optionalFieldOf("use_phases", EnumSet.allOf(BlockUsePhase.class)).forGetter(ModifyBlockUsePower::getUsePhases))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockUsePower::getPriority))
		.apply(instance, ModifyBlockUsePower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyBlockUsePower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Actions.PACKET_CODEC, ModifyBlockUsePower::getActions,
		Conditions.PACKET_CODEC, ModifyBlockUsePower::getConditions,
		BlockUsePhase.SET_PACKET_CODEC, ModifyBlockUsePower::getUsePhases,
		PacketCodecs.INTEGER, ModifyBlockUsePower::getPriority,
		ModifyBlockUsePower::new
	);

	private final Actions actions;
	private final Conditions conditions;

	private final EnumSet<BlockUsePhase> usePhases;
	private final int priority;

	public ModifyBlockUsePower(Optional<Condition> activeCondition, Actions actions, Conditions conditions, EnumSet<BlockUsePhase> usePhases, int priority) {
		super(activeCondition);
		this.actions = actions;
		this.conditions = conditions;
		this.usePhases = usePhases;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_BLOCK_USE;
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

	public static class Instance extends Power.Instance<ModifyBlockUsePower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyBlockUsePower power) {
			super(holder, power);
		}

		public boolean doesApply(BlockUsePhase interactionPhase, PriorityPhase priorityPhase) {
			return this.getPower().getUsePhases().contains(interactionPhase)
				&& this.getPower().inPriorityPhase(priorityPhase);
		}

		public ActionResult apply(Context context) {
			return power.getActions().execute(context);
		}

	}

	public record Actions(Action action, ActionResult result) implements ContextAware {

		public static final MapCodec<Actions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Action.CODEC.optionalFieldOf("action", new NothingItemAction()).forGetter(Actions::action),
			NeoApoliCodecs.ACTION_RESULT.optionalFieldOf("result", ActionResult.SUCCESS).forGetter(Actions::result)
		).apply(instance, Actions::new));

		public static final PacketCodec<RegistryByteBuf, Actions> PACKET_CODEC = PacketCodec.tuple(
			Action.PACKET_CODEC, Actions::action,
			NeoApoliPacketCodecs.ACTION_RESULT, Actions::result,
			Actions::new
		);

		@Override
		public void validate(ErrorReporter reporter) {
			ContextAware.super.validate(reporter);
			action().validate(reporter.makeChild(".action"));
		}

		public ActionResult execute(Context context) {
			action().execute(context.makeChild(".action"));
			return result();
		}

	}

	public record Conditions(EnumSet<Direction> directions, EnumSet<Hand> hands) implements ContextAware {

		public static final MapCodec<Conditions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			NeoApoliCodecs.DIRECTION_SET.optionalFieldOf("directions", EnumSet.allOf(Direction.class)).forGetter(Conditions::directions),
			NeoApoliCodecs.HAND_SET.optionalFieldOf("hands", EnumSet.allOf(Hand.class)).forGetter(Conditions::hands)
		).apply(instance, Conditions::new));

		public static final PacketCodec<RegistryByteBuf, Conditions> PACKET_CODEC = PacketCodec.tuple(
			NeoApoliPacketCodecs.DIRECTION_SET, Conditions::directions,
			NeoApoliPacketCodecs.HAND_SET, Conditions::hands,
			Conditions::new
		);

		public boolean test(Context context) {
			return context.optional(NeoApoliContextParameters.DIRECTION).map(directions()::contains).orElse(false)
				&& context.optional(NeoApoliContextParameters.HAND).map(hands()::contains).orElse(false);
		}

	}

	public static ActionResult execute(PlayerEntity player, Hand hand, BlockHitResult blockHitResult, BlockUsePhase interactionPhase, PriorityPhase priorityPhase, Consumer<ActionResult> zeroPriorityResultSetter, Supplier<ActionResult> zeroPriorityResultGetter, Supplier<ActionResult> defaultValueSupplier) {
		return switch (priorityPhase) {
			case BEFORE ->
				executeOnBeforeBlockUse(player, hand, blockHitResult, interactionPhase, zeroPriorityResultSetter, defaultValueSupplier);
			case AFTER ->
				executeOnAfterBlockUse(player, hand, blockHitResult, interactionPhase, zeroPriorityResultGetter, defaultValueSupplier);
		};
	}

	private static ActionResult executeOnBeforeBlockUse(PlayerEntity player, Hand hand, BlockHitResult blockHitResult, BlockUsePhase interactionPhase, Consumer<ActionResult> zeroPriorityResultSetter, Supplier<ActionResult> defaultResultSupplier) {

		InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(player, Instance.class, instance -> instance.doesApply(interactionPhase, PriorityPhase.BEFORE));

		for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority(); priority--) {

			if (!instanceCollection.hasInstances(priority)) {
				continue;
			}

			List<Instance> instances = instanceCollection.getInstances(priority);
			ActionResult previousResult = ActionResult.PASS;

			for (var instance : instances) {

				Context context = createContext(instance, player, hand, blockHitResult);

				if (instance.isActive(context)) {
					previousResult = MiscUtil.overrideResult(previousResult, instance.apply(context));
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

			if (previousResult instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext ignored) && swingSource != ActionResult.SwingSource.NONE) {
				player.swingHand(hand, swingSource == ActionResult.SwingSource.SERVER);
			}

			return previousResult;

		}

		return defaultResultSupplier.get();

	}

	private static ActionResult executeOnAfterBlockUse(PlayerEntity player, Hand hand, BlockHitResult blockHitResult, BlockUsePhase interactionPhase, Supplier<ActionResult> zeroPriorityResultGetter, Supplier<ActionResult> defaultResultSupplier) {

		ActionResult original = defaultResultSupplier.get();
		ActionResult modified = ActionResult.PASS;

		ActionResult zeroPriorityResult = zeroPriorityResultGetter.get();

		if (zeroPriorityResult != null && zeroPriorityResult != ActionResult.PASS) {
			modified = zeroPriorityResult;
		}

		else if (original == ActionResult.PASS) {

			InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(player, Instance.class, instance -> instance.doesApply(interactionPhase, PriorityPhase.AFTER));

			for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority(); priority--) {

				if (!instanceCollection.hasInstances(priority)) {
					continue;
				}

				List<Instance> instances = instanceCollection.getInstances(priority);
				ActionResult previousResult = ActionResult.PASS;

				for (var instance : instances) {

					Context context = createContext(instance, player, hand, blockHitResult);

					if (instance.isActive(context)) {
						previousResult = MiscUtil.overrideResult(previousResult, instance.apply(context));
					}

				}

				if (previousResult != ActionResult.PASS) {
					modified = previousResult;
					break;
				}

			}

		}

		if (modified instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext ignored) && swingSource != ActionResult.SwingSource.NONE) {
			player.swingHand(hand, swingSource == ActionResult.SwingSource.SERVER);
		}

		return MiscUtil.overrideResult(original, modified);

	}

	public static Context createContext(Instance instance, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {

		World world = player.getWorld();
		BlockPos blockPos = blockHitResult.getBlockPos();
		StackReference stackReference = StackReference.of(() -> player.getStackInHand(hand), stack -> player.setStackInHand(hand, stack));

		return instance.createHolderContextBuilder()
			.add(NeoApoliContextParameters.BLOCK_POS, blockPos)
			.add(NeoApoliContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos))
			.add(NeoApoliContextParameters.DIRECTION, blockHitResult.getSide())
			.add(NeoApoliContextParameters.STACK_REFERENCE, stackReference)
			.add(NeoApoliContextParameters.ITEM_STACK, stackReference.get())
			.add(NeoApoliContextParameters.HAND, hand)
			.build(world);

	}

}
