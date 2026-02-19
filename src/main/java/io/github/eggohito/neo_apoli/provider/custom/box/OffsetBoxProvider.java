package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record OffsetBoxProvider(BoxProvider box, BoxProvider offset) implements BoxProvider {

	public static final MapCodec<OffsetBoxProvider> MAP_CODEC = MapCodecUtil.lazy(OffsetBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BoxProvider.CODEC.fieldOf("box").forGetter(OffsetBoxProvider::box),
		BoxProvider.CODEC.fieldOf("offset").forGetter(OffsetBoxProvider::offset)
	).apply(instance, OffsetBoxProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(OffsetBoxProvider.class.getSimpleName(), () -> StreamCodec.composite(
		BoxProvider.STREAM_CODEC, OffsetBoxProvider::box,
		BoxProvider.STREAM_CODEC, OffsetBoxProvider::offset,
		OffsetBoxProvider::new
	));

	@Override
	public @NotNull BoxProviderType<?> getType() {
		return BoxProviderTypes.OFFSET;
	}

	@Override
	public @NotNull AABB nextBox(Context context) {

		AABB box = box().nextBox(context.forChild(".box"));
		AABB offset = offset().nextBox(context.forChild(".offset"));

		return new AABB(
			box.minX + offset.minX,
			box.minY + offset.minY,
			box.minZ + offset.minZ,
			box.maxX + offset.maxX,
			box.maxY + offset.maxY,
			box.maxZ + offset.maxZ
		);

	}

	@Override
	public void validate(Context.Validator validator) {

		BoxProvider.super.validate(validator);

		box().validate(validator.forChild(".box"));
		offset().validate(validator.forChild(".offset"));

	}

}
