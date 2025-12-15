package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
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
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.LIGHT_LEVEL;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context positionContext = context.forChild(".position");
		Vec3 position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return 0;
		}

		Level world = context.getLevel();
		BlockPos blockPos = BlockPos.containing(position);

		return this.lightType()
			.map(lightType -> world.getBrightness(lightType, blockPos))
			.orElseGet(() -> world.getMaxLocalRawBrightness(blockPos));

	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		position().validate(reporter.forChild(".position"));
	}

}
