package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

//	TODO: Generalize this action and add some sort of provider for command sources
public record ExecuteCommandEntityAction(StringProvider command) implements EntityAction {

	public static final MapCodec<ExecuteCommandEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandEntityAction::command))
		.apply(instance, ExecuteCommandEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, ExecuteCommandEntityAction> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, ExecuteCommandEntityAction::command,
		ExecuteCommandEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters()) || !(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Entity entity = context.required(ContextParameters.THIS_ENTITY);
		Vec3d pos = context.required(ContextParameters.ENTITY_POS);

		Context commandContext = context.makeChild(".command");
		String command = command().next(commandContext);

		if (commandContext.hasErrors()) {
			return;
		}

		MinecraftServer server = serverWorld.getServer();
		ServerCommandSource commandSource = new ServerCommandSource(
			NeoApoli.validateCommandOutput(this.getOutput(entity, server)),
			pos,
			Vec2f.ZERO,
			serverWorld,
			NeoApoli.getConfig().command().permissionLevel(),
			entity.getName().getString(),
			entity.getName(),
			server,
			entity
		);

		server.getCommandManager().executeWithPrefix(commandSource, command);

	}

	private CommandOutput getOutput(Entity entity, MinecraftServer server) {

		if (entity instanceof ServerPlayerEntity serverPlayer && serverPlayer.networkHandler != null) {
			return serverPlayer.getCommandOutput();
		}

		else {
			return server;
		}

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.THIS_ENTITY, ContextParameters.ENTITY_POS);
	}

}
