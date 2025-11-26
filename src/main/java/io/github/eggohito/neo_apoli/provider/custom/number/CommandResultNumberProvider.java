package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

//	TODO: Add providers for command sources
public record CommandResultNumberProvider(StringProvider command) implements NumberProvider {

	public static final MapCodec<CommandResultNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("command").forGetter(CommandResultNumberProvider::command)
	).apply(instance, CommandResultNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CommandResultNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, CommandResultNumberProvider::command,
		CommandResultNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.COMMAND_RESULT;
	}

	@Override
	public @NotNull Number next(Context context) {

		if (!(context.getWorld() instanceof ServerLevel serverWorld)) {
			return 0;
		}

		MinecraftServer server = serverWorld.getServer();
		AtomicInteger result = new AtomicInteger();

		Context commandContext = context.makeChild(".command");
		String command = command().next(commandContext);

		if (commandContext.hasErrors()) {
			return result.get();
		}

		CommandSourceStack commandSource = server.createCommandSourceStack()
			.withPosition(Vec3.ZERO)
			.withPermission(NeoApoli.getConfig().command().permissionLevel())
			.withSource(NeoApoli.validateCommandOutput(server))
			.withCallback((successful, returnValue) -> result.set(returnValue));

		server.getCommands().performPrefixedCommand(commandSource, command);
		return result.get();

	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		command().validate(reporter.forChild(".command"));
	}

}
