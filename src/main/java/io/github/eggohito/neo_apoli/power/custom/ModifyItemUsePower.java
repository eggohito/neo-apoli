package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.action.meta.item.NothingItemAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.item.ConstantItemCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class ModifyItemUsePower extends Power implements Prioritized<ModifyItemUsePower> {

	public static final MapCodec<ModifyItemUsePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(Actions.CODEC.forGetter(ModifyItemUsePower::getActions))
		.and(Conditions.CODEC.forGetter(ModifyItemUsePower::getConditions))
		.and(TriggerType.CODEC.optionalFieldOf("trigger_type", TriggerType.INSTANT).forGetter(ModifyItemUsePower::getTriggerType))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyItemUsePower::getPriority))
		.apply(instance, ModifyItemUsePower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyItemUsePower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			Actions.PACKET_CODEC.encode(buf, power.getActions());
			Conditions.PACKET_CODEC.encode(buf, power.getConditions());
			TriggerType.PACKET_CODEC.encode(buf, power.getTriggerType());
			buf.writeVarInt(power.getPriority());
		},
		(buf, properties, activeCondition) -> new ModifyItemUsePower(properties, activeCondition,
			Actions.PACKET_CODEC.decode(buf),
			Conditions.PACKET_CODEC.decode(buf),
			TriggerType.PACKET_CODEC.decode(buf),
			buf.readVarInt()
		)
	);

	private final Actions actions;
	private final Conditions conditions;

	private final TriggerType triggerType;
	private final int priority;

	public ModifyItemUsePower(Properties properties, Optional<EntityCondition> activeCondition, Actions actions, Conditions conditions, TriggerType triggerType, int priority) {
		super(properties, activeCondition);
		this.actions = actions;
		this.conditions = conditions;
		this.triggerType = triggerType;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ITEM_USE;
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

	@Override
	public int getPriority() {
		return priority;
	}

	public static ActionResult execute(World world, LivingEntity user, Hand hand, StackReference stackReference, TriggerType triggerType, PriorityPhase priorityPhase, Consumer<ActionResult> zeroPriorityResultSetter, Supplier<@Nullable ActionResult> zeroPriorityResultGetter, Supplier<ActionResult> defaultResultGetter) {
		InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(user, Instance.class, instance -> instance.shouldExecute(priorityPhase, triggerType));
		return switch (priorityPhase) {
			case BEFORE ->
				executeBeforeUse(instanceCollection, world, user, hand, stackReference, zeroPriorityResultSetter, defaultResultGetter);
			case AFTER ->
				executeAfterUse(instanceCollection, world, user, hand, stackReference, zeroPriorityResultGetter, defaultResultGetter);
		};
	}

	private static ActionResult executeBeforeUse(InstanceCollection<Instance> instanceCollection, World world, LivingEntity user, Hand hand, StackReference stackReference, Consumer<ActionResult> zeroPriorityResultSetter, Supplier<ActionResult> defaultResultGetter) {

		for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority() && instanceCollection.hasInstances(priority); priority--) {

			List<Instance> instances = instanceCollection.getInstances(priority);
			ActionResult previousResult = ActionResult.PASS;

			for (var instance: instances) {

				Context context = createContext(world, user, hand, stackReference);

				if (instance.doesApply(context)) {

					previousResult = instance.execute(context);

					if (!MiscUtil.isResultPass(previousResult)) {
						break;
					}

				}

			}

			boolean previousPassed = MiscUtil.isResultPass(previousResult);
			boolean zeroPriority = priority == 0;

			if (previousPassed || zeroPriority) {

				if (zeroPriority) {
					zeroPriorityResultSetter.accept(previousResult);
				}

				continue;

			}

			if (previousResult instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext ignored) && swingSource != ActionResult.SwingSource.NONE) {
				user.swingHand(hand, swingSource == ActionResult.SwingSource.SERVER);
			}

			return previousResult;

		}

		return defaultResultGetter.get();

	}

	private static ActionResult executeAfterUse(InstanceCollection<Instance> instanceCollection, World world, LivingEntity user, Hand hand, StackReference stackReference, Supplier<@Nullable ActionResult> zeroPriorityResultGetter, Supplier<ActionResult> defaultResultGetter) {

		ActionResult original = defaultResultGetter.get();
		ActionResult modified = ActionResult.PASS;

		ActionResult zeroPriorityResult = zeroPriorityResultGetter.get();

		if (zeroPriorityResult != null && !MiscUtil.isResultPass(zeroPriorityResult)) {
			modified = zeroPriorityResult;
		}

		else if (MiscUtil.isResultPass(original)) {

			for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority() && instanceCollection.hasInstances(priority); priority--) {

				List<Instance> instances = instanceCollection.getInstances(priority);
				ActionResult previousResult = ActionResult.PASS;

				for (var instance: instances) {

					Context context = createContext(world, user, hand, stackReference);

					if (instance.doesApply(context)) {

						previousResult = instance.execute(context);

						if (!MiscUtil.isResultPass(previousResult)) {
							break;
						}

					}

				}

				if (!MiscUtil.isResultPass(previousResult)) {
					modified = previousResult;
					break;
				}

			}

		}

		if (modified instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext ignored) && swingSource != ActionResult.SwingSource.NONE) {
			user.swingHand(hand, swingSource == ActionResult.SwingSource.SERVER);
		}

		return MiscUtil.overrideResult(original, modified);

	}

	public static Context createContext(World world, LivingEntity user, Hand hand, StackReference stackReference) {
		return PowerTypes.MODIFY_ITEM_USE.contextBuilder()
			.add(ContextParameters.HAND, hand)
			.add(ContextParameters.ENTITY, user)
			.add(ContextParameters.ENTITY_POS, user.getPos())
			.add(ContextParameters.STACK_REFERENCE, stackReference)
			.add(ContextParameters.ITEM_STACK, stackReference.get())
			.build(world);
	}

	public static class Instance extends Power.Instance<ModifyItemUsePower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyItemUsePower power) {
			super(holder, power);
		}

		public ActionResult execute(Context context) {
			return power.getActions().execute(context);
		}

		public boolean doesApply(Context context) {
			return power.getConditions().test(context);
		}

		public boolean shouldExecute(PriorityPhase priorityPhase, TriggerType triggerType) {
			return power.inPriorityPhase(priorityPhase)
				&& Objects.equals(power.getTriggerType(), triggerType);
		}

	}

	public record Actions(ItemAction itemAction, EntityAction entityAction, ActionResult result) {

		public static final MapCodec<Actions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemAction.CODEC.optionalFieldOf("item_action", new NothingItemAction()).forGetter(Actions::itemAction),
			EntityAction.CODEC.optionalFieldOf("entity_action", new NothingEntityAction()).forGetter(Actions::entityAction),
			NeoApoliCodecs.ACTION_RESULT.optionalFieldOf("result", ActionResult.SUCCESS).forGetter(Actions::result)
		).apply(instance, Actions::new));

		public static final PacketCodec<RegistryByteBuf, Actions> PACKET_CODEC = PacketCodec.tuple(
			ItemAction.PACKET_CODEC, Actions::itemAction,
			EntityAction.PACKET_CODEC, Actions::entityAction,
			NeoApoliPacketCodecs.ACTION_RESULT, Actions::result,
			Actions::new
		);

		public void validate(ContextAware.ErrorReporter reporter) {
			itemAction().validate(reporter.makeChild(".item_action"));
			entityAction().validate(reporter.makeChild(".entity_action"));
		}

		public ActionResult execute(Context context) {

			itemAction().execute(context.makeChild(".item_action"));
			entityAction().execute(context.makeChild(".entity_action"));

			if (result() instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext itemContext)) {
				return new ActionResult.Success(swingSource, new ActionResult.ItemContext(itemContext.incrementStat(), context.nullable(ContextParameters.ITEM_STACK)));
			}

			else {
				return result();
			}

		}

	}

	public record Conditions(ItemCondition itemCondition, EnumSet<Hand> hands) {

		public static final MapCodec<Conditions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemCondition.CODEC.optionalFieldOf("item_condition", new ConstantItemCondition(true)).forGetter(Conditions::itemCondition),
			NeoApoliCodecs.HAND_SET.optionalFieldOf("hands", EnumSet.allOf(Hand.class)).forGetter(Conditions::hands)
		).apply(instance, Conditions::new));

		public static final PacketCodec<RegistryByteBuf, Conditions> PACKET_CODEC = PacketCodec.tuple(
			ItemCondition.PACKET_CODEC, Conditions::itemCondition,
			NeoApoliPacketCodecs.HAND_SET, Conditions::hands,
			Conditions::new
		);

		public void validate(ContextAware.ErrorReporter reporter) {
			itemCondition().validate(reporter.makeChild(".item_condition"));
		}

		public boolean test(Context context) {
			return context.optional(ContextParameters.HAND).map(hands()::contains).orElse(false)
				&& itemCondition().test(context.makeChild(".item_condition"));
		}

	}

	public enum TriggerType implements StringIdentifiable {

		INSTANT("instant"),
		START("start"),
		STOP("stop"),
		FINISH("finish"),
		DURING("during");

		public static final Codec<TriggerType> CODEC = CodecUtil.enumType(TriggerType.class);
		public static final PacketCodec<ByteBuf, TriggerType> PACKET_CODEC = PacketCodecUtil.enumType(TriggerType.class);

		private final String name;
		TriggerType(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}

	}

}
