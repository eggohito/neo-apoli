package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.atomic.AtomicInteger;

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
	public double doubleValue(Context context) {

		Context commandContext = context.makeChild("command");
		String command = command().stringValue(commandContext);

		AtomicInteger result = new AtomicInteger();
		if (!commandContext.pathHasErrors()) {

			if (context.getWorld() instanceof ServerWorld serverWorld) {

				MinecraftServer server = serverWorld.getServer();
				ServerCommandSource commandSource = server.getCommandSource()
					.withPosition(context.optionalParameter(ContextParameters.POSITION).orElse(Vec3d.ZERO))
					.withLevel(NeoApoli.getConfig().command().permissionLevel())
					.withOutput(NeoApoli.validateCommandOutput(server).orElse(CommandOutput.DUMMY))
					.withReturnValueConsumer((successful, returnValue) -> result.set(returnValue));

				server.getCommandManager().executeWithPrefix(commandSource, command);

			}

			else {
				commandContext.getReporter().report("Couldn't get result of command \"" + command + "\" in the client!");
			}

		}

		return result.get();

	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		command().validate(reporter.makeChild("command"));
	}

}
