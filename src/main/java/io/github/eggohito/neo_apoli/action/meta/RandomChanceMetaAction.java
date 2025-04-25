package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public interface RandomChanceMetaAction<A extends Action<T>, T extends ActionType<?>> extends Action<T> {

	A successAction();

	Optional<A> failAction();

	float chance();

	@Override
	default void execute(Context context) {

		if (context.getWorld().getRandom().nextFloat() < chance()) {
			successAction().execute(context.makeChild("success_action"));
		}

		else {
			failAction().ifPresent(failAction -> failAction.execute(context.makeChild("fail_action")));
		}

	}

	@Override
	default void validate(ErrorReporter reporter) {
		successAction().validate(reporter.makeChild("success_action"));
		failAction().ifPresent(failAction -> failAction.validate(reporter.makeChild("fail_action")));
	}

	static <A extends Action<?>, M extends RandomChanceMetaAction<A, ?>> MapCodec<M> createCodec(Codec<A> elementCodec, TriFunction<A, Optional<A>, Float, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.fieldOf("success_action").forGetter(RandomChanceMetaAction::successAction),
			elementCodec.optionalFieldOf("fail_action").forGetter(RandomChanceMetaAction::failAction),
			Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(RandomChanceMetaAction::chance)
		).apply(instance, constructor::apply));
	}

	static <B extends ByteBuf, A extends Action<?>, M extends RandomChanceMetaAction<A, ?>> PacketCodec<B, M> createPacketCodec(PacketCodec<B, A> elementCodec, TriFunction<A, Optional<A>, Float, M> constructor) {
		return PacketCodec.tuple(
			elementCodec, RandomChanceMetaAction::successAction,
			PacketCodecs.optional(elementCodec), RandomChanceMetaAction::failAction,
			PacketCodecs.FLOAT, RandomChanceMetaAction::chance,
			constructor::apply
		);
	}

}
