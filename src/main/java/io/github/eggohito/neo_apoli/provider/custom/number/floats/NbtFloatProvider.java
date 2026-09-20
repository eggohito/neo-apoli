package io.github.eggohito.neo_apoli.provider.custom.number.floats;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NbtNumberProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliFloatProviderTypes;
import io.github.eggohito.neo_apoli.util.FloatConsumer;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record NbtFloatProvider(NbtProvider source, NbtPathArgument.NbtPath path) implements FloatProvider, NbtNumberProvider {

	public static final MapCodec<NbtFloatProvider> CODEC = NbtNumberProvider.mapCodec(NbtFloatProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, NbtFloatProvider> STREAM_CODEC = NbtNumberProvider.streamCodec(NbtFloatProvider::new);

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.NBT;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {
		this.processTag(context, numericTag -> setter.accept(numericTag.floatValue()), setter::accept);
	}

}
