package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record AbsoluteNumberProvider(NumberProvider number) implements NumberProvider {

	public static final MapCodec<AbsoluteNumberProvider> CODEC = MapCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(AbsoluteNumberProvider::number)
	).apply(instance, AbsoluteNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, AbsoluteNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> NumberProvider.STREAM_CODEC.map(
		AbsoluteNumberProvider::new,
		AbsoluteNumberProvider::number
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ABSOLUTE;
	}

	@Override
	public @NotNull Number next(Context context) {
		return Math.abs(this.number().nextDouble(context.makeChild(".number")));
	}

	@Override
	public long nextLong(Context context) {
		return Math.abs(this.number().nextLong(context.makeChild(".number")));
	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		number().validate(reporter.forChild(".number"));
	}

}
