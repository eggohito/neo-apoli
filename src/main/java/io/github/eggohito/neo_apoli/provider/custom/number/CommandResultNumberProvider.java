package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
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

	public static final MapCodec<CommandResultNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("command").forGetter(CommandResultNumberProvider::command)
	).apply(instance, CommandResultNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CommandResultNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, CommandResultNumberProvider::command,
		CommandResultNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.COMMAND_RESULT;
	}

	@Override
	public double nextDouble(Context context) {

		if (!(context.level() instanceof ServerLevel serverWorld)) {
			return 0;
		}

		MinecraftServer server = serverWorld.getServer();
		AtomicInteger result = new AtomicInteger();

		Context commandContext = context.forChild(".command");
		String command = command().nextString(commandContext);

		if (commandContext.hasErrors()) {
			return result.get();
		}

		CommandSourceStack commandSource = server.createCommandSourceStack()
			.withPosition(Vec3.ZERO)
			.withPermission(NeoApoli.getConfig().command.get().permissionLevel())
			.withSource(NeoApoli.validateCommandOutput(server))
			.withCallback((successful, returnValue) -> result.set(returnValue));

		server.getCommands().performPrefixedCommand(commandSource, command);
		return result.get();

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		command().validate(validator.forChild(".command"));
	}

}
