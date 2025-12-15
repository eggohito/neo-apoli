package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SwapBiEntityAction(BiEntityAction biEntityAction) implements BiEntityAction {

	public static final MapCodec<SwapBiEntityAction> CODEC = MapCodecUtil.lazy(SwapBiEntityAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(SwapBiEntityAction::biEntityAction)
	).apply(instance, SwapBiEntityAction::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, SwapBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(SwapBiEntityAction.class.getSimpleName(), () -> StreamCodec.composite(
		BiEntityAction.STREAM_CODEC, SwapBiEntityAction::biEntityAction,
		SwapBiEntityAction::new
	));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SWAP;
	}

	@Override
	public void execute(Context context) {

		Context actionContext = new Context.Builder(context)
			.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, context.nullable(NeoApoliContextKeys.TARGET_ENTITY))
			.addNullable(NeoApoliContextKeys.TARGET_ENTITY, context.nullable(NeoApoliContextKeys.ACTOR_ENTITY))
			.build(context.getLevel());

		biEntityAction().execute(actionContext.forChild(".bientity_action"));

	}

	@Override
	public void validate(ProblemReporter reporter) {
		biEntityAction().validate(reporter.forChild(".bientity_action"));
	}

}
