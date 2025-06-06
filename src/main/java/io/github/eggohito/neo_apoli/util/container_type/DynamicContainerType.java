package io.github.eggohito.neo_apoli.util.container_type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.TextAlignment;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

//	TODO: Finish the implementation of dynamic containers -eggohito
public record DynamicContainerType(TextAlignment textAlignment, Identifier texture, int columns, int rows) implements ContainerType {

	public static final MapCodec<DynamicContainerType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TextAlignment.CODEC.optionalFieldOf("title_alignment", TextAlignment.CENTER).forGetter(DynamicContainerType::textAlignment),
		Identifier.CODEC.fieldOf("texture").forGetter(DynamicContainerType::texture),
		Codecs.rangedInt(1, Integer.MAX_VALUE).fieldOf("columns").forGetter(DynamicContainerType::columns),
		Codecs.rangedInt(1, Integer.MAX_VALUE).fieldOf("rows").forGetter(DynamicContainerType::rows)
	).apply(instance, DynamicContainerType::new));

	public static final PacketCodec<ByteBuf, DynamicContainerType> PACKET_CODEC = PacketCodec.tuple(
		TextAlignment.PACKET_CODEC, DynamicContainerType::textAlignment,
		Identifier.PACKET_CODEC, DynamicContainerType::texture,
		PacketCodecs.INTEGER, DynamicContainerType::columns,
		PacketCodecs.INTEGER, DynamicContainerType::rows,
		DynamicContainerType::new
	);

	public DynamicContainerType {
		throw new UnsupportedOperationException("Dynamic container types are currently not supported!");
	}

	@Override
	public ScreenHandlerFactory create(Inventory inventory) {
		throw new UnsupportedOperationException("Dynamic container types are currently not supported!");
	}

}
