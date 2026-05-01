package io.github.eggohito.neo_apoli.condition.kind;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.command.argument.condition.ConditionArgument;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.mixin.access.ExecuteCommandAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.Kind;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.ExecuteCommand;
import org.apache.commons.lang3.function.FailableFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public interface ConditionKind<C extends Condition> extends Kind<C>, StringDisplayable {

	ConditionKind<Condition> INSTANCE = new ConditionKind<>() {

		@Override
		public @Nullable Function<String, CommandBuilder> commandBuilder() {
			return null;
		}

		@Override
		public ResourceKey<? extends Registry<Condition>> registryKey() {
			return NeoApoliRegistryKeys.CONDITION;
		}

		@Override
		public Codec<Condition> codec() {
			return Condition.CODEC;
		}

		@Override
		public String asDisplayString() {
			return "Condition";
		}

	};

	Codec<ConditionKind<?>> CODEC = NeoApoliRegistries.CONDITION_KIND.byNameCodec();

	StreamCodec<RegistryFriendlyByteBuf, ConditionKind<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.CONDITION_KIND);

	@Nullable
	Function<String, CommandBuilder> commandBuilder();

	default boolean test(CommandContext<CommandSourceStack> commandContext, String conditionKey, Function<C, String> fallbackName, FailableFunction<Context.Builder, Context.Builder, CommandSyntaxException> builderOperator) throws CommandSyntaxException {

		CommandSourceStack source = commandContext.getSource();

		C condition = ConditionArgument.getCondition(commandContext, this, conditionKey);
		String path = ConditionManager.getIdAsResult(condition).mapOrElse(id -> "{\"" + id + "\"}", error -> "{\"" + fallbackName.apply(condition) + "\"}");

		Reporter reporter = new Reporter(path);
		Context.Builder contextBuilder = builderOperator.apply(new Context.Builder());

		Context.Validator validator = new Context.Validator(contextBuilder.toKeySet(), reporter).withResolver(source.registryAccess());
		condition.validate(validator);

		var validationException = reporter.getErrorsFlattened()
			.map(error -> Component.literal("Found errors while validating ").append(asDisplayString(false)).append(" ").append(error))
			.map(MiscUtil::createCommandException);

		if (validationException.isPresent()) {
			throw validationException.get();
		}

		reporter = new Reporter(path);
		boolean result = condition.test(contextBuilder
			.withReporter(reporter)
			.build(source.getLevel()));

		var executionException = reporter.getErrorsFlattened()
			.map(error -> Component.literal("Found errors while testing ").append(asDisplayString(false)).append(" ").append(error))
			.map(MiscUtil::createCommandException);

		if (executionException.isPresent()) {
			throw executionException.get();
		}

		return result;

	}

	@FunctionalInterface
	interface CommandBuilder {

		ArgumentBuilder<CommandSourceStack, ?> addArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive);

		default ArgumentBuilder<CommandSourceStack, ?> optionallyAddForkedConditionedLogic(Optional<CommandNode<CommandSourceStack>> rootNode, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive, ExecuteCommand.CommandPredicate condition) {

			if (rootNode.isPresent()) {
				return ExecuteCommandAccessor.callAddConditional(rootNode.get(), builder, positive, condition);
			}

			else {

				Command<CommandSourceStack> command = context -> {

					if (positive == condition.test(context)) {
						context.getSource().sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
						return 1;
					}

					else {
						throw MiscUtil.createCommandException(Component.translatable("commands.execute.conditional.fail"));
					}

				};

				return builder.executes(command);

			}

		}

	}

}
