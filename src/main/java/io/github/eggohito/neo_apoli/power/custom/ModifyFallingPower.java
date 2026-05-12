package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.ModifyValue;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextHelper;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyFallingPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyFallingPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ExtraCodecs.nonEmptyList(Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(ModifyFallingPower::getModifiers))
		.and(BooleanProvider.CODEC.optionalFieldOf("take_fall_damage", new ConstantBooleanProvider(true)).forGetter(ModifyFallingPower::getTakeFallDamage))
		.apply(instance, ModifyFallingPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyFallingPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		ByteBufCodecs.collection(ObjectArrayList::new, Modifier.STREAM_CODEC), ModifyFallingPower::getModifiers,
		BooleanProvider.STREAM_CODEC, ModifyFallingPower::getTakeFallDamage,
		ModifyFallingPower::new
	);

	private final List<Modifier> modifiers;
	private final BooleanProvider takeFallDamage;

	public ModifyFallingPower(Optional<Condition> activeCondition, List<Modifier> modifiers, BooleanProvider takeFallDamage) {
		super(activeCondition);
		this.modifiers = modifiers;
		this.takeFallDamage = takeFallDamage;
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_FALLING;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		ContextHelper.validateAll(getModifiers(), validator, index -> ".modifiers[" + index + "]");
		getTakeFallDamage().validate(validator.forChild(".take_fall_damage"));

	}

	public static class Instance extends Power.Instance<ModifyFallingPower> {

		protected Instance(@NotNull ModifyFallingPower power) {
			super(power);
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

		public boolean shouldNegateFallDamage(Context context) {
			return this.isActive(context)
				&& power.getTakeFallDamage().nextBoolean(context.forChild(".take_fall_damage"));
		}

	}

	public static boolean shouldNegateFallDamage(Entity entity) {

		for (var instance : Powers.getInstances(entity, Instance.class)) {

			Context context = instance.createHolderContext(entity);

			try {

				if (VISITOR.push(instance) && instance.shouldNegateFallDamage(context)) {
					return true;
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return false;

	}

	public static double modify(Entity entity, double effectiveGravity) {

		List<Modifier.Operation> modifiers = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(entity, Instance.class)) {

			Context context = instance.createHolderContext(entity);

			try {

				if (!VISITOR.push(instance) || !instance.isActive(context)) {
					continue;
				}

				ListIterator<Modifier> listIterator = instance.getModifiers().listIterator();

				while (listIterator.hasNext()) {

					Context modifierContext = context.forChild(".modifiers[" + listIterator.nextIndex() + "]");
					Modifier modifier = listIterator.next();

					modifiers.add(Modifier.operation(modifier, modifierContext));

				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		ModifyValue.EVENT.invoker().beforeModified(NeoApoliPowerTypes.MODIFY_FALLING, modifiers, effectiveGravity);
		return Modifier.applyAll(modifiers, effectiveGravity);

	}

}
