package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class ConditionCategories {

	public static final Codec<ConditionCategory<?>> CODEC = NeoApoliRegistries.CONDITION_CATEGORY.getCodec();
	public static final PacketCodec<RegistryByteBuf, ConditionCategory<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.CONDITION_CATEGORY);

	public static final BiEntityConditionCategory BIENTITY_CONDITION = register(new BiEntityConditionCategory());
	public static final BlockConditionCategory BLOCK_CONDITION = register(new BlockConditionCategory());
	public static final EntityConditionCategory ENTITY_CONDITION = register(new EntityConditionCategory());
	public static final DamageConditionCategory DAMAGE_CONDITION = register(new DamageConditionCategory());
	public static final ItemConditionCategory ITEM_CONDITION = register(new ItemConditionCategory());

	public static void registerAll() {

	}

	public static <C extends Condition, CC extends ConditionCategory<C>> CC register(CC category) {
		return Registry.register(NeoApoliRegistries.CONDITION_CATEGORY, category.registryRef().getValue(), category);
	}

	public static ArgumentBuilder<ServerCommandSource, ?> addArguments(Optional<CommandNode<ServerCommandSource>> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive) {

		for (var category : NeoApoliRegistries.CONDITION_CATEGORY) {

			String categoryId = category.registryRef().getValue().toString();
			Function<String, ConditionCategory.CommandBuilder> commandBuilderFactory = category.commandBuilderFactory();

			if (commandBuilderFactory == null) {
				continue;
			}

			Consumer<String> finalizer = key -> builder
				.then(literal(categoryId)
					.then(commandBuilderFactory.apply(key).addArguments(root, registryAccess, argument(key, ConditionArgumentType.condition(registryAccess, category)), positive)));

			finalizer.accept("condition");

		}

		return builder;

	}

}
