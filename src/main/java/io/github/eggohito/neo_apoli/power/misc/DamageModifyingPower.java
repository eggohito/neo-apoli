package io.github.eggohito.neo_apoli.power.misc;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.api.event.ModifyValue;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public abstract class DamageModifyingPower extends Power {

	public static final ClearableVisitor<Instance<?>> VISITOR = ClearableVisitor.createThreadLocalized();

	private final List<Modifier> modifiers;
	private final Action onModifyAction;

	public DamageModifyingPower(Optional<Condition> activeCondition, List<Modifier> modifiers, Action onModifyAction) {
		super(activeCondition);
		this.modifiers = modifiers;
		this.onModifyAction = onModifyAction;
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);
		ListIterator<Modifier> listIterator = this.getModifiers().listIterator();

		while (listIterator.hasNext()) {

			Context.Validator modifierValidator = validator.forChild(".modifiers[" + listIterator.nextIndex() + "]");

			listIterator.next().validate(modifierValidator);

		}

		getOnModifyAction().validate(validator.forChild(".on_modify_action"));

	}

	protected static <P extends DamageModifyingPower> MapCodec<P> createDamageModifyingCodec(Function3<Optional<Condition>, List<Modifier>, Action, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
			.and(Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(DamageModifyingPower::getModifiers))
			.and(Action.CODEC.optionalFieldOf("on_modify_action", NothingAction.INSTANCE).forGetter(DamageModifyingPower::getOnModifyAction))
			.apply(instance, constructor));
	}

	protected static <P extends DamageModifyingPower> StreamCodec<RegistryFriendlyByteBuf, P> createDamageModifyingStreamCodec(Function3<Optional<Condition>, List<Modifier>, Action, P> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
			ByteBufCodecs.collection(ObjectArrayList::new, Modifier.STREAM_CODEC), DamageModifyingPower::getModifiers,
			Action.STREAM_CODEC, DamageModifyingPower::getOnModifyAction,
			constructor
		);
	}

	public static abstract class Instance<P extends DamageModifyingPower> extends Power.Instance<P> {

		protected Instance(@NotNull P power) {
			super(power);
		}

		public abstract Context createDamageContext(Entity actor, Entity target, DamageSource source, float amount);

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

		public void execute(Context context) {
			power.getOnModifyAction().execute(context.forChild(".on_modify_action"));
		}

	}

	public static <P extends DamageModifyingPower, I extends DamageModifyingPower.Instance<P>> float modify(PowerType<P> type, Class<I> instanceClass, Entity holder, Entity actor, Entity target, DamageSource source, float amount) {

		List<Modifier.Entry> entries = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(holder, instanceClass)) {

			Context context = instance.createDamageContext(actor, target, source, amount);

			try {

				if (!VISITOR.push(instance) || !instance.isActive(context)) {
					continue;
				}

				ListIterator<Modifier> listIterator = instance.getModifiers().listIterator();
				instance.execute(context);

				while (listIterator.hasNext()) {

					Context modifierContext = context.forChild(".modifiers[" + listIterator.nextIndex() + "]");
					Modifier modifier = listIterator.next();

					entries.add(Modifier.entry(modifier, modifierContext));

				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		ModifyValue.EVENT.invoker().beforeModified(type, entries, amount);
		return (float) Modifier.applyAll(entries, amount);

	}

}
