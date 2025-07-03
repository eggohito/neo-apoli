package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;

@EqualsAndHashCode
@Data
public final class LightLevelNumberProvider extends NumberProvider {

	public static final MapCodec<LightLevelNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.LIGHT_TYPE.optionalFieldOf("light_type").forGetter(LightLevelNumberProvider::lightType)
	).apply(instance, LightLevelNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, LightLevelNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(NeoApoliPacketCodecs.LIGHT_TYPE), LightLevelNumberProvider::lightType,
		LightLevelNumberProvider::new
	);

	private final Optional<LightType> lightType;

	public LightLevelNumberProvider(Optional<LightType> lightType) {
		this.lightType = lightType;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.LIGHT_LEVEL;
	}

	@Override
	protected Number impl(Context context) {

		World world = context.getWorld();
		BlockPos pos = BlockPos.ofFloored(context.required(ContextParameters.POSITION));

		return lightType()
			.map(type -> world.getLightLevel(type, pos))
			.orElseGet(() -> world.getLightLevel(pos));

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

}
