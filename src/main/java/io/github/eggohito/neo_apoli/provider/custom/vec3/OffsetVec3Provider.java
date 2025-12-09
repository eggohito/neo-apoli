package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record OffsetVec3Provider(Vec3Provider vector, Vec3Provider offset) implements Vec3Provider {

	public static final MapCodec<OffsetVec3Provider> CODEC = MapCodecUtil.lazy(OffsetVec3Provider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("vector").forGetter(OffsetVec3Provider::vector),
		Vec3Provider.CODEC.fieldOf("offset").forGetter(OffsetVec3Provider::offset)
	).apply(instance, OffsetVec3Provider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetVec3Provider> STREAM_CODEC = StreamCodecUtil.lazy(OffsetVec3Provider.class.getSimpleName(), () -> StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, OffsetVec3Provider::vector,
		Vec3Provider.STREAM_CODEC, OffsetVec3Provider::offset,
		OffsetVec3Provider::new
	));

	@Override
	public Vec3ProviderType<?> getType() {
		return Vec3ProviderTypes.OFFSET;
	}

	@Override
	public @NotNull Vec3 next(Context context) {

		Vec3 vector = vector().next(context.makeChild(".vector"));
		Vec3 offset = offset().next(context.makeChild(".offset"));

		return vector.add(offset);

	}

	@Override
	public void validate(ProblemReporter reporter) {

		Vec3Provider.super.validate(reporter);

		vector().validate(reporter.forChild(".vector"));
		offset().validate(reporter.forChild(".offset"));

	}

}
