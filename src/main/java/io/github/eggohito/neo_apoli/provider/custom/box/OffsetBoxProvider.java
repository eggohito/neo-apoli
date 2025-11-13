package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;

public record OffsetBoxProvider(BoxProvider box, BoxProvider offset) implements BoxProvider {

	public static final MapCodec<OffsetBoxProvider> CODEC = MapCodecUtil.lazy(OffsetBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BoxProvider.CODEC.fieldOf("box").forGetter(OffsetBoxProvider::box),
		BoxProvider.CODEC.fieldOf("offset").forGetter(OffsetBoxProvider::offset)
	).apply(instance, OffsetBoxProvider::new)));

	public static final PacketCodec<RegistryByteBuf, OffsetBoxProvider> PACKET_CODEC = PacketCodecUtil.lazy(OffsetBoxProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		BoxProvider.PACKET_CODEC, OffsetBoxProvider::box,
		BoxProvider.PACKET_CODEC, OffsetBoxProvider::offset,
		OffsetBoxProvider::new
	));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.OFFSET;
	}

	@Override
	public @NotNull Box next(Context context) {

		Box box = box().next(context.makeChild(".box"));
		Box offset = offset().next(context.makeChild(".offset"));

		return new Box(
			box.minX + offset.minX,
			box.minY + offset.minY,
			box.minZ + offset.minZ,
			box.maxX + offset.maxX,
			box.maxY + offset.maxY,
			box.maxZ + offset.maxZ
		);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		BoxProvider.super.validate(reporter);

		box().validate(reporter.makeChild(".box"));
		offset().validate(reporter.makeChild(".offset"));

	}

}
