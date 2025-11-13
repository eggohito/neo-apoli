package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ConstantBoxProvider(Vec3d min, Vec3d max) implements BoxProvider {

	public static final MapCodec<ConstantBoxProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.VECTOR_3_DOUBLE.fieldOf("min").forGetter(ConstantBoxProvider::min),
		NeoApoliCodecs.VECTOR_3_DOUBLE.fieldOf("max").forGetter(ConstantBoxProvider::max)
	).apply(instance, ConstantBoxProvider::new));

	public static final Codec<ConstantBoxProvider> INLINE_CODEC = Codec.DOUBLE.listOf().comapFlatMap(
		doubles -> Util.decodeFixedLengthList(doubles, 6).map(values -> new ConstantBoxProvider(values.getFirst(), values.get(1), values.get(2), values.get(3), values.get(4), values.getLast())),
		constant -> List.of(constant.min().getX(), constant.min().getY(), constant.min().getZ(), constant.max().getX(), constant.max().getY(), constant.max().getZ())
	);

	public static final PacketCodec<RegistryByteBuf, ConstantBoxProvider> PACKET_CODEC = PacketCodec.tuple(
		Vec3d.PACKET_CODEC, ConstantBoxProvider::min,
		Vec3d.PACKET_CODEC, ConstantBoxProvider::max,
		ConstantBoxProvider::new
	);

	public ConstantBoxProvider(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this(new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ));
	}

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull Box next(Context context) {
		return new Box(min(), max());
	}

}
