package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.ModifyValue;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyEffectDurationPower extends Power {

	public static final MapCodec<ModifyEffectDurationPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ExtraCodecs.nonEmptyList(Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(ModifyEffectDurationPower::getModifiers))
		.apply(instance, ModifyEffectDurationPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyEffectDurationPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		ByteBufCodecs.collection(ObjectArrayList::new, Modifier.STREAM_CODEC), ModifyEffectDurationPower::getModifiers,
		ModifyEffectDurationPower::new
	);

	private final List<Modifier> modifiers;

	public ModifyEffectDurationPower(Optional<Condition> activeCondition, List<Modifier> modifiers) {
		super(activeCondition);
		this.modifiers = modifiers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_EFFECT_DURATION;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<ModifyEffectDurationPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyEffectDurationPower power) {
			super(holder, power);
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

	}

	public static int modify(Context context, List<Instance> instances, int original) {

		List<Modifier.Entry> entries = new ObjectArrayList<>();

		for (var instance : instances) {

			Context instanceContext = new Context.Builder(context)
				.withValidator(instance.createValidator())
				.build(context.getLevel());

			try {

				if (!instanceContext.markActive(instance) || !instance.isActive(instanceContext)) {
					continue;
				}

				ListIterator<Modifier> listIterator = instance.getModifiers().listIterator();
				while (listIterator.hasNext()) {

					int index = listIterator.nextIndex();
					Modifier modifier = listIterator.next();

					entries.add(Modifier.entry(modifier, instanceContext.forChild(".modifiers[" + index + "]")));

				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		ModifyValue.EVENT.invoker().beforeModified(PowerTypes.MODIFY_EFFECT_DURATION, entries, context, original);
		return (int) Math.round(Modifier.applyAll(entries, original));

	}

	public static Context createContext(@NotNull Entity holder, @Nullable Entity source, MobEffectInstance effectInstance) {
		return PowerTypes.MODIFY_EFFECT_DURATION.contextBuilder()
			.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, source)
			.add(NeoApoliContextKeys.TARGET_ENTITY, holder)
			.add(NeoApoliContextKeys.THIS_ENTITY, holder)
			.add(NeoApoliContextKeys.THIS_POS, holder.position())
			.add(NeoApoliContextKeys.EFFECT_INSTANCE, effectInstance)
			.build(holder.level());
	}

}
