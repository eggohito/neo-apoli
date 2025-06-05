package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.dynamic.Codecs;

import java.util.Map;
import java.util.Set;

public record SwapEntityContextBiEntityAction(BiEntityAction biEntityAction, Map<EntityParameter, EntityParameter> parameters) implements BiEntityAction {

	public static final MapCodec<SwapEntityContextBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliMapCodecs.lazy(() -> BiEntityAction.CODEC.fieldOf("bientity_action")).forGetter(SwapEntityContextBiEntityAction::biEntityAction),
		Codecs.nonEmptyMap(NeoApoliCodecs.ENTITY_PARAMETER_MAP).fieldOf("parameters").forGetter(SwapEntityContextBiEntityAction::parameters)
	).apply(instance, SwapEntityContextBiEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, SwapEntityContextBiEntityAction> PACKET_CODEC = PacketCodec.tuple(
		BiEntityAction.PACKET_CODEC, SwapEntityContextBiEntityAction::biEntityAction,
		NeoApoliPacketCodecs.ENTITY_PARAMETER_MAP, SwapEntityContextBiEntityAction::parameters,
		SwapEntityContextBiEntityAction::new
	);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SWAP_ENTITY_CONTEXT;
	}

	@Override
	public void execute(Context context) {

		Context.Builder builder = new Context.Builder(context);
		parameters().forEach((targetParam, sourceParam) -> builder.add(targetParam.getParameter(), context.required(sourceParam.getParameter())));

		biEntityAction().execute(builder.build(context.getWorld()).makeChild("bientity_action"));

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {

		ImmutableSet.Builder<ContextParameter<?>> builder = ImmutableSet.builder();
		parameters().values().forEach(entityParameter -> builder.add(entityParameter.getParameter()));

		return builder.build();

	}

	@Override
	public void validate(ErrorReporter reporter) {
		BiEntityAction.super.validate(reporter);
		biEntityAction().validate(reporter.makeChild("bientity_action"));
	}

}
