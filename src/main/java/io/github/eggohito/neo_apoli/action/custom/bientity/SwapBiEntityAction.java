package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SwapBiEntityAction(BiEntityAction biEntityAction) implements BiEntityAction {

	public static final MapCodec<SwapBiEntityAction> CODEC = MapCodecUtil.lazy(SwapBiEntityAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(SwapBiEntityAction::biEntityAction)
	).apply(instance, SwapBiEntityAction::new)));

	public static final PacketCodec<RegistryByteBuf, SwapBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(SwapBiEntityAction.class.getSimpleName(), () -> PacketCodec.tuple(
		BiEntityAction.PACKET_CODEC, SwapBiEntityAction::biEntityAction,
		SwapBiEntityAction::new
	));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SWAP;
	}

	@Override
	public void execute(Context context) {

		Context actionContext = ContextImpl.of(context, builder -> builder
			.addNullable(ContextParameters.ACTOR, context.nullable(ContextParameters.TARGET))
			.addNullable(ContextParameters.TARGET, context.nullable(ContextParameters.ACTOR)));

		biEntityAction().execute(actionContext.makeChild(".bientity_action"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		biEntityAction().validate(reporter.makeChild(".bientity_action"));
	}

}
