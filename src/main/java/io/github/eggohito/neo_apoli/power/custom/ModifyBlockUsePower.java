package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.item.NothingItemAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.BlockUsePhase;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EqualsAndHashCode
@Getter
public class ModifyBlockUsePower extends Power implements Prioritized<ModifyBlockUsePower> {

	public static final MapCodec<ModifyBlockUsePower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Actions.CODEC.forGetter(ModifyBlockUsePower::getActions))
		.and(Conditions.CODEC.forGetter(ModifyBlockUsePower::getConditions))
		.and(BlockUsePhase.SET_CODEC.optionalFieldOf("use_phases", EnumSet.allOf(BlockUsePhase.class)).forGetter(ModifyBlockUsePower::getUsePhases))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockUsePower::getPriority))
		.apply(instance, ModifyBlockUsePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyBlockUsePower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Actions.STREAM_CODEC, ModifyBlockUsePower::getActions,
		Conditions.STREAM_CODEC, ModifyBlockUsePower::getConditions,
		BlockUsePhase.SET_STREAM_CODEC, ModifyBlockUsePower::getUsePhases,
		ByteBufCodecs.INT, ModifyBlockUsePower::getPriority,
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
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getActions().validate(validator);
		getConditions().validate(validator);
	}

	public static class Instance extends Power.Instance<ModifyBlockUsePower> {

		protected Instance(@NotNull ModifyBlockUsePower power) {
			super(power);
		}

		public Context createContext(Entity holder, BlockHitResult blockResult, InteractionHand hand) {

			Level level = holder.level();
			BlockPos blockPos  = blockResult.getBlockPos();
			SlotAccess slotAccess = holder instanceof LivingEntity livingEntity
				? SlotAccess.of(() -> livingEntity.getItemInHand(hand), stack -> livingEntity.setItemInHand(hand, stack))
				: SlotAccess.NULL;

			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(blockPos))
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(blockPos))
				.withRequired(NeoApoliContextParams.DIRECTION, blockResult.getDirection())
				.withRequired(NeoApoliContextParams.SLOT_ACCESS, slotAccess)
				.withRequired(NeoApoliContextParams.ITEM_STACK, slotAccess.get())
				.buildWithRequirements(level, PowerTypes.MODIFY_BLOCK_USE.keySet());

		}

		public InteractionResult apply(Context context) {
			return power.getActions().execute(context);
		}

		public boolean doesApply(BlockUsePhase interactionPhase, PriorityPhase priorityPhase) {
			return power.getUsePhases().contains(interactionPhase)
				&& power.inPriorityPhase(priorityPhase);
		}

	}

	public record Actions(Action action, InteractionResult result) implements ContextUser {

		public static final MapCodec<Actions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Action.CODEC.optionalFieldOf("action", NothingItemAction.INSTANCE).forGetter(Actions::action),
			NeoApoliCodecs.ACTION_RESULT.optionalFieldOf("result", InteractionResult.SUCCESS).forGetter(Actions::result)
		).apply(instance, Actions::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Actions> STREAM_CODEC = StreamCodec.composite(
			Action.STREAM_CODEC, Actions::action,
			NeoApoliStreamCodecs.ACTION_RESULT, Actions::result,
			Actions::new
		);

		@Override
		public void validate(Context.Validator validator) {
			ContextUser.super.validate(validator);
			action().validate(validator.forChild(".action"));
		}

		public InteractionResult execute(Context context) {
			action().execute(context.forChild(".action"));
			return result();
		}

	}

	public record Conditions(EnumSet<Direction> directions, EnumSet<InteractionHand> hands) implements ContextUser {

		public static final MapCodec<Conditions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			NeoApoliCodecs.DIRECTION_SET.optionalFieldOf("directions", EnumSet.allOf(Direction.class)).forGetter(Conditions::directions),
			NeoApoliCodecs.HAND_SET.optionalFieldOf("hands", EnumSet.allOf(InteractionHand.class)).forGetter(Conditions::hands)
		).apply(instance, Conditions::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Conditions> STREAM_CODEC = StreamCodec.composite(
			NeoApoliStreamCodecs.DIRECTION_SET, Conditions::directions,
			NeoApoliStreamCodecs.HAND_SET, Conditions::hands,
			Conditions::new
		);

		public boolean test(Context context, InteractionHand hand) {
			return context.getOptional(NeoApoliContextParams.DIRECTION).map(directions()::contains).orElse(false)
				&& hands().contains(hand);
		}

	}

	public static InteractionResult execute(Player player, InteractionHand hand, BlockHitResult blockHitResult, BlockUsePhase interactionPhase, PriorityPhase priorityPhase, Consumer<InteractionResult> zeroPriorityResultSetter, Supplier<InteractionResult> zeroPriorityResultGetter, Supplier<InteractionResult> defaultValueSupplier) {
		return switch (priorityPhase) {
			case BEFORE ->
				executeOnBeforeBlockUse(player, hand, blockHitResult, interactionPhase, zeroPriorityResultSetter, defaultValueSupplier);
			case AFTER ->
				executeOnAfterBlockUse(player, hand, blockHitResult, interactionPhase, zeroPriorityResultGetter, defaultValueSupplier);
		};
	}

	private static InteractionResult executeOnBeforeBlockUse(Player player, InteractionHand hand, BlockHitResult blockHitResult, BlockUsePhase interactionPhase, Consumer<InteractionResult> zeroPriorityResultSetter, Supplier<InteractionResult> defaultResultSupplier) {

		InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(player, Instance.class, instance -> instance.doesApply(interactionPhase, PriorityPhase.BEFORE));

		for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority(); priority--) {

			if (!instanceCollection.hasInstances(priority)) {
				continue;
			}

			List<Instance> instances = instanceCollection.getInstances(priority);
			InteractionResult previousResult = InteractionResult.PASS;

			for (var instance : instances) {

				Context context = instance.createContext(player, blockHitResult, hand);

				if (instance.isActive(context)) {
					previousResult = MiscUtil.overrideResult(previousResult, instance.apply(context));
				}

			}

			boolean previousPassed = previousResult == InteractionResult.PASS;
			boolean zeroPriority = priority == 0;

			if (previousPassed || zeroPriority) {

				if (zeroPriority) {
					zeroPriorityResultSetter.accept(previousResult);
				}

				continue;

			}

			if (previousResult instanceof InteractionResult.Success(InteractionResult.SwingSource swingSource, InteractionResult.ItemContext ignored) && swingSource != InteractionResult.SwingSource.NONE) {
				player.swing(hand, swingSource == InteractionResult.SwingSource.SERVER);
			}

			return previousResult;

		}

		return defaultResultSupplier.get();

	}

	private static InteractionResult executeOnAfterBlockUse(Player player, InteractionHand hand, BlockHitResult blockHitResult, BlockUsePhase interactionPhase, Supplier<InteractionResult> zeroPriorityResultGetter, Supplier<InteractionResult> defaultResultSupplier) {

		InteractionResult original = defaultResultSupplier.get();
		InteractionResult modified = InteractionResult.PASS;

		InteractionResult zeroPriorityResult = zeroPriorityResultGetter.get();

		if (zeroPriorityResult != null && zeroPriorityResult != InteractionResult.PASS) {
			modified = zeroPriorityResult;
		}

		else if (original == InteractionResult.PASS) {

			InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(player, Instance.class, instance -> instance.doesApply(interactionPhase, PriorityPhase.AFTER));

			for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority(); priority--) {

				if (!instanceCollection.hasInstances(priority)) {
					continue;
				}

				List<Instance> instances = instanceCollection.getInstances(priority);
				InteractionResult previousResult = InteractionResult.PASS;

				for (var instance : instances) {

					Context context = instance.createContext(player, blockHitResult, hand);

					if (instance.isActive(context)) {
						previousResult = MiscUtil.overrideResult(previousResult, instance.apply(context));
					}

				}

				if (previousResult != InteractionResult.PASS) {
					modified = previousResult;
					break;
				}

			}

		}

		if (modified instanceof InteractionResult.Success(InteractionResult.SwingSource swingSource, InteractionResult.ItemContext ignored) && swingSource != InteractionResult.SwingSource.NONE) {
			player.swing(hand, swingSource == InteractionResult.SwingSource.SERVER);
		}

		return MiscUtil.overrideResult(original, modified);

	}

}
