package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
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
import net.minecraft.world.World;

import java.util.function.Supplier;

public record ExecuteCommandEntityAction(StringProvider command) implements EntityAction {

	public static final MapCodec<ExecuteCommandEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandEntityAction::command)
	).apply(instance, ExecuteCommandEntityAction::new));

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

		Entity entity = context.requiredParameter(ContextParameters.CURRENT_ENTITY);
		World world = context.getWorld();

		if (!(world instanceof ServerWorld serverWorld)) {
			return;
		}

		MinecraftServer server = serverWorld.getServer();
		ServerCommandSource commandSource = entity.getCommandSource(serverWorld)
			.withLevel(NeoApoli.getConfig().command().permissionLevel())
			.withOutput(getOutputOrElse(entity, () -> server));

		server.getCommandManager().executeWithPrefix(commandSource, command().get(context.makeChild("command")));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityAction.super.validate(reporter);
		command().validate(reporter.makeChild("command"));
	}

	private static CommandOutput getOutputOrElse(Entity entity, Supplier<CommandOutput> defaultValue) {

		if (NeoApoli.getConfig().command().showOutput()) {

			if (entity instanceof ServerPlayerEntity serverPlayer && serverPlayer.networkHandler != null) {
				return serverPlayer.getCommandOutput();
			}

			else {
				return defaultValue.get();
			}

		}

		else {
			return CommandOutput.DUMMY;
		}

	}

}
