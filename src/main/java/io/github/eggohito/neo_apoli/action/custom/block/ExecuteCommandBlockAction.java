package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

//	TODO: Generalize this action and add some sort of provider for command sources
public record ExecuteCommandBlockAction(StringProvider command) implements BlockAction {

	public static final MapCodec<ExecuteCommandBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandBlockAction::command))
		.apply(instance, ExecuteCommandBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, ExecuteCommandBlockAction> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, ExecuteCommandBlockAction::command,
		ExecuteCommandBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void serverExecute(ServerContext context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		ServerWorld world = context.getWorld();
		MinecraftServer server = context.getServer();

		BlockPos blockPos = context.required(NeoApoliContextParameters.BLOCK_POS);
		BlockState blockState = context.required(NeoApoliContextParameters.BLOCK_STATE);

		ServerContext commandContext = context.makeChild(".command");
		String command = command().next(commandContext);

		if (commandContext.hasErrors()) {
			return;
		}

		ServerCommandSource commandSource = new ServerCommandSource(
			NeoApoli.validateCommandOutput(server),
			blockPos.toCenterPos(),
			Vec2f.ZERO,
			world,
			NeoApoli.getConfig().command().permissionLevel(),
			blockState.getBlock().getTranslationKey(),
			Text.translatable(blockState.getBlock().getTranslationKey()),
			server,
			null
		);

		server.getCommandManager().executeWithPrefix(commandSource, command);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		BlockAction.super.validate(reporter);
		command().validate(reporter.makeChild(".command"));
	}

}
