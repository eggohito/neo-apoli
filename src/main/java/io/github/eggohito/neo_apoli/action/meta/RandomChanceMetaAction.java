package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public interface RandomChanceMetaAction<AX extends ActionContext<?>, AA extends Action<AX, AT>, AT extends ActionType<?>> extends Action<AX, AT> {

	AA successAction();

	Optional<AA> failAction();

	float chance();

	@Override
	default void execute(ErrorReporter reporter, AX context) {

		if (Random.create().nextFloat() < chance()) {
			successAction().execute(reporter, context);
		}

		else {
			failAction().ifPresent(failAction -> failAction.execute(reporter, context));
		}

	}

	@Override
	default void validate(ErrorReporter reporter) {
		successAction().validate(reporter.makeChild("success_action"));
		failAction().ifPresent(failAction -> failAction.validate(reporter.makeChild("fail_action")));
	}

	static <AA extends Action<?, ?>, RCMA extends RandomChanceMetaAction<?, AA, ?>> MapCodec<RCMA> createCodec(Codec<AA> elementCodec, TriFunction<AA, Optional<AA>, Float, RCMA> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.fieldOf("success_action").forGetter(RandomChanceMetaAction::successAction),
			elementCodec.optionalFieldOf("fail_action").forGetter(RandomChanceMetaAction::failAction),
			Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(RandomChanceMetaAction::chance)
		).apply(instance, constructor::apply));
	}

	static <B extends ByteBuf, AA extends Action<?, ?>, RCMA extends RandomChanceMetaAction<?, AA, ?>> PacketCodec<B, RCMA> createPacketCodec(PacketCodec<B, AA> elementCodec, TriFunction<AA, Optional<AA>, Float, RCMA> constructor) {
		return PacketCodec.tuple(
			elementCodec, RandomChanceMetaAction::successAction,
			PacketCodecs.optional(elementCodec), RandomChanceMetaAction::failAction,
			PacketCodecs.FLOAT, RandomChanceMetaAction::chance,
			constructor::apply
		);
	}

}
