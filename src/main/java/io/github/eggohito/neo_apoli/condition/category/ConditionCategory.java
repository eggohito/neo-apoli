package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class ConditionCategory<C extends Condition> implements Category<C> {

	public static final Codec<ConditionCategory<?>> CODEC = NeoApoliRegistries.CONDITION_CATEGORY.getCodec();
	public static final PacketCodec<RegistryByteBuf, ConditionCategory<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.CONDITION_CATEGORY);

	private final Codec<C> entryCodec = new ValueSuppliedElementCodec<>(this.baseCodec(), true, id -> ConditionManager.getAsResult(ConditionCategory.this, id), ConditionManager::getIdAsResult);

	@Override
	public Codec<C> entryCodec() {
		return entryCodec;
	}

	@Nullable
	public Function<String, CommandBuilder> commandBuilderFactory() {
		return null;
	}

	@FunctionalInterface
	public interface CommandBuilder {
		ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandNode<ServerCommandSource> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive);
	}

}
