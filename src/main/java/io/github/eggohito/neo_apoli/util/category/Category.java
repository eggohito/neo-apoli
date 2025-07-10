package io.github.eggohito.neo_apoli.util.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Category<A> {

	RegistryKey<? extends Registry<A>> registryRef();

	Codec<A> baseCodec();

	default Codec<A> entryCodec() {
		return this.baseCodec();
	}

	PacketCodec<RegistryByteBuf, A> basePacketCodec();

	@Nullable
	default Function<String, CommandBuilder> commandBuilderFactory() {
		return null;
	}

	@FunctionalInterface
	interface CommandBuilder {
		ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder);
	}

}
