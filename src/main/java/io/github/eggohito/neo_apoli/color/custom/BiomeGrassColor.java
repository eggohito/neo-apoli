package io.github.eggohito.neo_apoli.color.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.Color;
import io.github.eggohito.neo_apoli.color.DynamicColor;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliColorResolvers;
import io.github.eggohito.neo_apoli.registry.NeoApoliColorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;

public record BiomeGrassColor(Vec3Provider position, NumberProvider alpha) implements Color {

	public static final MapCodec<BiomeGrassColor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("position").forGetter(BiomeGrassColor::position),
		NumberProvider.clamped(0.0, 1.0).fieldOf("alpha").forGetter(BiomeGrassColor::alpha)
	).apply(instance, BiomeGrassColor::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BiomeGrassColor> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, BiomeGrassColor::position,
		NumberProvider.STREAM_CODEC, BiomeGrassColor::alpha,
		BiomeGrassColor::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliColorTypes.BIOME_GRASS;
	}

	@Override
	public int intValue(Context context) {

		Context positionContext = context.forChild(".position");
		BlockPos position = BlockPos.containing(position().getVec3(positionContext));

		if (positionContext.hasErrors()) {
			return 0;
		}

		float alphaFloat = DynamicColor.getValue(context.forChild(".alpha"), alpha()::getFloat, () -> 1.0F);
		int blockTint = context.level().getBlockTint(position, NeoApoliColorResolvers.BIOME_GRASS_COLOR);

		return ARGB.color(ARGB.as8BitChannel(alphaFloat), blockTint);

	}

	@Override
	public void validate(Context.Validator validator) {
		Color.super.validate(validator);
		position().validate(validator.forChild(".position"));
		alpha().validate(validator.forChild(".alpha"));
	}

}
