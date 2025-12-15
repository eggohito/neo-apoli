package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

//	TODO: Generalize this action and add some sort of provider for command sources
public record ExecuteCommandBlockAction(StringProvider command) implements BlockAction {

	public static final MapCodec<ExecuteCommandBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandBlockAction::command))
		.apply(instance, ExecuteCommandBlockAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteCommandBlockAction> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, ExecuteCommandBlockAction::command,
		ExecuteCommandBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void execute(Context context) {

		if (!(context.getLevel() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		BlockPos blockPos = context.required(NeoApoliContextKeys.BLOCK_POS);
		BlockState blockState = context.required(NeoApoliContextKeys.BLOCK_STATE);

		Context commandContext = context.forChild(".command");
		String command = command().next(commandContext);

		if (commandContext.hasErrors() || command.isEmpty()) {
			return;
		}

		MinecraftServer server = serverLevel.getServer();
		CommandSourceStack commandSource = new CommandSourceStack(
			NeoApoli.validateCommandOutput(server),
			blockPos.getCenter(),
			Vec2.ZERO,
			serverLevel,
			NeoApoli.getConfig().command.permissionLevel,
			blockState.getBlock().getDescriptionId(),
			Component.translatable(blockState.getBlock().getDescriptionId()),
			server,
			null
		);

		server.getCommands().performPrefixedCommand(commandSource, command);

	}

	@Override
	public void validate(ProblemReporter reporter) {
		BlockAction.super.validate(reporter);
		command().validate(reporter.forChild(".command"));
	}

}
