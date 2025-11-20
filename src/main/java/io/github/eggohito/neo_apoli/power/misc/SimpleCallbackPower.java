package io.github.eggohito.neo_apoli.power.misc;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;
import java.util.function.BiFunction;

@Getter
public abstract class SimpleCallbackPower extends Power {

	private final Action action;

	public SimpleCallbackPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition);
		this.action = action;
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		getAction().validate(reporter.makeChild(".action"));
	}

	protected static <P extends SimpleCallbackPower> MapCodec<P> createSimpleCallbackCodec(BiFunction<Optional<Condition>, Action, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
			.and(Action.CODEC.fieldOf("action").forGetter(SimpleCallbackPower::getAction))
			.apply(instance, constructor));
	}

	protected static <P extends SimpleCallbackPower> PacketCodec<RegistryByteBuf, P> createSimpleCallbackPacketCodec(BiFunction<Optional<Condition>, Action, P> constructor) {
		return PacketCodec.tuple(
			PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
			Action.PACKET_CODEC, SimpleCallbackPower::getAction,
			constructor
		);
	}

}
