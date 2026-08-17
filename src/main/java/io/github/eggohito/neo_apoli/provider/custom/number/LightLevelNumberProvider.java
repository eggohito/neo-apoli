package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record LightLevelNumberProvider(Optional<LightLayer> lightType, Vec3Provider position) implements NumberProvider {

	public static final MapCodec<LightLevelNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.LIGHT_TYPE.optionalFieldOf("light_type").forGetter(LightLevelNumberProvider::lightType),
		Vec3Provider.CODEC.fieldOf("position").forGetter(LightLevelNumberProvider::position)
	).apply(instance, LightLevelNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, LightLevelNumberProvider> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(NeoApoliStreamCodecs.LIGHT_TYPE), LightLevelNumberProvider::lightType,
		Vec3Provider.STREAM_CODEC, LightLevelNumberProvider::position,
		LightLevelNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.LIGHT_LEVEL;
	}

	@Override
	public double getDouble(Context context) {
		Level level = context.level();
		return this.position().getVec3(context.forChild(".position"))
			.map(BlockPos::containing)
			.map(position -> this.lightType()
				.map(lightType -> level.getBrightness(lightType, position))
				.orElseGet(() -> level.getMaxLocalRawBrightness(position))).orElse(0);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		position().validate(validator.forChild(".position"));
	}

}
