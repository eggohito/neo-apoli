package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SwapBiEntityCondition(BiEntityCondition condition) implements BiEntityCondition {

	public static final MapCodec<SwapBiEntityCondition> CODEC = MapCodecUtil.lazy(SwapBiEntityCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.fieldOf("condition").forGetter(SwapBiEntityCondition::condition)
	).apply(instance, SwapBiEntityCondition::new)));

	public static final PacketCodec<RegistryByteBuf, SwapBiEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(SwapBiEntityCondition.class.getSimpleName(), () -> PacketCodec.tuple(
		BiEntityCondition.PACKET_CODEC, SwapBiEntityCondition::condition,
		SwapBiEntityCondition::new
	));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.SWAP;
	}

	@Override
	public boolean test(Context context) {

		Context conditionContext = ContextImpl.of(context, builder -> builder
			.addNullable(ContextParameters.ACTOR, context.nullable(ContextParameters.TARGET))
			.addNullable(ContextParameters.TARGET, context.nullable(ContextParameters.ACTOR)));

		return condition().test(conditionContext.makeChild(".condition"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		condition().validate(reporter.makeChild(".condition"));
	}

}
