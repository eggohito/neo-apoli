package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliVec3ProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
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
	public @NotNull Vec3Provider.Type<?> getType() {
		return NeoApoliVec3ProviderTypes.OFFSET;
	}

	@Override
	public @NotNull Vec3 getVec3(Context context) {

		Vec3 vector = vector().getVec3(context.forChild(".vector"));
		Vec3 offset = offset().getVec3(context.forChild(".offset"));

		return vector.add(offset);

	}

	@Override
	public void validate(Context.Validator validator) {

		Vec3Provider.super.validate(validator);

		vector().validate(validator.forChild(".vector"));
		offset().validate(validator.forChild(".offset"));

	}

}
