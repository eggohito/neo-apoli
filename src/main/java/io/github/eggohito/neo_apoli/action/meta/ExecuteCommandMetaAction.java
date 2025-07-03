package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Function;

public interface ExecuteCommandMetaAction {

	StringProvider command();

	@ApiStatus.Internal
	default void internalImpl(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Optional<Entity> entity = context.optional(ContextParameters.THIS_ENTITY);
		MinecraftServer server = serverWorld.getServer();

		CommandOutput commandOutput = NeoApoli.validateCommandOutput(entity
			.map(ExecuteCommandMetaAction::getOutputFromEntity)
			.orElse(server));

		ServerCommandSource commandSource = new ServerCommandSource(
			commandOutput,
			context.required(ContextParameters.POSITION),
			entity.map(Entity::getRotationClient).orElse(Vec2f.ZERO),
			serverWorld,
			NeoApoli.getConfig().command().permissionLevel(),
			entity.map(e -> e.getName().getString()).orElse("Server"),
			entity.map(Entity::getDisplayName).orElse(Text.literal("Server")),
			server,
			entity.orElse(null)
		);

		Context commandContext = context.makeChild(".command");
		String command = command().next(commandContext);

		if (!commandContext.hasErrors()) {
			server.getCommandManager().executeWithPrefix(commandSource, command);
		}

	}

	default void validate(ContextAware.ErrorReporter reporter) {
		command().validate(reporter.makeChild(".command"));
	}

	static CommandOutput getOutputFromEntity(Entity entity) {

		if (entity instanceof ServerPlayerEntity serverPlayer && serverPlayer.networkHandler != null) {
			return serverPlayer.getCommandOutput();
		}

		else {
			return CommandOutput.DUMMY;
		}

	}

	static <M extends ExecuteCommandMetaAction> MapCodec<M> codec(Function<StringProvider, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandMetaAction::command)
		).apply(instance, constructor));
	}

	static <M extends ExecuteCommandMetaAction> PacketCodec<RegistryByteBuf, M> packetCodec(Function<StringProvider, M> constructor) {
		return PacketCodec.tuple(
			StringProvider.PACKET_CODEC, ExecuteCommandMetaAction::command,
			constructor
		);
	}

}
