package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
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

public class EntityConditionCategory extends ConditionCategory<EntityCondition> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = conditionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(Optional<CommandNode<ServerCommandSource>> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive) {
			return builder
				.then(CommandBuilder.optionallyAddForkedConditionLogic(root, argument("target", EntityArgumentType.entity()), positive, this::test));
		}

		public boolean test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			Entity target = EntityArgumentType.getEntity(commandContext, "target");

			EntityCondition entityCondition = ConditionArgumentType.getCondition(commandContext, conditionKey, EntityCondition.class);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ConditionManager.getIdAsResult(entityCondition).mapOrElse(Identifier::toString, error -> entityCondition.toString()) + "}")
				.withContextType(ContextTypeUtil.merge(ContextTypes.GENERIC, ContextTypes.ENTITY))
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandSource.getServer().getReloadableRegistries()).getRegistries());

			Context context = Context.builder(reporter)
				.add(ContextParameters.ENTITY, target)
				.add(ContextParameters.ENTITY_POS, target.getPos())
				.build(commandSource.getWorld());

			entityCondition.validate(reporter);

			if (reporter.hasAnyErrors()) {
				throw MiscUtil.createCommandException(Text.literal("Error(s) while validating entity condition " + reporter.getErrorsAsString()));
			}

			else {

				boolean result = entityCondition.test(context);

				if (reporter.hasAnyErrors()) {
					throw MiscUtil.createCommandException(Text.literal("Error(s) while testing entity condition " + reporter.getErrorsAsString()));
				}

				else {
					return result;
				}

			}

		}

	};

	EntityConditionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<EntityCondition>> registryRef() {
		return NeoApoliRegistryKeys.ENTITY_CONDITION;
	}

	@Override
	public @Nullable Function<String, CommandBuilder> commandBuilderFactory() {
		return BUILDER_FACTORY;
	}

	@Override
	public Codec<EntityCondition> codec() {
		return EntityCondition.CODEC;
	}

	@Override
	public MapCodec<EntityCondition> mapCodec() {
		return EntityCondition.MAP_CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, EntityCondition> packetCodec() {
		return EntityCondition.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Entity condition";
	}

}
