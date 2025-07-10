package io.github.eggohito.neo_apoli.action.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.command.argument.ActionArgumentType;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
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

import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;

public final class BiEntityActionCategory extends ActionCategory<BiEntityAction> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = actionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder) {
			return builder.then(
				argument("actor", EntityArgumentType.entity())
					.then(argument("target", EntityArgumentType.entity())
						.executes(this::execute))
			);
		}

		public int execute(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();

			Entity actor = EntityArgumentType.getEntity(commandContext, "actor");
			Entity target = EntityArgumentType.getEntity(commandContext, "target");

			BiEntityAction biEntityAction = ActionArgumentType.getAction(commandContext, actionKey);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ActionManager.getIdAsResult(biEntityAction).mapOrElse(Identifier::toString, error -> biEntityAction.toString()) + "}")
				.withContextType(ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY))
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandSource.getServer().getReloadableRegistries()).getRegistries());

			Context context = new Context.Builder(reporter.getContextType())
				.withReporter(reporter)
				.add(ContextParameters.THIS_ENTITY, actor)
				.add(ContextParameters.POSITION, actor.getPos())
				.add(ContextParameters.ACTOR, actor)
				.add(ContextParameters.TARGET, target)
				.build(commandSource.getWorld());

			biEntityAction.validate(reporter);

			if (reporter.hasAnyErrors()) {
				commandSource.sendError(Text.literal("Error validating bi-entity action due to error(s) " + reporter.getErrorsAsString()));
				return 0;
			}

			else {

				biEntityAction.execute(context);

				if (reporter.hasAnyErrors()) {
					commandSource.sendError(Text.literal("Error(s) while executing bi-entity action " + reporter.getErrorsAsString()));
					return 0;
				}

				else {
					commandSource.sendFeedback(() -> Text.literal("Successfully executed bi-entity action!"), true);
					return 1;
				}

			}

		}

	};

	BiEntityActionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<BiEntityAction>> registryRef() {
		return NeoApoliRegistryKeys.BIENTITY_ACTION;
	}

	@Override
	public Function<String, CommandBuilder> commandBuilderFactory() {
		return BUILDER_FACTORY;
	}

	@Override
	public Codec<BiEntityAction> baseCodec() {
		return BiEntityAction.CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, BiEntityAction> basePacketCodec() {
		return BiEntityAction.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Bi-entity action";
	}

}
