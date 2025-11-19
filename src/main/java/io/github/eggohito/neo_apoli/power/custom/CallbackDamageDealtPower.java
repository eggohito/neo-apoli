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
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
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
		.and(Action.BASE_CODEC.fieldOf("action").forGetter(CallbackDamageDealtPower::getAction))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(CallbackDamageDealtPower::getPriority))
		.apply(instance, CallbackDamageDealtPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackDamageDealtPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition,
		Action.BASE_PACKET_CODEC, CallbackDamageDealtPower::getAction,
		PacketCodecs.INTEGER, CallbackDamageDealtPower::getPriority,
		CallbackDamageDealtPower::new
	);

	private final Action action;
	private final int priority;

	public CallbackDamageDealtPower(Optional<Condition> activeCondition, Action action, int priority) {
		super(activeCondition);
		this.action = action;
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
		getAction().validate(reporter.makeChild(".action"));
	}

	public static class Instance extends Power.Instance<CallbackDamageDealtPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackDamageDealtPower power) {
			super(holder, power);
		}

		public void execute(Context context) {
			power.getAction().execute(context.makeChild(".action"));
		}

	}

	public static void execute(Context context, InstanceCollection<Instance> instances) {

		for (var instance : instances) {
			instance.execute(context);
		}

	}

	public static Context createContext(Entity actor, Entity target, DamageSource damageSource, float damageAmount) {
		return PowerTypes.CALLBACK_DAMAGE_DEALT.contextBuilder()
			.add(ContextParameters.ACTOR, actor)
			.add(ContextParameters.TARGET, target)
			.add(ContextParameters.DAMAGE_SOURCE, damageSource)
			.add(ContextParameters.DAMAGE_AMOUNT, damageAmount)
			.addNullable(ContextParameters.DAMAGING_ENTITY, damageSource.getAttacker())
			.addNullable(ContextParameters.DIRECT_DAMAGING_ENTITY, damageSource.getSource())
			.add(ContextParameters.THIS_ENTITY, actor)
			.add(ContextParameters.ENTITY_POS, actor.getPos())
			.build(actor.getWorld());
	}

}
