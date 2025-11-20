package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
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

	public static final PacketCodec<RegistryByteBuf, ModifyDamageDealtPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Action.PACKET_CODEC, ModifyDamageDealtPower::getOnModifyAction,
		PacketCodecs.collection(ObjectArrayList::new, Modifier.PACKET_CODEC), ModifyDamageDealtPower::getModifiers,
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
		return new Instance(holder, this);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);
		getOnModifyAction().validate(reporter.makeChild(".on_modify_action"));

		ListIterator<Modifier> listIterator = getModifiers().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			Modifier modifier = listIterator.next();

			modifier.validate(reporter.makeChild(".modifiers[" + index + "]"));

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

	public static float modify(Context context, List<Instance> instances, float baseValue) {

		List<Modifier> modifiers = new ObjectArrayList<>();

		for (var instance : instances) {
			instance.execute(context);
			modifiers.addAll(instance.getModifiers());
		}

		return (float) Modifier.applyAll(index -> context.makeChild(".modifiers[" + index + "]"), modifiers, baseValue);

	}

	public static Context createContext(Entity actor, Entity target, DamageSource damageSource, float damageAmount) {
		return PowerTypes.MODIFY_DAMAGE_DEALT.contextBuilder()
			.add(NeoApoliContextParameters.ACTOR, actor)
			.add(NeoApoliContextParameters.TARGET, target)
			.add(NeoApoliContextParameters.DAMAGE_SOURCE, damageSource)
			.add(NeoApoliContextParameters.DAMAGE_AMOUNT, damageAmount)
			.addNullable(NeoApoliContextParameters.DAMAGING_ENTITY, damageSource.getAttacker())
			.addNullable(NeoApoliContextParameters.DIRECT_DAMAGING_ENTITY, damageSource.getSource())
			.add(NeoApoliContextParameters.THIS_ENTITY, actor)
			.add(NeoApoliContextParameters.ENTITY_POS, actor.getPos())
			.build(actor.getWorld());
	}

}
