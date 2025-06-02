package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record ExecuteEntityActionBiEntityAction(EntityAction entityAction, EntityParameter entity) implements BiEntityAction {

	public static final MapCodec<ExecuteEntityActionBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityAction.CODEC.fieldOf("entity_action").forGetter(ExecuteEntityActionBiEntityAction::entityAction),
		EntityParameter.CODEC.fieldOf("entity").forGetter(ExecuteEntityActionBiEntityAction::entity)
	).apply(instance, ExecuteEntityActionBiEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, ExecuteEntityActionBiEntityAction> PACKET_CODEC = PacketCodec.tuple(
		EntityAction.PACKET_CODEC, ExecuteEntityActionBiEntityAction::entityAction,
		EntityParameter.PACKET_CODEC, ExecuteEntityActionBiEntityAction::entity,
		ExecuteEntityActionBiEntityAction::new
	);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.EXECUTE_ENTITY_ACTION;
	}

	@Override
	public void execute(Context context) {

		Context entityActionContext = context
			.copy(builder -> builder.add(ContextParameters.THIS_ENTITY, context.required(this.entity().getParameter())))
			.makeChild("entity_action");

		entityAction().execute(entityActionContext);

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(this.entity().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		BiEntityAction.super.validate(reporter);
		entityAction().validate(reporter.makeChild("entity_action"));
	}

}
