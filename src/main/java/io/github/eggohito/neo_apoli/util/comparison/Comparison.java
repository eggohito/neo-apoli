package io.github.eggohito.neo_apoli.util.comparison;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface Comparison extends ContextAware {

	String TYPE_KEY = "type";
	MapCodec<Comparison> MAP_CODEC = ComparisonTypes.CODEC.dispatchMap(TYPE_KEY, Comparison::type, ComparisonType::mapCodec);

	Codec<Comparison> CODEC = MAP_CODEC.codec();
	PacketCodec<RegistryByteBuf, Comparison> PACKET_CODEC = ComparisonTypes.PACKET_CODEC.dispatch(Comparison::type, ComparisonType::packetCodec);


	ComparisonType<?> type();

	Comparator comparator();


	boolean compare(Context context);


	static <C extends Comparison> Products.P1<RecordCodecBuilder.Mu<C>, Comparator> addComparatorField(RecordCodecBuilder.Instance<C> instance) {
		return instance.group(
			Comparator.CODEC.fieldOf("comparator").forGetter(Comparison::comparator)
		);
	}

	static <B extends ByteBuf, C extends Comparison> PacketCodec<B, C> createPacketCodec(BiConsumer<B, C> encoder, BiFunction<B, Comparator, C> decoder) {
		return PacketCodec.ofStatic(
			(buf, value) -> {
				Comparator.PACKET_CODEC.encode(buf, value.comparator());
				encoder.accept(buf, value);
			},
			buf -> {
				Comparator comparator = Comparator.PACKET_CODEC.decode(buf);
				return decoder.apply(buf, comparator);
			}
		);
	}

}
