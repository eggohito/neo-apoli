package io.github.eggohito.neo_apoli.action.kind.custom;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public enum BiEntityActionKind implements ActionKind<BiEntityAction> {

	INSTANCE;

	@Override
	public @NotNull Function<String, CommandBuilder> commandBuilder() {
		return actionKey -> new CommandBuilder() {

			@Override
			public ArgumentBuilder<CommandSourceStack, ?> addArguments(CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder) {
				return builder
					.then(argument("actor", EntityArgument.entity())
						.then(argument("target", EntityArgument.entity())
							.executes(this::execute)));
			}

			int execute(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
				return BiEntityActionKind.this.execute(
					commandContext,
					actionKey,
					action -> Util.getRegisteredName(NeoApoliRegistries.BIENTITY_ACTION_TYPE, action.getType()),
					builder -> builder
						.withRequired(NeoApoliContextParams.ACTOR_ENTITY, EntityArgument.getEntity(commandContext, "actor"))
						.withRequired(NeoApoliContextParams.TARGET_ENTITY, EntityArgument.getEntity(commandContext, "target"))
				);
			}

		};
	}

	@Override
	public ResourceKey<? extends Registry<BiEntityAction>> registryKey() {
		return NeoApoliRegistryKeys.BIENTITY_ACTION;
	}

	@Override
	public Codec<BiEntityAction> codec() {
		return BiEntityAction.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Bi-entity action";
	}

}
