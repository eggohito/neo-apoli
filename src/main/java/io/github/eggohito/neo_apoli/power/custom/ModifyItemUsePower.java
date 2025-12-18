package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EqualsAndHashCode
@Getter
public class ModifyItemUsePower extends Power implements Prioritized<ModifyItemUsePower> {

	public static final MapCodec<ModifyItemUsePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.fieldOf("on_use_action").forGetter(ModifyItemUsePower::getOnUseAction))
		.and(NeoApoliCodecs.ACTION_RESULT.optionalFieldOf("result", InteractionResult.SUCCESS).forGetter(ModifyItemUsePower::getResult))
		.and(NeoApoliCodecs.HAND_SET.optionalFieldOf("hands", EnumSet.allOf(InteractionHand.class)).forGetter(ModifyItemUsePower::getHands))
		.and(TriggerType.CODEC.fieldOf("trigger_type").forGetter(ModifyItemUsePower::getTriggerType))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyItemUsePower::getPriority))
		.apply(instance, ModifyItemUsePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyItemUsePower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Action.STREAM_CODEC, ModifyItemUsePower::getOnUseAction,
		NeoApoliStreamCodecs.ACTION_RESULT, ModifyItemUsePower::getResult,
		NeoApoliStreamCodecs.HAND_SET, ModifyItemUsePower::getHands,
		TriggerType.STREAM_CODEC, ModifyItemUsePower::getTriggerType,
		ByteBufCodecs.INT, ModifyItemUsePower::getPriority,
		ModifyItemUsePower::new
	);

	private final Action onUseAction;
	private final InteractionResult result;

	private final EnumSet<InteractionHand> hands;
	private final TriggerType triggerType;

	private final int priority;

	public ModifyItemUsePower(Optional<Condition> activeCondition, Action onUseAction, InteractionResult result, EnumSet<InteractionHand> hands, TriggerType triggerType, int priority) {
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
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getOnUseAction().validate(validator.forChild(".on_use_action"));
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public static class Instance extends Power.Instance<ModifyItemUsePower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyItemUsePower power) {
			super(holder, power);
		}

		public InteractionResult execute(Context context) {
			power.getOnUseAction().execute(context.forChild(".on_use_action"));
			return power.getResult();
		}

		public boolean shouldExecute(PriorityPhase priorityPhase, TriggerType triggerType) {
			return power.inPriorityPhase(priorityPhase)
				&& Objects.equals(power.getTriggerType(), triggerType);
		}

	}

	public static InteractionResult execute(Level world, LivingEntity user, InteractionHand hand, SlotAccess stackReference, TriggerType triggerType, PriorityPhase priorityPhase, Consumer<InteractionResult> zeroPriorityResultSetter, Supplier<@Nullable InteractionResult> zeroPriorityResultGetter, Supplier<InteractionResult> defaultResultGetter) {
		InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(user, Instance.class, instance -> instance.shouldExecute(priorityPhase, triggerType));
		return switch (priorityPhase) {
			case BEFORE ->
				executeBeforeUse(instanceCollection, world, user, hand, stackReference, zeroPriorityResultSetter, defaultResultGetter);
			case AFTER ->
				executeAfterUse(instanceCollection, world, user, hand, stackReference, zeroPriorityResultGetter, defaultResultGetter);
		};
	}

	private static InteractionResult executeBeforeUse(InstanceCollection<Instance> instanceCollection, Level world, LivingEntity user, InteractionHand hand, SlotAccess stackReference, Consumer<InteractionResult> zeroPriorityResultSetter, Supplier<InteractionResult> defaultResultGetter) {

		for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority() && instanceCollection.hasInstances(priority); priority--) {

			List<Instance> instances = instanceCollection.getInstances(priority);
			InteractionResult previousResult = InteractionResult.PASS;

			for (var instance: instances) {

				Context context = createContext(instance, user, hand, stackReference);

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

			if (previousResult instanceof InteractionResult.Success(InteractionResult.SwingSource swingSource, InteractionResult.ItemContext ignored) && swingSource != InteractionResult.SwingSource.NONE) {
				user.swing(hand, swingSource == InteractionResult.SwingSource.SERVER);
			}

			return previousResult;

		}

		return defaultResultGetter.get();

	}

	private static InteractionResult executeAfterUse(InstanceCollection<Instance> instanceCollection, Level world, LivingEntity user, InteractionHand hand, SlotAccess stackReference, Supplier<@Nullable InteractionResult> zeroPriorityResultGetter, Supplier<InteractionResult> defaultResultGetter) {

		InteractionResult original = defaultResultGetter.get();
		InteractionResult modified = InteractionResult.PASS;

		InteractionResult zeroPriorityResult = zeroPriorityResultGetter.get();

		if (zeroPriorityResult != null && !MiscUtil.isResultPass(zeroPriorityResult)) {
			modified = zeroPriorityResult;
		}

		else if (MiscUtil.isResultPass(original)) {

			for (int priority = instanceCollection.getMaxPriority(); priority >= instanceCollection.getMinPriority() && instanceCollection.hasInstances(priority); priority--) {

				List<Instance> instances = instanceCollection.getInstances(priority);
				InteractionResult previousResult = InteractionResult.PASS;

				for (var instance: instances) {

					Context context = createContext(instance, user, hand, stackReference);

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

		if (modified instanceof InteractionResult.Success(InteractionResult.SwingSource swingSource, InteractionResult.ItemContext ignored) && swingSource != InteractionResult.SwingSource.NONE) {
			user.swing(hand, swingSource == InteractionResult.SwingSource.SERVER);
		}

		return MiscUtil.overrideResult(original, modified);

	}

	public static Context createContext(Instance instance, LivingEntity user, InteractionHand hand, SlotAccess stackReference) {
		return instance.createHolderContextBuilder()
			.add(NeoApoliContextKeys.HAND, hand)
			.add(NeoApoliContextKeys.THIS_ENTITY, user)
			.add(NeoApoliContextKeys.THIS_POS, user.position())
			.add(NeoApoliContextKeys.STACK_REFERENCE, stackReference)
			.add(NeoApoliContextKeys.ITEM_STACK, stackReference.get())
			.build(user.level());
	}

	public enum TriggerType implements StringRepresentable {

		INSTANT("instant"),
		START("start"),
		STOP("stop"),
		FINISH("finish"),
		DURING("during");

		public static final Codec<TriggerType> CODEC = CodecUtil.enumType(TriggerType.class);
		public static final StreamCodec<ByteBuf, TriggerType> STREAM_CODEC = StreamCodecUtil.enumType(TriggerType.class);

		private final String name;
		TriggerType(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}

	}

}
