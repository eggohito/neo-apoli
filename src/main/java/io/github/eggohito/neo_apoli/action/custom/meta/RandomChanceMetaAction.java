package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public interface RandomChanceMetaAction<A extends Action> extends MetaAction {

	A successAction();

	Optional<A> failAction();

	NumberProvider chance();

	@Override
	default void execute(Context context) {

		Context chanceContext = context.makeChild(".chance");
		float chance = MathHelper.clamp(chance().nextFloat(chanceContext), 0.0f, 1.0f);

		if (!chanceContext.hasErrors()) {

			if (context.getWorld().getRandom().nextFloat() < chance) {
				successAction().execute(context.makeChild(".success_action"));
			}

			else {
				failAction().ifPresent(elseAction -> elseAction.execute(context.makeChild(".fail_action")));
			}

		}

	}

	@Override
	default void validate(ErrorReporter reporter) {

		MetaAction.super.validate(reporter);

		successAction().validate(reporter.makeChild(".success_action"));
		failAction().ifPresent(failAction -> failAction.validate(reporter.makeChild(".fail_action")));

		chance().validate(reporter.makeChild(".chance"));

	}

	static <A extends Action, M extends RandomChanceMetaAction<A>> MapCodec<M> codec(Codec<A> actionCodec, Function3<A, Optional<A>, NumberProvider, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.fieldOf("success_action").forGetter(RandomChanceMetaAction::successAction),
			actionCodec.optionalFieldOf("fail_action").forGetter(RandomChanceMetaAction::failAction),
			NumberProvider.CODEC.fieldOf("chance").forGetter(RandomChanceMetaAction::chance)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends RandomChanceMetaAction<A>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, A> actionCodec, Function3<A, Optional<A>, NumberProvider, M> constructor) {
		return PacketCodec.tuple(
			actionCodec, RandomChanceMetaAction::successAction,
			PacketCodecs.optional(actionCodec), RandomChanceMetaAction::failAction,
			NumberProvider.PACKET_CODEC, RandomChanceMetaAction::chance,
			constructor
		);
	}

}
