package io.github.eggohito.neo_apoli.provider.custom.command_source;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.config.NeoApoliCommonConfig;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.block.BlockProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliCommandSourceProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public record BlockCommandSourceProvider(BlockProvider block) implements CommandSourceProvider {

	public static final MapCodec<BlockCommandSourceProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockProvider.CODEC.fieldOf("block").forGetter(BlockCommandSourceProvider::block))
		.apply(instance, BlockCommandSourceProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockCommandSourceProvider> STREAM_CODEC = StreamCodec.composite(
		BlockProvider.STREAM_CODEC, BlockCommandSourceProvider::block,
		BlockCommandSourceProvider::new
	);

	@Override
	public CommandSourceProvider.@NotNull Type<?> getType() {
		return NeoApoliCommandSourceProviderTypes.BLOCK;
	}

	@Override
	public Optional<CommandSourceStack> getSource(MinecraftServer server, Context context) {
		return block().getBlock(context.forChild(".block"))
			.map(block -> this.getCommandSource(context, block))
			.map(NeoApoliCommonConfig.INSTANCE.command.get()::sanitizeSource);
	}

	@Override
	public void validate(Context.Validator validator) {
		CommandSourceProvider.super.validate(validator);
		block().validate(validator.forChild(".block"));
	}

	private CommandSourceStack getCommandSource(Context context, CachedBlock block) {

		ServerLevel serverLevel = (ServerLevel) context.level();
		Component blockName = Component.translatable(block.state().getBlock().getDescriptionId());

		return new CommandSourceStack(

			CommandSource.NULL,
			block.pos().getCenter(),
			Vec2.ZERO,
			serverLevel,
			0,
			blockName.getString(),
			blockName,
			serverLevel.getServer(),
			null
		);

	}

}
