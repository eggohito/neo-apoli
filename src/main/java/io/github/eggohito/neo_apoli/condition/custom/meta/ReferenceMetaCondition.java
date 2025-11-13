package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public interface ReferenceMetaCondition<C extends Condition> extends MetaCondition { ;

	Identifier value();

	Pair<Class<C>, String> classAndName();

	@Override
	default boolean test(Context context) {
		return ConditionManager.getAsResult(this.value())
			.flatMap(this::checkAndCast)
			.mapOrElse(
				condition -> {

					try {

						if (context.markActive(condition)) {
							return condition.test(context.makeChild("{" + this.value() + "}", this.value()));
						}

						else {
							context.getReporter().makeChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was tested recursively!");
						}

					}

					finally {
						context.markInActive(condition);
					}

					return false;

				},
				error -> false
			);

	}

	@Override
	default void validate(ErrorReporter reporter) {

		if (reporter.isInStack(this.value())) {
			reporter.makeChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was referenced recursively!");
		}

		else {
			ConditionManager.getAsResult(this.value())
				.flatMap(this::checkAndCast)
				.ifSuccess(condition -> condition.validate(reporter.makeChild("{" + this.value() + "}", this.value())))
				.ifError(error -> reporter.makeChild(".value").report(error.message()));
		}

	}

	default DataResult<C> checkAndCast(Condition condition) {

		Class<C> clazz = this.classAndName().getFirst();
		String name = this.classAndName().getSecond();

		if (clazz.isInstance(condition)) {
			return DataResult.success(clazz.cast(condition));
		}

		else {
			return DataResult.error(() -> name + " with ID \"" + this.value() + "\" doesn't exist!");
		}

	}

	static <C extends Condition, M extends ReferenceMetaCondition<C>> MapCodec<M> codec(Function<Identifier, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("value").forGetter(ReferenceMetaCondition::value)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends ReferenceMetaCondition<C>> PacketCodec<RegistryByteBuf, M> packetCodec(Function<Identifier, M> constructor) {
		return PacketCodec.tuple(
			Identifier.PACKET_CODEC, ReferenceMetaCondition::value,
			constructor
		);
	}

}
