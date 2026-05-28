package io.github.eggohito.neo_apoli.provider.custom.command_source;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.config.NeoApoliCommonConfig;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

@SuppressWarnings("UnstableApiUsage")
public enum ServerCommandSourceProvider implements SimpleCommandSourceProvider<ServerCommandSourceProvider> {

	INSTANCE;

	public static final MapCodec<ServerCommandSourceProvider> CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerCommandSourceProvider> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public MapCodec<ServerCommandSourceProvider> mapCodec() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ServerCommandSourceProvider> streamCodec() {
		return STREAM_CODEC;
	}

	@Override
	public CommandSourceStack getSource(ServerLevel serverLevel, Context context) {
		return NeoApoliCommonConfig.INSTANCE.command.get().sanitize(serverLevel.getServer().createCommandSourceStack());
	}

}
