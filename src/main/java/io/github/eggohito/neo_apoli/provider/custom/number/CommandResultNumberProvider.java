package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

//	TODO: Add providers for command sources
public record CommandResultNumberProvider(StringProvider command) implements NumberProvider {

	public static final MapCodec<CommandResultNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("command").forGetter(CommandResultNumberProvider::command)
	).apply(instance, CommandResultNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, CommandResultNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, CommandResultNumberProvider::command,
		CommandResultNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.COMMAND_RESULT;
	}

	@Override
	public @NotNull Number next(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return 0;
		}

		MinecraftServer server = serverWorld.getServer();
		AtomicInteger result = new AtomicInteger();

		Context commandContext = context.makeChild(".command");
		String command = command().next(commandContext);

		if (commandContext.hasErrors()) {
			return result.get();
		}

		ServerCommandSource commandSource = server.getCommandSource()
			.withPosition(Vec3d.ZERO)
			.withLevel(NeoApoli.getConfig().command().permissionLevel())
			.withOutput(NeoApoli.validateCommandOutput(server))
			.withReturnValueConsumer((successful, returnValue) -> result.set(returnValue));

		server.getCommandManager().executeWithPrefix(commandSource, command);
		return result.get();

	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		command().validate(reporter.makeChild(".command"));
	}

}
