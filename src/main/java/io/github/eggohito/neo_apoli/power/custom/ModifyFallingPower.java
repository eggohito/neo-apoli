package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.integration.ModifyValueEvent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
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

	public static final MapCodec<ModifyFallingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_FALLING;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<ModifyFallingPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyFallingPower power) {
			super(holder, power);
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

		public boolean shouldNegateFallDamage(Context context) {
			return this.isActive(context)
				&& power.getTakeFallDamage().next(context.makeChild(".take_fall_damage"));
		}

	}

	public static boolean shouldNegateFallDamage(Context context) {
		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		return shouldNegateFallDamage(context, PowersComponent.getInstances(holder, Instance.class));
	}

	public static boolean shouldNegateFallDamage(Context context, List<Instance> instances) {

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && instance.shouldNegateFallDamage(context)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return false;

	}

	public static double modify(Context context, double baseValue) {
		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		return modify(context, PowersComponent.getInstances(holder, Instance.class), baseValue);
	}

	public static double modify(Context context, List<Instance> instances, double baseValue) {

		List<Modifier.Entry> modifiers = new ObjectArrayList<>();
		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (!instanceContext.markActive(instance) || !instance.isActive(context)) {
					continue;
				}

				ListIterator<Modifier> listIterator = instance.getModifiers().listIterator();

				while (listIterator.hasNext()) {

					int index = listIterator.nextIndex();
					Modifier modifier = listIterator.next();

					modifiers.add(Modifier.entry(modifier, instanceContext.makeChild(".modifiers[" + index + "]")));

				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		ModifyValueEvent.INSTANCE.invoker().beforeModified(PowerTypes.MODIFY_FALLING, modifiers, context, baseValue);
		return Modifier.applyAll(modifiers, baseValue);

	}

	public static Context createContext(Entity entity) {
		return PowerTypes.MODIFY_FALLING.contextBuilder()
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());
	}

}
