package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SwapBiEntityCondition(BiEntityCondition condition) implements BiEntityCondition {

	public static final MapCodec<SwapBiEntityCondition> CODEC = MapCodecUtil.lazy(SwapBiEntityCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.fieldOf("condition").forGetter(SwapBiEntityCondition::condition)
	).apply(instance, SwapBiEntityCondition::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, SwapBiEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(SwapBiEntityCondition.class.getSimpleName(), () -> StreamCodec.composite(
		BiEntityCondition.STREAM_CODEC, SwapBiEntityCondition::condition,
		SwapBiEntityCondition::new
	));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.SWAP;
	}

	@Override
	public boolean test(Context context) {

		Context conditionContext = new Context.Builder(context)
			.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, context.nullable(NeoApoliContextKeys.TARGET_ENTITY))
			.addNullable(NeoApoliContextKeys.TARGET_ENTITY, context.nullable(NeoApoliContextKeys.ACTOR_ENTITY))
			.build(context.getLevel());

		return condition().test(conditionContext.forChild(".condition"));

	}

	@Override
	public void validate(Context.Validator validator) {
		condition().validate(validator.forChild(".condition"));
	}

}
