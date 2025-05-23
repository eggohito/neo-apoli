package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.dynamic.Codecs;

import java.util.Map;
import java.util.Set;

public record SwapEntityContextBiEntityAction(BiEntityAction biEntityAction, Map<EntityParameter, EntityParameter> parameters) implements BiEntityAction {

	public static final MapCodec<SwapEntityContextBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(SwapEntityContextBiEntityAction::biEntityAction),
		Codecs.nonEmptyMap(new UnboundedMapCodec<>(EntityParameter.CODEC, EntityParameter.CODEC)).fieldOf("parameters").forGetter(SwapEntityContextBiEntityAction::parameters)
	).apply(instance, SwapEntityContextBiEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, SwapEntityContextBiEntityAction> PACKET_CODEC = PacketCodec.tuple(
		BiEntityAction.PACKET_CODEC, SwapEntityContextBiEntityAction::biEntityAction,
		PacketCodecs.map(Object2ObjectOpenHashMap::new, EntityParameter.PACKET_CODEC, EntityParameter.PACKET_CODEC), SwapEntityContextBiEntityAction::parameters,
		SwapEntityContextBiEntityAction::new
	);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SWAP_ENTITY_CONTEXT;
	}

	@Override
	public void execute(Context context) {

		Context.Builder builder = new Context.Builder(context);
		parameters().forEach((targetParam, sourceParam) -> builder.add(targetParam.getParameter(), context.requiredParameter(sourceParam.getParameter())));

		biEntityAction().execute(builder.build(context.getWorld()).makeChild("bientity_action"));

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {

		Set<ContextParameter<?>> allowedParams = new ObjectOpenHashSet<>();
		parameters().forEach((targetParam, sourceParam) -> {
			allowedParams.add(targetParam.getParameter());
			allowedParams.add(sourceParam.getParameter());
		});

		return allowedParams;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		BiEntityAction.super.validate(reporter.makeChild("parameters"));
		biEntityAction().validate(reporter.makeChild("bientity_action"));
	}

}
