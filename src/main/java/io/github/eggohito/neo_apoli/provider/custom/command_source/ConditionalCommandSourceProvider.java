package io.github.eggohito.neo_apoli.provider.custom.command_source;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliCommandSourceProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ConditionalCommandSourceProvider(Condition condition, CommandSourceProvider ifValue, CommandSourceProvider elseValue) implements CommandSourceProvider, ConditionalValueProvider<CommandSourceProvider> {

	public static final MapCodec<ConditionalCommandSourceProvider> CODEC = MapCodecUtil.lazy(ConditionalCommandSourceProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(CommandSourceProvider.CODEC, ConditionalCommandSourceProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalCommandSourceProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalCommandSourceProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(CommandSourceProvider.STREAM_CODEC, ConditionalCommandSourceProvider::new));

	@Override
	public CommandSourceProvider.@NotNull Type<?> getType() {
		return NeoApoliCommandSourceProviderTypes.CONDITIONAL;
	}

	@Override
	public Optional<CommandSourceStack> getSource(MinecraftServer server, Context context) {
		return this.getOrElse(context, (provider, ctx) -> provider.getSource(server, ctx), Optional::empty);
	}

}
