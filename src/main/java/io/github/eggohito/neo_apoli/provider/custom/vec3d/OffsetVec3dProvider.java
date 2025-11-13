package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record OffsetVec3dProvider(Vec3dProvider vector, Vec3dProvider offset) implements Vec3dProvider {

	public static final MapCodec<OffsetVec3dProvider> CODEC = MapCodecUtil.lazy(OffsetVec3dProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("vector").forGetter(OffsetVec3dProvider::vector),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetVec3dProvider::offset)
	).apply(instance, OffsetVec3dProvider::new)));

	public static final PacketCodec<RegistryByteBuf, OffsetVec3dProvider> PACKET_CODEC = PacketCodecUtil.lazy(OffsetVec3dProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		Vec3dProvider.PACKET_CODEC, OffsetVec3dProvider::vector,
		Vec3dProvider.PACKET_CODEC, OffsetVec3dProvider::offset,
		OffsetVec3dProvider::new
	));

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.OFFSET;
	}

	@Override
	public @NotNull Vec3d next(Context context) {

		Vec3d vector = vector().next(context.makeChild(".vector"));
		Vec3d offset = offset().next(context.makeChild(".offset"));

		return vector.add(offset);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		Vec3dProvider.super.validate(reporter);

		vector().validate(reporter.makeChild(".vector"));
		offset().validate(reporter.makeChild(".offset"));

	}

}
