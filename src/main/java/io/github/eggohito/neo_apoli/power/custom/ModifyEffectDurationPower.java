package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.ModifyValue;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
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

	public static final MapCodec<ModifyEffectDurationPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<ModifyEffectDurationPower> {

		protected Instance(@NotNull ModifyEffectDurationPower power) {
			super(power);
		}

		public Context createContext(Entity holder, MobEffectInstance effectInstance, @Nullable Entity source) {
			return this.createHolderContextBuilder(holder)
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, source)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, holder)
				.withRequired(NeoApoliContextParams.EFFECT_INSTANCE, effectInstance)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_EFFECT_DURATION.keySet());
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

	}

	public static int modify(Entity holder, MobEffectInstance effectInstance, @Nullable Entity source, int duration) {

		List<Modifier.Entry> entries = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(holder, Instance.class)) {

			Context context = instance.createContext(holder, effectInstance, source);

			if (!instance.isActive(context)) {
				continue;
			}

			ListIterator<Modifier> listIterator = instance.getModifiers().listIterator();

			while (listIterator.hasNext()) {

				Context modifierContext = context.forChild(".modifiers[" + listIterator.nextIndex() + "]");
				Modifier modifier = listIterator.next();

				entries.add(Modifier.entry(modifier, modifierContext));

			}

		}

		ModifyValue.EVENT.invoker().beforeModified(PowerTypes.MODIFY_EFFECT_DURATION, entries, duration);
		return (int) Math.round(Modifier.applyAll(entries, duration));

	}

}
