package io.github.eggohito.neo_apoli.util.container_type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import io.github.eggohito.neo_apoli.util.TextAlignment;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;

//	TODO: Finish the implementation of dynamic containers -eggohito
public record DynamicContainerType(TextAlignment textAlignment, ResourceLocation texture, int columns, int rows) implements ContainerType {

	public static final MapCodec<DynamicContainerType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TextAlignment.CODEC.optionalFieldOf("title_alignment", TextAlignment.CENTER).forGetter(DynamicContainerType::textAlignment),
		DynamicResourceLocation.CODEC.fieldOf("texture").forGetter(DynamicContainerType::texture),
		ExtraCodecs.intRange(1, Integer.MAX_VALUE).fieldOf("columns").forGetter(DynamicContainerType::columns),
		ExtraCodecs.intRange(1, Integer.MAX_VALUE).fieldOf("rows").forGetter(DynamicContainerType::rows)
	).apply(instance, DynamicContainerType::new));

	public static final StreamCodec<ByteBuf, DynamicContainerType> STREAM_CODEC = StreamCodec.composite(
		TextAlignment.STREAM_CODEC, DynamicContainerType::textAlignment,
		ResourceLocation.STREAM_CODEC, DynamicContainerType::texture,
		ByteBufCodecs.INT, DynamicContainerType::columns,
		ByteBufCodecs.INT, DynamicContainerType::rows,
		DynamicContainerType::new
	);

	public DynamicContainerType {
		throw new UnsupportedOperationException("Dynamic container types are currently not supported!");
	}

	@Override
	public MenuConstructor create(Container inventory) {
		throw new UnsupportedOperationException("Dynamic container types are currently not supported!");
	}

}
