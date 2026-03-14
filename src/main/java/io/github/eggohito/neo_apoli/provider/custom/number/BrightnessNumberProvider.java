package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BrightnessNumberProvider(Vec3Provider position) implements NumberProvider {

	public static final MapCodec<BrightnessNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Vec3Provider.CODEC.fieldOf("position").forGetter(BrightnessNumberProvider::position))
		.apply(instance, BrightnessNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BrightnessNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, BrightnessNumberProvider::position,
		BrightnessNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.BRIGHTNESS;
	}

	@Override
	public double nextDouble(Context context) {

		Context positionContext = context.forChild(".position");
		Vec3 position = position().nextVec3(positionContext);

		if (positionContext.hasErrors()) {
			return 0.0D;
		}

		else {
			return context.level().getLightLevelDependentMagicValue(BlockPos.containing(position));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		position().validate(validator.forChild(".position"));
	}

}
