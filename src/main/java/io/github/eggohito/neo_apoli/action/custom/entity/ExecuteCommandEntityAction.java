package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextType;

public record ExecuteCommandEntityAction(StringProvider command) implements EntityAction {

	public static final MapCodec<ExecuteCommandEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandEntityAction::command)
	).apply(instance, ExecuteCommandEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, ExecuteCommandEntityAction> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, ExecuteCommandEntityAction::command,
		ExecuteCommandEntityAction::new
	);

	public static final ContextType CONTEXT_TYPE = new ContextType.Builder()
		.require(LootContextParameters.THIS_ENTITY)
		.require(LootContextParameters.ORIGIN)
		.allow(LootContextParameters.ATTACKING_ENTITY)
		.allow(LootContextParameters.LAST_DAMAGE_PLAYER)
		.build();

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void execute(ErrorReporter reporter, EntityActionContext context) {

		if (context.entity().isEmpty()) {
			return;
		}

		Entity entity = context.entity().get();
		if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		//	TODO: Create a config for customizing the permission level and command output of this entity action
		ServerCommandSource commandSource = entity.getCommandSource(serverWorld);
		ValueProviderContext providerContext = ValueProviderContext.builder(CONTEXT_TYPE)
			.add(LootContextParameters.THIS_ENTITY, entity)
			.add(LootContextParameters.ORIGIN, entity.getPos())
			.addOptional(LootContextParameters.ATTACKING_ENTITY, entity instanceof LivingEntity livingEntity ? livingEntity.getAttacker() : null)
			.addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, entity instanceof LivingEntity livingEntity ? livingEntity.getAttackingPlayer() : null)
			.build(serverWorld);

		ErrorReporter finalReporter = reporter.withContextType(CONTEXT_TYPE);
		this.validate(finalReporter);

		finalReporter.getErrorsAsString().ifPresentOrElse(
			error -> NeoApoli.LOGGER.warn("Error executing command due to error {}", error),
			() -> serverWorld.getServer().getCommandManager().executeWithPrefix(commandSource, command().get(reporter, providerContext))
		);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		command().validate(reporter.makeChild("command"));
	}

}
