package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackDamageDealtPower extends Power implements Prioritized<CallbackDamageDealtPower> {

	public static final MapCodec<CallbackDamageDealtPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getOnHitAction().validate(validator.forChild(".on_hit_action"));
	}

	public static class Instance extends Power.Instance<CallbackDamageDealtPower> {

		protected Instance(@NotNull CallbackDamageDealtPower power) {
			super(power);
		}

		public Context createContext(Entity holder, Entity target, DamageSource source, float amount) {
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.ACTOR_ENTITY, holder)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, target)
				.withRequired(NeoApoliContextParams.DAMAGE_SOURCE, source)
				.withRequired(NeoApoliContextParams.DAMAGE_AMOUNT, amount)
				.withNullable(NeoApoliContextParams.DAMAGING_ENTITY, source.getEntity())
				.withNullable(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY, source.getDirectEntity())
				.buildWithRequirements(holder.level(), PowerTypes.CALLBACK_DAMAGE_DEALT.keySet());
		}

		public void execute(Context context) {
			power.getOnHitAction().execute(context.forChild(".on_hit_action"));
		}

	}

	public static void execute(Entity actor, Entity target, DamageSource damageSource, float damageAmount) {

		for (var instance : new InstanceCollection<>(actor, Instance.class)) {

			Context context = instance.createContext(actor, target, damageSource, damageAmount);

			if (instance.isActive(context)) {
				instance.execute(context);
			}

		}

	}

}
