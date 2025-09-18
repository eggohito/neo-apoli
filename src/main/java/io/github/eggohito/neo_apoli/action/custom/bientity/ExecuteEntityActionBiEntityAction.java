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
import io.github.eggohito.neo_apoli.util.context.ContextTypeUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class ExecuteEntityActionBiEntityAction extends BiEntityAction {

	public static final MapCodec<ExecuteEntityActionBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityAction.CODEC.fieldOf("entity_action").forGetter(ExecuteEntityActionBiEntityAction::entityAction),
		EntityParameter.CODEC.fieldOf("entity").forGetter(ExecuteEntityActionBiEntityAction::entity)
	).apply(instance, ExecuteEntityActionBiEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, ExecuteEntityActionBiEntityAction> PACKET_CODEC = PacketCodec.tuple(
		EntityAction.PACKET_CODEC, ExecuteEntityActionBiEntityAction::entityAction,
		EntityParameter.PACKET_CODEC, ExecuteEntityActionBiEntityAction::entity,
		ExecuteEntityActionBiEntityAction::new
	);

	private final EntityAction entityAction;
	private final EntityParameter entity;

	public ExecuteEntityActionBiEntityAction(EntityAction entityAction, EntityParameter entity) {
		this.entityAction = entityAction;
		this.entity = entity;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.EXECUTE_ENTITY_ACTION;
	}

	@Override
	protected void impl(Context context) {

		Entity entity = context.required(entity().getParameter());
		Context entityContext = context.copy(builder -> builder
			.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.ENTITY))
			.add(ContextParameters.ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, entity.getPos()));

		entityAction().execute(entityContext);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(this.entity().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		entityAction().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.ENTITY))
			.makeChild(".entity_action"));
	}

}
