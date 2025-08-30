package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;

public class BiEntityConditionCategory extends ConditionCategory<BiEntityCondition> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = conditionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(Optional<CommandNode<ServerCommandSource>> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive) {
			return builder
				.then(argument("actor", EntityArgumentType.entity())
					.then(CommandBuilder.optionallyAddForkedConditionLogic(root, argument("target", EntityArgumentType.entity()), positive, this::test)));
		}

		public boolean test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			BiEntityCondition biEntityCondition = ConditionArgumentType.getCondition(commandContext, conditionKey, BiEntityCondition.class);

			Entity actor = EntityArgumentType.getEntity(commandContext, "actor");
			Entity target = EntityArgumentType.getEntity(commandContext, "target");

			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ConditionManager.getIdAsResult(biEntityCondition).mapOrElse(Identifier::toString, err -> biEntityCondition.toString()) + "}")
				.withContextType(ContextTypeUtil.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY))
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandSource.getServer().getReloadableRegistries()).getRegistries());

			Context context = Context.builder(reporter)
				.add(ContextParameters.ACTOR, actor)
				.add(ContextParameters.TARGET, target)
				.build(commandSource.getWorld());

			biEntityCondition.validate(reporter);

			if (reporter.hasAnyErrors()) {
				throw MiscUtil.createCommandException(Text.literal("Error(s) while validating bi-entity condition " + reporter.getErrorsAsString()));
			}

			else {

				boolean result = biEntityCondition.test(context);
				if (reporter.hasAnyErrors()) {
					throw MiscUtil.createCommandException(Text.literal("Error(s) while testing bi-entity condition " + reporter.getErrorsAsString()));
				}

				else {
					return result;
				}

			}

		}

	};

	BiEntityConditionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<BiEntityCondition>> registryRef() {
		return NeoApoliRegistryKeys.BIENTITY_CONDITION;
	}

	@Override
	public @Nullable Function<String, CommandBuilder> commandBuilderFactory() {
		return BUILDER_FACTORY;
	}

	@Override
	public Codec<BiEntityCondition> codec() {
		return BiEntityCondition.CODEC;
	}

	@Override
	public MapCodec<BiEntityCondition> mapCodec() {
		return BiEntityCondition.MAP_CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, BiEntityCondition> packetCodec() {
		return BiEntityCondition.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Bi-entity condition";
	}

}
