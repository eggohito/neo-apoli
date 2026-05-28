package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.command_source.CommandSourceProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public record CommandResultNumberProvider(CommandSourceProvider source, StringProvider command) implements NumberProvider {

	public static final MapCodec<CommandResultNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		CommandSourceProvider.CODEC.fieldOf("source").forGetter(CommandResultNumberProvider::source),
		StringProvider.CODEC.fieldOf("command").forGetter(CommandResultNumberProvider::command)
	).apply(instance, CommandResultNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CommandResultNumberProvider> STREAM_CODEC = StreamCodec.composite(
		CommandSourceProvider.STREAM_CODEC, CommandResultNumberProvider::source,
		StringProvider.STREAM_CODEC, CommandResultNumberProvider::command,
		CommandResultNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.COMMAND_RESULT;
	}

	@Override
	public double getDouble(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return 0;
		}

		MinecraftServer server = serverLevel.getServer();
		AtomicInteger result = new AtomicInteger();

		CommandSourceStack source = source()
			.getSource(serverLevel, context.forChild(".source"))
			.withCallback((successful, returnValue) -> result.set(returnValue));

		Context commandContext = context.forChild(".command");
		String command = command().getString(commandContext);

		if (!commandContext.hasErrors()) {
			server.getCommands().performPrefixedCommand(source, command);
		}

		return result.get();

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		source().validate(validator.forChild(".source"));
		command().validate(validator.forChild(".command"));
	}

}
