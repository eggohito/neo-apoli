package io.github.eggohito.neo_apoli.provider.custom.command_source;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.CompositeConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliCommandSourceProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.conditional.CompositeConditional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record CompositeConditionalCommandSourceProvider(List<CompositeConditional.Entry<CommandSourceProvider>> entries, CommandSourceProvider defaultValue) implements CommandSourceProvider, CompositeConditionalValueProvider<CommandSourceProvider> {

	public static final MapCodec<CompositeConditionalCommandSourceProvider> CODEC = MapCodecUtil.lazy(CompositeConditionalCommandSourceProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.mapCodec(CommandSourceProvider.CODEC, CompositeConditionalCommandSourceProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CompositeConditionalCommandSourceProvider> STREAM_CODEC = StreamCodecUtil.lazy(CompositeConditionalCommandSourceProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.streamCodec(CommandSourceProvider.STREAM_CODEC, CompositeConditionalCommandSourceProvider::new));

	@Override
	public CommandSourceProvider.@NotNull Type<?> getType() {
		return NeoApoliCommandSourceProviderTypes.COMPOSITE_CONDITIONAL;
	}

	@Override
	public Optional<CommandSourceStack> getSource(Context context) {
		return this.getOrDefault(context, CommandSourceProvider::getSource);
	}

}
