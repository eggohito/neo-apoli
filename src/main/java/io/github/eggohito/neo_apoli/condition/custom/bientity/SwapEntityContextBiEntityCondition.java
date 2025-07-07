package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
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
public final class SwapEntityContextBiEntityCondition extends BiEntityCondition {

	public static final MapCodec<SwapEntityContextBiEntityCondition> CODEC = NeoApoliMapCodecs.lazy(SwapEntityContextBiEntityCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.fieldOf("bientity_condition").forGetter(SwapEntityContextBiEntityCondition::biEntityCondition),
		Codecs.nonEmptyMap(NeoApoliCodecs.ENTITY_PARAMETER_MAP).fieldOf("parameters").forGetter(SwapEntityContextBiEntityCondition::parameters)
	).apply(instance, SwapEntityContextBiEntityCondition::new)));

	public static final PacketCodec<RegistryByteBuf, SwapEntityContextBiEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SwapEntityContextBiEntityCondition.class.getSimpleName(), () -> PacketCodec.tuple(
		BiEntityCondition.PACKET_CODEC, SwapEntityContextBiEntityCondition::biEntityCondition,
		NeoApoliPacketCodecs.ENTITY_PARAMETER_MAP, SwapEntityContextBiEntityCondition::parameters,
		SwapEntityContextBiEntityCondition::new
	));

	private final BiEntityCondition biEntityCondition;
	private final Map<EntityParameter, EntityParameter> parameters;

	public SwapEntityContextBiEntityCondition(BiEntityCondition biEntityCondition, Map<EntityParameter, EntityParameter> parameters) {
		this.biEntityCondition = biEntityCondition;
		this.parameters = parameters;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.SWAP_ENTITY_CONTEXT;
	}

	@Override
	protected boolean impl(Context context) {

		Context.Builder builder = new Context.Builder(context);
		parameters().forEach((target, source) -> builder.add(target.getParameter(), context.required(source.getParameter())));

		return biEntityCondition().test(builder.build(context.getWorld()).makeChild(".bientity_condition"));

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {

		ImmutableSet.Builder<ContextParameter<?>> builder = ImmutableSet.builder();
		parameters().values().forEach(source -> builder.add(source.getParameter()));

		return builder.build();

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		biEntityCondition().validate(reporter.makeChild(".bientity_condition"));
	}

}
