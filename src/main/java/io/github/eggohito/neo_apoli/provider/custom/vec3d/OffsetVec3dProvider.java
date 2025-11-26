package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record OffsetVec3dProvider(Vec3dProvider vector, Vec3dProvider offset) implements Vec3dProvider {

	public static final MapCodec<OffsetVec3dProvider> CODEC = MapCodecUtil.lazy(OffsetVec3dProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("vector").forGetter(OffsetVec3dProvider::vector),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetVec3dProvider::offset)
	).apply(instance, OffsetVec3dProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetVec3dProvider> STREAM_CODEC = StreamCodecUtil.lazy(OffsetVec3dProvider.class.getSimpleName(), () -> StreamCodec.composite(
		Vec3dProvider.STREAM_CODEC, OffsetVec3dProvider::vector,
		Vec3dProvider.STREAM_CODEC, OffsetVec3dProvider::offset,
		OffsetVec3dProvider::new
	));

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.OFFSET;
	}

	@Override
	public @NotNull Vec3 next(Context context) {

		Vec3 vector = vector().next(context.makeChild(".vector"));
		Vec3 offset = offset().next(context.makeChild(".offset"));

		return vector.add(offset);

	}

	@Override
	public void validate(ProblemReporter reporter) {

		Vec3dProvider.super.validate(reporter);

		vector().validate(reporter.forChild(".vector"));
		offset().validate(reporter.forChild(".offset"));

	}

}
