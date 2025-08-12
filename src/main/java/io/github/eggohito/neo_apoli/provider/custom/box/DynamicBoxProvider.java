package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.BoxProvider;
import io.github.eggohito.neo_apoli.provider.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class DynamicBoxProvider extends BoxProvider {

	public static final MapCodec<DynamicBoxProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("min").forGetter(DynamicBoxProvider::min),
		Vec3dProvider.CODEC.fieldOf("max").forGetter(DynamicBoxProvider::max)
	).apply(instance, DynamicBoxProvider::new));

	public static final PacketCodec<RegistryByteBuf, DynamicBoxProvider> PACKET_CODEC = PacketCodec.tuple(
		Vec3dProvider.PACKET_CODEC, DynamicBoxProvider::min,
		Vec3dProvider.PACKET_CODEC, DynamicBoxProvider::max,
		DynamicBoxProvider::new
	);

	private final Vec3dProvider min;
	private final Vec3dProvider max;

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.DYNAMIC;
	}

	@Override
	protected Box impl(Context context) {

		Vec3d min = min().next(context.makeChild(".min"));
		Vec3d max = max().next(context.makeChild(".max"));

		Vec3d pos = context.required(ContextParameters.POSITION);

		return new Box(pos.add(min), pos.add(max));

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		min().validate(reporter.makeChild(".min"));
		max().validate(reporter.makeChild(".max"));

	}

}
