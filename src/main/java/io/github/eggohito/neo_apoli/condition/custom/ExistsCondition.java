package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ExistsCondition(Context.Parameter<?> parameter) implements Condition {

	public static final MapCodec<ExistsCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliContextParams.CODEC.fieldOf("parameter").forGetter(ExistsCondition::parameter))
		.apply(instance, ExistsCondition::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ExistsCondition> STREAM_CODEC = StreamCodec.composite(
		NeoApoliContextParams.STREAM_CODEC, ExistsCondition::parameter,
		ExistsCondition::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.EXISTS;
	}

	@Override
	public boolean test(Context context) {
		return context.hasParameter(parameter());
	}

}
