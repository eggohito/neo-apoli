package io.github.eggohito.neo_apoli.provider.custom.command_source;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.config.NeoApoliCommonConfig;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliCommandSourceProviderTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"UnstableApiUsage", "ConstantValue"})
public record EntityCommandSourceProvider(EntityProvider entity) implements CommandSourceProvider {

	public static final MapCodec<EntityCommandSourceProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(EntityProvider.CODEC.fieldOf("entity").forGetter(EntityCommandSourceProvider::entity))
		.apply(instance, EntityCommandSourceProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityCommandSourceProvider> STREAM_CODEC = StreamCodec.composite(
		EntityProvider.STREAM_CODEC, EntityCommandSourceProvider::entity,
		EntityCommandSourceProvider::new
	);

	@Override
	public CommandSourceProvider.Type<?> getType() {
		return NeoApoliCommandSourceProviderTypes.ENTITY;
	}

	@Override
	public CommandSourceStack getSource(ServerLevel serverLevel, Context context) {

		MinecraftServer server = serverLevel.getServer();
		Entity entity = entity().getEntity(context.forChild(".entity")).orElse(null);

		return NeoApoliCommonConfig.INSTANCE.command.get().sanitize(this.getCommandSource(entity, server));

	}

	@Override
	public void validate(Context.Validator validator) {
		CommandSourceProvider.super.validate(validator);
		entity().validate(validator.forChild(".entity"));
	}

	private CommandSourceStack getCommandSource(@Nullable Entity entity, MinecraftServer server) {

		if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
			return serverPlayer.createCommandSourceStack();
		}

		else {
			return server.createCommandSourceStack();
		}

	}

}
