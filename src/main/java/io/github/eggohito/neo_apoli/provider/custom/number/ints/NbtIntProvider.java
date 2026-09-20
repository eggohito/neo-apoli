package io.github.eggohito.neo_apoli.provider.custom.number.ints;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.IntProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NbtNumberProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliIntProviderTypes;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public record NbtIntProvider(NbtProvider source, NbtPathArgument.NbtPath path) implements IntProvider, NbtNumberProvider {

	public static final MapCodec<NbtIntProvider> CODEC = NbtNumberProvider.mapCodec(NbtIntProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, NbtIntProvider> STREAM_CODEC = NbtNumberProvider.streamCodec(NbtIntProvider::new);

	@Override
	public @NotNull IntProvider.Type<?> getType() {
		return NeoApoliIntProviderTypes.NBT;
	}

	@Override
	public void provideInt(Context context, IntConsumer setter) {
		this.processTag(context, numericTag -> setter.accept(numericTag.intValue()), setter);
	}

}
