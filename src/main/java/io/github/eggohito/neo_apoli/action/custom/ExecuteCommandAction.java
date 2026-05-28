package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.command_source.CommandSourceProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public record ExecuteCommandAction(CommandSourceProvider source, StringProvider command) implements Action {

	public static final MapCodec<ExecuteCommandAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		CommandSourceProvider.CODEC.fieldOf("source").forGetter(ExecuteCommandAction::source),
		StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandAction::command)
	).apply(instance, ExecuteCommandAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteCommandAction> STREAM_CODEC = StreamCodec.composite(
		CommandSourceProvider.STREAM_CODEC, ExecuteCommandAction::source,
		StringProvider.STREAM_CODEC, ExecuteCommandAction::command,
		ExecuteCommandAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		MinecraftServer server = serverLevel.getServer();
		CommandSourceStack source = source().getSource(serverLevel, context.forChild(".source"));

		Context commandContext = context.forChild(".command");
		String command = command().getString(commandContext);

		if (!commandContext.hasErrors()) {
			server.getCommands().performPrefixedCommand(source, command);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		source().validate(validator.forChild(".source"));
		command().validate(validator.forChild(".command"));
	}

}
