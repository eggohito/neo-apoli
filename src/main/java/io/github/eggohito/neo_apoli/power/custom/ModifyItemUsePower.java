package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
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
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
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

	public static final MapCodec<ModifyItemUsePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.fieldOf("on_use_action").forGetter(ModifyItemUsePower::getOnUseAction))
		.and(NeoApoliCodecs.ACTION_RESULT.optionalFieldOf("result", ActionResult.SUCCESS).forGetter(ModifyItemUsePower::getResult))
		.and(NeoApoliCodecs.HAND_SET.optionalFieldOf("hands", EnumSet.allOf(Hand.class)).forGetter(ModifyItemUsePower::getHands))
		.and(TriggerType.CODEC.fieldOf("trigger_type").forGetter(ModifyItemUsePower::getTriggerType))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyItemUsePower::getPriority))
		.apply(instance, ModifyItemUsePower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyItemUsePower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Action.PACKET_CODEC, ModifyItemUsePower::getOnUseAction,
		NeoApoliPacketCodecs.ACTION_RESULT, ModifyItemUsePower::getResult,
		NeoApoliPacketCodecs.HAND_SET, ModifyItemUsePower::getHands,
		TriggerType.PACKET_CODEC, ModifyItemUsePower::getTriggerType,
		PacketCodecs.INTEGER, ModifyItemUsePower::getPriority,
		ModifyItemUsePower::new
	);

	private final Action onUseAction;
	private final ActionResult result;

	private final EnumSet<Hand> hands;
	private final TriggerType triggerType;

	private final int priority;

	public ModifyItemUsePower(Optional<Condition> activeCondition, Action onUseAction, ActionResult result, EnumSet<Hand> hands, TriggerType triggerType, int priority) {
		super(activeCondition);
		this.onUseAction = onUseAction;
		this.result = result;
		this.hands = hands;
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
		getOnUseAction().validate(reporter.makeChild(".on_use_action"));
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public static class Instance extends Power.Instance<ModifyItemUsePower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyItemUsePower power) {
			super(holder, power);
		}

		public ActionResult execute(Context context) {
			power.getOnUseAction().execute(context.makeChild(".on_use_action"));
			return power.getResult();
		}

		public boolean shouldExecute(PriorityPhase priorityPhase, TriggerType triggerType) {
			return power.inPriorityPhase(priorityPhase)
				&& Objects.equals(power.getTriggerType(), triggerType);
		}

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

				Context context = createContext(user, hand, stackReference);

				if (instance.isActive(context)) {

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

					Context context = createContext(user, hand, stackReference);

					if (instance.isActive(context)) {

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

	public static Context createContext(LivingEntity user, Hand hand, StackReference stackReference) {
		return PowerTypes.MODIFY_ITEM_USE.contextBuilder()
			.add(NeoApoliContextParameters.HAND, hand)
			.add(NeoApoliContextParameters.THIS_ENTITY, user)
			.add(NeoApoliContextParameters.ENTITY_POS, user.getPos())
			.add(NeoApoliContextParameters.STACK_REFERENCE, stackReference)
			.add(NeoApoliContextParameters.ITEM_STACK, stackReference.get())
			.build(user.getWorld());
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
