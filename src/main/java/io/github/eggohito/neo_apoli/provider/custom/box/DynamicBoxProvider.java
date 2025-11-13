package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record DynamicBoxProvider(Vec3dProvider min, Vec3dProvider max) implements BoxProvider {

	public static final MapCodec<DynamicBoxProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("min").forGetter(DynamicBoxProvider::min),
		Vec3dProvider.CODEC.fieldOf("max").forGetter(DynamicBoxProvider::max)
	).apply(instance, DynamicBoxProvider::new));

	public static final PacketCodec<RegistryByteBuf, DynamicBoxProvider> PACKET_CODEC = PacketCodec.tuple(
		Vec3dProvider.PACKET_CODEC, DynamicBoxProvider::min,
		Vec3dProvider.PACKET_CODEC, DynamicBoxProvider::max,
		DynamicBoxProvider::new
	);

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.DYNAMIC;
	}

	@Override
	public @NotNull Box next(Context context) {

		Vec3d min = min().next(context.makeChild(".min"));
		Vec3d max = max().next(context.makeChild(".max"));

		return new Box(min, max);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		BoxProvider.super.validate(reporter);

		min().validate(reporter.makeChild(".min"));
		max().validate(reporter.makeChild(".max"));

	}

}
