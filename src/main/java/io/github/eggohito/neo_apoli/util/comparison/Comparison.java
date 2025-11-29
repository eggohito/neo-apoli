package io.github.eggohito.neo_apoli.util.comparison;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Comparison extends ContextAware {

	String TYPE_KEY = "type";
	MapCodec<Comparison> MAP_CODEC = ComparisonType.CODEC.dispatchMap(TYPE_KEY, Comparison::type, ComparisonType::mapCodec);

	Codec<Comparison> CODEC = MAP_CODEC.codec();
	StreamCodec<RegistryFriendlyByteBuf, Comparison> STREAM_CODEC = ComparisonType.STREAM_CODEC.dispatch(Comparison::type, ComparisonType::packetCodec);


	ComparisonType<?> type();

	boolean compare(Context context);

}
