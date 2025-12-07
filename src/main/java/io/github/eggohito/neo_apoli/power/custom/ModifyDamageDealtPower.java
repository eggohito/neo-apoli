package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.integration.ModifyValueEvent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

@Getter
public class ModifyDamageDealtPower extends Power {

	public static final MapCodec<ModifyDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.optionalFieldOf("on_modify_action", new NothingAction()).forGetter(ModifyDamageDealtPower::getOnModifyAction))
		.and(Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifyDamageDealtPower::getModifiers))
		.apply(instance, ModifyDamageDealtPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyDamageDealtPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Action.STREAM_CODEC, ModifyDamageDealtPower::getOnModifyAction,
		ByteBufCodecs.collection(ObjectArrayList::new, Modifier.STREAM_CODEC), ModifyDamageDealtPower::getModifiers,
		ModifyDamageDealtPower::new
	);

	private final Action onModifyAction;
	private final List<Modifier> modifiers;

	public ModifyDamageDealtPower(Optional<Condition> activeCondition, Action onModifyAction, List<Modifier> modifiers) {
		super(activeCondition);
		this.onModifyAction = onModifyAction;
		this.modifiers = modifiers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_DAMAGE_DEALT;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new io.github.eggohito.neo_apoli.power.custom.ModifyDamageDealtPower.Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		super.validate(reporter);
		getOnModifyAction().validate(reporter.forChild(".on_modify_action"));

		ListIterator<Modifier> listIterator = getModifiers().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			Modifier modifier = listIterator.next();

			modifier.validate(reporter.forChild(".modifiers[" + index + "]"));

		}

	}

	public static class Instance extends Power.Instance<ModifyDamageDealtPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyDamageDealtPower power) {
			super(holder, power);
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

		public void execute(Context context) {
			power.getOnModifyAction().execute(context.makeChild(".on_modify_action"));
		}

	}

	public static float modify(Context context, float baseValue) {
		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		return modify(context, PowersComponent.getInstances(holder, Instance.class), baseValue);
	}

	public static float modify(Context context, List<Instance> instances, float baseValue) {

		List<Modifier.Entry> modifiers = new ObjectArrayList<>();
		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (!instanceContext.markActive(instance) || !instance.isActive(instanceContext)) {
					continue;
				}

				ListIterator<Modifier> listIterator = instance.getModifiers().listIterator();
				instance.execute(instanceContext);

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

		ModifyValueEvent.INSTANCE.invoker().beforeModified(PowerTypes.MODIFY_DAMAGE_DEALT, modifiers, context, baseValue);
		return (float) Modifier.applyAll(modifiers, baseValue);

	}

	public static Context createContext(Entity actor, Entity target, DamageSource damageSource, float damageAmount) {
		return PowerTypes.MODIFY_DAMAGE_DEALT.contextBuilder()
			.add(NeoApoliContextKeys.ACTOR_ENTITY, actor)
			.add(NeoApoliContextKeys.TARGET_ENTITY, target)
			.add(NeoApoliContextKeys.DAMAGE_SOURCE, damageSource)
			.add(NeoApoliContextKeys.DAMAGE_AMOUNT, damageAmount)
			.addNullable(NeoApoliContextKeys.DAMAGING_ENTITY, damageSource.getEntity())
			.addNullable(NeoApoliContextKeys.DIRECT_DAMAGING_ENTITY, damageSource.getDirectEntity())
			.add(NeoApoliContextKeys.THIS_ENTITY, actor)
			.add(NeoApoliContextKeys.THIS_POS, actor.position())
			.build(actor.level());
	}

}
