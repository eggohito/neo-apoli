package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.provider.BoxProvider;
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
public final class ConstantBoxProvider extends BoxProvider {

	public static final MapCodec<ConstantBoxProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.VECTOR_3_DOUBLE.fieldOf("min").forGetter(ConstantBoxProvider::min),
		NeoApoliCodecs.VECTOR_3_DOUBLE.fieldOf("max").forGetter(ConstantBoxProvider::max)
	).apply(instance, ConstantBoxProvider::new));

	public static final PacketCodec<RegistryByteBuf, ConstantBoxProvider> PACKET_CODEC = PacketCodec.tuple(
		Vec3d.PACKET_CODEC, ConstantBoxProvider::min,
		Vec3d.PACKET_CODEC, ConstantBoxProvider::max,
		ConstantBoxProvider::new
	);

	private final Vec3d min;
	private final Vec3d max;

	public ConstantBoxProvider(Vec3d min, Vec3d max) {
		this.min = min;
		this.max = max;
	}

	public ConstantBoxProvider(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this(new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ));
	}

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.CONSTANT;
	}

	@Override
	protected Box impl(Context context) {

		Vec3d min = min();
		Vec3d max = max();

		Vec3d pos = context.required(ContextParameters.POSITION);

		return new Box(pos.add(min), pos.add(max));

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

}
