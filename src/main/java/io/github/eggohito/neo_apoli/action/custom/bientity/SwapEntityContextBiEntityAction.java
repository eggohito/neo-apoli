package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.dynamic.Codecs;

import java.util.Map;
import java.util.Set;

@EqualsAndHashCode
@Data
public final class SwapEntityContextBiEntityAction extends BiEntityAction {

	public static final MapCodec<SwapEntityContextBiEntityAction> CODEC = MapCodecUtil.lazy(SwapEntityContextBiEntityAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(SwapEntityContextBiEntityAction::biEntityAction),
		Codecs.nonEmptyMap(NeoApoliCodecs.ENTITY_PARAMETER_MAP).fieldOf("parameters").forGetter(SwapEntityContextBiEntityAction::parameters)
	).apply(instance, SwapEntityContextBiEntityAction::new)));

	public static final PacketCodec<RegistryByteBuf, SwapEntityContextBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(SwapEntityContextBiEntityAction.class.getSimpleName(), () -> PacketCodec.tuple(
		BiEntityAction.PACKET_CODEC, SwapEntityContextBiEntityAction::biEntityAction,
		NeoApoliPacketCodecs.ENTITY_PARAMETER_MAP, SwapEntityContextBiEntityAction::parameters,
		SwapEntityContextBiEntityAction::new
	));

	private final BiEntityAction biEntityAction;
	private final Map<EntityParameter, EntityParameter> parameters;

	public SwapEntityContextBiEntityAction(BiEntityAction biEntityAction, Map<EntityParameter, EntityParameter> parameters) {
		this.biEntityAction = biEntityAction;
		this.parameters = parameters;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SWAP_ENTITY_CONTEXT;
	}

	@Override
	protected void impl(Context context) {

		ContextImpl.Builder builder = new ContextImpl.Builder(context);
		parameters().forEach((target, source) -> builder.add(target.getParameter(), context.required(source.getParameter())));

		biEntityAction().execute(builder
			.build(context.getWorld())
			.makeChild(".bientity_action"));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {

		ImmutableSet.Builder<ContextParameter<?>> builder = ImmutableSet.builder();
		parameters().values().forEach(entityParameter -> builder.add(entityParameter.getParameter()));

		return builder.build();

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		biEntityAction().validate(reporter.makeChild(".bientity_action"));
	}

}
