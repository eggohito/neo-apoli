package io.github.eggohito.neo_apoli.power.custom.misc;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.function.BiFunction;

@EqualsAndHashCode
@Getter
public abstract class CallbackPower extends Power {

	private final Action action;

	public CallbackPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition);
		this.action = action;
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getAction().validate(validator.forChild(".action"));
	}

	protected static <P extends CallbackPower> MapCodec<P> createSimpleCallbackCodec(BiFunction<Optional<Condition>, Action, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
			.and(Action.CODEC.fieldOf("action").forGetter(CallbackPower::getAction))
			.apply(instance, constructor));
	}

	protected static <P extends CallbackPower> StreamCodec<RegistryFriendlyByteBuf, P> createSimpleCallbackStreamCodec(BiFunction<Optional<Condition>, Action, P> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
			Action.STREAM_CODEC, CallbackPower::getAction,
			constructor
		);
	}

}
