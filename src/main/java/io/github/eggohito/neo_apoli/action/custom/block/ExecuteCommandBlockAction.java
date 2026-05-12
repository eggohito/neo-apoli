package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
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

	public static final MapCodec<ExecuteCommandBlockAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(StringProvider.CODEC.fieldOf("command").forGetter(ExecuteCommandBlockAction::command))
		.apply(instance, ExecuteCommandBlockAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteCommandBlockAction> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, ExecuteCommandBlockAction::command,
		ExecuteCommandBlockAction::new
	);

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		BlockPos blockPos = context.getRequired(NeoApoliContextParams.BLOCK_POS);
		BlockState blockState = context.getRequired(NeoApoliContextParams.BLOCK_STATE);

		MinecraftServer server = serverLevel.getServer();
		String command = command().nextString(context.forChild(".command"));

		if (command.isEmpty()) {
			return;
		}

		CommandSourceStack source = new CommandSourceStack(
			NeoApoli.validateCommandOutput(server),
			blockPos.getCenter(),
			Vec2.ZERO,
			serverLevel,
			NeoApoli.getConfig().command.get().permissionLevel(),
			blockState.getBlock().getDescriptionId(),
			Component.translatable(blockState.getBlock().getDescriptionId()),
			server,
			null
		);

		server.getCommands().performPrefixedCommand(source, command);

	}

	@Override
	public void validate(Context.Validator validator) {
		BlockAction.super.validate(validator);
		command().validate(validator.forChild(".command"));
	}

}
