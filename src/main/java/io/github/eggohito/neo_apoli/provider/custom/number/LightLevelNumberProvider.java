package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record LightLevelNumberProvider(Optional<LightType> lightType, Vec3dProvider position) implements NumberProvider {

	public static final MapCodec<LightLevelNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.LIGHT_TYPE.optionalFieldOf("light_type").forGetter(LightLevelNumberProvider::lightType),
		Vec3dProvider.CODEC.fieldOf("position").forGetter(LightLevelNumberProvider::position)
	).apply(instance, LightLevelNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, LightLevelNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(NeoApoliPacketCodecs.LIGHT_TYPE), LightLevelNumberProvider::lightType,
		Vec3dProvider.PACKET_CODEC, LightLevelNumberProvider::position,
		LightLevelNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.LIGHT_LEVEL;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context positionContext = context.makeChild(".position");
		Vec3d position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return 0;
		}

		World world = context.getWorld();
		BlockPos blockPos = BlockPos.ofFloored(position);

		return this.lightType()
			.map(lightType -> world.getLightLevel(lightType, blockPos))
			.orElseGet(() -> world.getLightLevel(blockPos));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		position().validate(reporter.makeChild(".position"));
	}

}
