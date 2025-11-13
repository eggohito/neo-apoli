package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public record AbsoluteNumberProvider(NumberProvider number) implements NumberProvider {

	public static final MapCodec<AbsoluteNumberProvider> CODEC = MapCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(AbsoluteNumberProvider::number)
	).apply(instance, AbsoluteNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, AbsoluteNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> NumberProvider.PACKET_CODEC.xmap(
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
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		number().validate(reporter.makeChild(".number"));
	}

}
