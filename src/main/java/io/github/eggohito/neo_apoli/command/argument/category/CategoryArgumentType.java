package io.github.eggohito.neo_apoli.command.argument.category;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.category.Category;
import lombok.AllArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public abstract class CategoryArgumentType<C extends Category<?>> implements ArgumentType<C> {

	private final Registry<C> registry;
	private final Codec<C> codec;

	@Override
	public C parse(StringReader reader) throws CommandSyntaxException {

		Identifier id = Identifier.fromCommandInputNonEmpty(reader);
		DynamicOps<NbtElement> ops = NbtOps.INSTANCE;

		Dynamic<NbtElement> result = new Dynamic<>(ops, ops.createString(id.toString()));
		return codec.parse(result).getOrThrow(err -> MiscUtil.createCommandException(() -> err));

	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(registry.getIds().stream(), builder);
	}

}
