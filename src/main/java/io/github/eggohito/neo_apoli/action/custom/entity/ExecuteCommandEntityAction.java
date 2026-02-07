package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

//	TODO: Generalize this action and add some sort of provider for command sources
public record ExecuteCommandEntityAction(StringProvider command) implements EntityAction {

	public static final MapCodec<ExecuteCommandEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandEntityAction::command))
		.apply(instance, ExecuteCommandEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteCommandEntityAction> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, ExecuteCommandEntityAction::command,
		ExecuteCommandEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters()) || !(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		Entity entity = context.getRequired(NeoApoliContextParams.THIS_ENTITY);
		Vec3 pos = context.getRequired(NeoApoliContextParams.THIS_POS);

		MinecraftServer server = serverLevel.getServer();
		String command = command().next(context.forChild(".command"));

		CommandSourceStack source = new CommandSourceStack(
			NeoApoli.validateCommandOutput(this.getOutput(entity, server)),
			pos,
			entity.getRotationVector(),
			serverLevel,
			NeoApoli.getConfig().command.permissionLevel,
			entity.getName().getString(),
			entity.getName(),
			server,
			entity
		);

		server.getCommands().performPrefixedCommand(source, command);

	}

	private CommandSource getOutput(Entity entity, MinecraftServer server) {

		if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
			return serverPlayer.commandSource();
		}

		else {
			return server;
		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_ENTITY, NeoApoliContextParams.THIS_POS);
	}

}
