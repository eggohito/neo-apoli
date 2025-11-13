package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public interface ExecuteOnEntityMetaAction extends MetaAction {

	EntityAction action();

	EntityTarget entity();

	@Override
	default void execute(Context context) {

		Optional<Entity> entity = context.optional(entity().getParameter());
		Context entityContext = ContextImpl.of(context, builder -> builder
			.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.ENTITY))
			.addOptional(ContextParameters.THIS_ENTITY, entity)
			.addOptional(ContextParameters.ENTITY_POS, entity.map(Entity::getPos)));

		action().execute(entityContext.makeChild(".action"));

	}

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

	@Override
	default void validate(ErrorReporter reporter) {
		MetaAction.super.validate(reporter);
		action().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.ENTITY))
			.makeChild(".action"));
	}

	static <M extends ExecuteOnEntityMetaAction> MapCodec<M> codec(BiFunction<EntityAction, EntityTarget, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityAction.CODEC.fieldOf("action").forGetter(ExecuteOnEntityMetaAction::action),
			EntityTarget.CODEC.fieldOf("entity").forGetter(ExecuteOnEntityMetaAction::entity)
		).apply(instance, constructor));
	}

	static <M extends ExecuteOnEntityMetaAction> PacketCodec<RegistryByteBuf, M> packetCodec(BiFunction<EntityAction, EntityTarget, M> constructor) {
		return PacketCodec.tuple(
			EntityAction.PACKET_CODEC, ExecuteOnEntityMetaAction::action,
			EntityTarget.PACKET_CODEC, ExecuteOnEntityMetaAction::entity,
			constructor
		);
	}

}
