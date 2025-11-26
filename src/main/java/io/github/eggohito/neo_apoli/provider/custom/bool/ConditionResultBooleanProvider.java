package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionResultBooleanProvider(Condition condition) implements BooleanProvider {

	public static final MapCodec<ConditionResultBooleanProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.fieldOf("condition").forGetter(ConditionResultBooleanProvider::condition)
	).apply(instance, ConditionResultBooleanProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionResultBooleanProvider> STREAM_CODEC = StreamCodec.composite(
		Condition.STREAM_CODEC, ConditionResultBooleanProvider::condition,
		ConditionResultBooleanProvider::new
	);

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.CONDITION_RESULT;
	}

	@Override
	public @NotNull Boolean next(Context context) {
		return condition().test(context.makeChild(".condition"));
	}

	@Override
	public void validate(ProblemReporter reporter) {
		BooleanProvider.super.validate(reporter);
		condition().validate(reporter.forChild(".condition"));
	}

}
