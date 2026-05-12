package io.github.eggohito.neo_apoli.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.SequenceAction;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.command.argument.action.ActionArgument;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IKind;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.function.FailableFunction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Action extends ContextUser {

	Codec<Action> CODEC = Codec.recursive(Action.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(Action::getType, Type::mapCodec), codec.listOf().xmap(SequenceAction::new, SequenceAction::actions)));

	StreamCodec<RegistryFriendlyByteBuf, Action> STREAM_CODEC = Type.STREAM_CODEC.dispatch(Action::getType, Type::streamCodec);

	Type<?> getType();

	void execute(Context context);

	interface Kind<A extends Action> extends IKind<A>, StringDisplayable {

		Kind<Action> INSTANCE = new Kind<>() {

			@Override
			public @Nullable Function<String, CommandBuilder> commandBuilder() {
				return null;
			}

			@Override
			public ResourceKey<? extends Registry<Action>> registryKey() {
				return NeoApoliRegistryKeys.ACTION;
			}

			@Override
			public Codec<Action> codec() {
				return Action.CODEC;
			}

			@Override
			public String asDisplayString() {
				return "Action";
			}

		};

		Codec<Kind<?>> CODEC = NeoApoliRegistries.ACTION_KIND.byNameCodec();

		StreamCodec<RegistryFriendlyByteBuf, Kind<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ACTION_KIND);

		@Nullable
		Function<String, CommandBuilder> commandBuilder();

		default int execute(CommandContext<CommandSourceStack> commandContext, String actionKey, Function<A, String> fallbackName, FailableFunction<Context.Builder, Context.Builder, CommandSyntaxException> builderOperator) throws CommandSyntaxException {

			CommandSourceStack source = commandContext.getSource();

			A action = ActionArgument.getAction(commandContext, this, actionKey);
			String path = ActionManager.getIdAsResult(action).mapOrElse(id -> "{\"" + id + "\"}", error -> "{\"" + fallbackName.apply(action) + "\"}");

			Reporter reporter = new Reporter(path);
			Context.Builder contextBuilder = builderOperator.apply(new Context.Builder());

			Context.Validator validator = new Context.Validator(contextBuilder.toKeySet(), reporter).withResolver(source.registryAccess());
			action.validate(validator);

			var validationException = reporter.getErrorsFlattened()
				.map(error -> Component.literal("Found errors while validating ").append(asDisplayString(false)).append(" ").append(error))
				.map(MiscUtil::createCommandException);

			if (validationException.isPresent()) {
				throw validationException.get();
			}

			reporter = new Reporter(path);
			action.execute(contextBuilder
				.withReporter(reporter)
				.build(source.getLevel()));

			var executionException = reporter.getErrorsFlattened()
				.map(error -> Component.literal("Found errors while executing ").append(asDisplayString(false)).append(" ").append(error))
				.map(MiscUtil::createCommandException);

			if (executionException.isPresent()) {
				throw executionException.get();
			}

			source.sendSuccess(() -> Component.literal("Successfully executed ").append(asDisplayString(false)).append("!"), true);
			return 1;

		}

		@FunctionalInterface
		interface CommandBuilder {
			ArgumentBuilder<CommandSourceStack, ?> addArguments(CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder);
		}

	}

	interface Type<A extends Action> {

		FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.ACTION_TYPE);

		Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ACTION_TYPE);

		Kind<?> kind();

		MapCodec<A> mapCodec();

		StreamCodec<RegistryFriendlyByteBuf, A> streamCodec();

	}

}
