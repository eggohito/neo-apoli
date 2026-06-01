package io.github.eggohito.neo_apoli.provider.custom.command_source;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliCommandSourceProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchCommandSourceProvider(List<Case<Condition, CommandSourceProvider>> cases, CommandSourceProvider defaultValue) implements CommandSourceProvider, SwitchValueProvider<CommandSourceProvider> {

	public static final MapCodec<SwitchCommandSourceProvider> CODEC = MapCodecUtil.lazy(SwitchCommandSourceProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(CommandSourceProvider.CODEC, SwitchCommandSourceProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchCommandSourceProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchCommandSourceProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(CommandSourceProvider.STREAM_CODEC, SwitchCommandSourceProvider::new));

	@Override
	public CommandSourceProvider.@NotNull Type<?> getType() {
		return NeoApoliCommandSourceProviderTypes.SWITCH;
	}

	@Override
	public @NotNull CommandSourceStack getSource(ServerLevel serverLevel, Context context) {
		return this.getOrDefault(context, (provider, ctx) -> provider.getSource(serverLevel, ctx));
	}

}
