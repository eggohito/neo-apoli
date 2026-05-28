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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

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
	public CommandSourceProvider.Type<?> getType() {
		return NeoApoliCommandSourceProviderTypes.BLOCK;
	}

	@Override
	public CommandSourceStack getSource(ServerLevel serverLevel, Context context) {
		CachedBlock block = block().getBlock(context.forChild(".block")).orElse(null);
		return NeoApoliCommonConfig.INSTANCE.command.get().sanitize(this.getCommandSource(block, serverLevel));
	}

	@Override
	public void validate(Context.Validator validator) {
		CommandSourceProvider.super.validate(validator);
		block().validate(validator.forChild(".block"));
	}

	private CommandSourceStack getCommandSource(@Nullable CachedBlock block, ServerLevel serverLevel) {

		if (block != null) {
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

		else {
			return serverLevel.getServer().createCommandSourceStack();
		}

	}

}
