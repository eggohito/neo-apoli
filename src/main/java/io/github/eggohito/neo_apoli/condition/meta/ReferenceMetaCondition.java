package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public interface ReferenceMetaCondition<C extends Condition<T>, T extends ConditionType<?>> extends Condition<T> {

	@Override
	ConditionCategory<C> getCategory();

	@Override
	default boolean test(Context context) {

		Identifier value = this.value();
		ConditionCategory<C> category = this.getCategory();

		return ConditionManager.getAsResult(category, value).mapOrElse(
			condition -> {

				if (context.markActive(condition)) {

					try {
						return condition.test(context);
					}

					finally {
						context.markInactive(condition);
					}

				}

				else {
					context.getReporter().report(category + " \"" + value + "\" was recursively referenced!");
					return false;
				}

			},
			error -> {
				context.getReporter().report(error.message());
				return false;
			}
		);

	}

	@Override
	default void validate(ErrorReporter reporter) {

		Identifier value = this.value();
		ConditionCategory<C> category = this.getCategory();

		if (reporter.isInStack(value)) {
			reporter.report(category + " \"" + value + "\" was recursively referenced!");
		}

		else {
			ConditionManager.getAsResult(category, value)
				.ifSuccess(condition -> condition.validate(reporter.makeChild("{\"" + value + "\"}", value)))
				.ifError(error -> reporter.report(error.message()));
		}

	}

	Identifier value();

	static <M extends ReferenceMetaCondition<?, ?>> MapCodec<M> codec(Function<Identifier, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("value").forGetter(ReferenceMetaCondition::value)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, M extends ReferenceMetaCondition<?, ?>> PacketCodec<B, M> packetCodec(Function<Identifier, M> constructor) {
		return PacketCodec.tuple(
			Identifier.PACKET_CODEC, ReferenceMetaCondition::value,
			constructor
		);
	}

}
