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
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class CallbackDamageDealtPower extends Power implements Prioritized<CallbackDamageDealtPower> {

	public static final MapCodec<CallbackDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.fieldOf("on_hit_action").forGetter(CallbackDamageDealtPower::getOnHitAction))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(CallbackDamageDealtPower::getPriority))
		.apply(instance, CallbackDamageDealtPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackDamageDealtPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Action.STREAM_CODEC, CallbackDamageDealtPower::getOnHitAction,
		ByteBufCodecs.INT, CallbackDamageDealtPower::getPriority,
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
		return new io.github.eggohito.neo_apoli.power.custom.CallbackDamageDealtPower.Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		super.validate(reporter);
		getOnHitAction().validate(reporter.forChild(".on_hit_action"));
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

		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		InstanceCollection<io.github.eggohito.neo_apoli.power.custom.CallbackDamageDealtPower.Instance> instances = new InstanceCollection<>(holder, io.github.eggohito.neo_apoli.power.custom.CallbackDamageDealtPower.Instance.class);

		execute(context, instances);

	}

	public static void execute(Context context, InstanceCollection<io.github.eggohito.neo_apoli.power.custom.CallbackDamageDealtPower.Instance> instances) {

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
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
			.add(NeoApoliContextKeys.ACTOR, actor)
			.add(NeoApoliContextKeys.TARGET, target)
			.add(NeoApoliContextKeys.DAMAGE_SOURCE, damageSource)
			.add(NeoApoliContextKeys.DAMAGE_AMOUNT, damageAmount)
			.addNullable(NeoApoliContextKeys.DAMAGING_ENTITY, damageSource.getEntity())
			.addNullable(NeoApoliContextKeys.DIRECT_DAMAGING_ENTITY, damageSource.getDirectEntity())
			.add(NeoApoliContextKeys.THIS_ENTITY, actor)
			.add(NeoApoliContextKeys.ENTITY_POS, actor.position())
			.build(actor.level());
	}

}
