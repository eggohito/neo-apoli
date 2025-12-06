package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

@Getter
public class ModifyAirSpeedPower extends Power {

	public static final MapCodec<ModifyAirSpeedPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ExtraCodecs.nonEmptyList(Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(ModifyAirSpeedPower::getModifiers))
		.apply(instance, ModifyAirSpeedPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyAirSpeedPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		ByteBufCodecs.collection(ObjectArrayList::new, Modifier.STREAM_CODEC), ModifyAirSpeedPower::getModifiers,
		ModifyAirSpeedPower::new
	);

	private final List<Modifier> modifiers;

	public ModifyAirSpeedPower(Optional<Condition> activeCondition, List<Modifier> modifiers) {
		super(activeCondition);
		this.modifiers = modifiers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_AIR_SPEED;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<ModifyAirSpeedPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyAirSpeedPower power) {
			super(holder, power);
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

	}

	public static float modify(Context context, float baseValue) {
		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		return modify(context, PowersComponent.getInstances(holder, Instance.class), baseValue);
	}

	public static float modify(Context context, List<Instance> instances, float baseValue) {

		List<Pair<Modifier, Context>> modifiers = new ObjectArrayList<>();

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (!instanceContext.markActive(instance) && !instance.isActive(instanceContext)) {
					continue;
				}

				ListIterator<Modifier> listIterator = instance.getModifiers().listIterator();

				while (listIterator.hasNext()) {

					int index = listIterator.nextIndex();
					Modifier modifier = listIterator.next();

					modifiers.add(Pair.of(modifier, instanceContext.makeChild(".modifiers[" + index + "]")));

				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return (float) Modifier.applyAll(modifiers, baseValue);

	}

	public static Context createContext(Entity entity) {
		return PowerTypes.MODIFY_AIR_SPEED.contextBuilder()
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());
	}

}
