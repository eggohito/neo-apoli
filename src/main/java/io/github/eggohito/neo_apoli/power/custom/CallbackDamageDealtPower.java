package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class CallbackDamageDealtPower extends Power implements Prioritized<CallbackDamageDealtPower> {

	public static final MapCodec<CallbackDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.fieldOf("on_hit_action").forGetter(CallbackDamageDealtPower::getOnHitAction))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(CallbackDamageDealtPower::getPriority))
		.apply(instance, CallbackDamageDealtPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackDamageDealtPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Action.PACKET_CODEC, CallbackDamageDealtPower::getOnHitAction,
		PacketCodecs.INTEGER, CallbackDamageDealtPower::getPriority,
		CallbackDamageDealtPower::new
	);

	private final Action onHitAction;
	private final int priority;

	public CallbackDamageDealtPower(Optional<Condition> activeCondition, Action onHitAction, int priority) {
		super(activeCondition);
		this.onHitAction = onHitAction;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_DAMAGE_DEALT;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		getOnHitAction().validate(reporter.makeChild(".on_hit_action"));
	}

	public static class Instance extends Power.Instance<CallbackDamageDealtPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackDamageDealtPower power) {
			super(holder, power);
		}

		public void execute(Context context) {
			power.getOnHitAction().execute(context.makeChild(".on_hit_action"));
		}

	}

	public static void execute(Context context) {

		Entity holder = context.required(NeoApoliContextParameters.THIS_ENTITY);
		InstanceCollection<Instance> instances = new InstanceCollection<>(holder, Instance.class);

		execute(context, instances);

	}

	public static void execute(Context context, InstanceCollection<Instance> instances) {

		for (var instance : instances) {

			ErrorReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {
					instance.execute(instanceContext);
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

	}

	public static Context createContext(Entity actor, Entity target, DamageSource damageSource, float damageAmount) {
		return PowerTypes.CALLBACK_DAMAGE_DEALT.contextBuilder()
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
