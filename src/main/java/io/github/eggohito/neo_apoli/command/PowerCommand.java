package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.command.argument.PowerReferenceArgumentType;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PowerCommand {

	public static final Identifier DEFAULT_SOURCE = NeoApoli.id("command");
	public static final CommandNode<ServerCommandSource> NODE = literal("power")
		.requires(commandSource -> commandSource.hasPermissionLevel(2))
		.build();

	public static void register(CommandNode<ServerCommandSource> baseNode) {

		NODE.addChild(GrantSubCommand.NODE);
		NODE.addChild(RevokeSubCommand.NODE);
		NODE.addChild(RemoveSubCommand.NODE);
		NODE.addChild(ClearSubCommand.NODE);
		NODE.addChild(ListSubCommand.NODE);
		NODE.addChild(DumpSubCommand.NODE);

		baseNode.addChild(NODE);

	}

	static final class GrantSubCommand {

		static final CommandNode<ServerCommandSource> NODE = literal("grant")
			.then(argument("targets", EntityArgumentType.entities())
				.then(argument("power", PowerReferenceArgumentType.powerReference())
					.executes(GrantSubCommand::withDefaultSource)
					.then(argument("source", IdentifierArgumentType.identifier())
						.executes(GrantSubCommand::withSpecificSource))))
			.build();

		static int withDefaultSource(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, DEFAULT_SOURCE);
		}

		static int withSpecificSource(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "source"));
		}

		static int execute(CommandContext<ServerCommandSource> commandContext, Identifier source) throws CommandSyntaxException {

			List<Entity> targets = new ObjectArrayList<>(EntityArgumentType.getEntities(commandContext, "targets"));
			List<Entity> processedTargets = new ObjectArrayList<>();

			PowerReference reference = PowerReferenceArgumentType.getExistingPowerReference(commandContext, "power");

			Power power = PowerManager.get(reference);
			Text powerName = power.getName().copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.of(reference.toString()))));

			Map<Identifier, Collection<PowerReference>> grantedPowers = Map.of(source, List.of(reference));
			ServerCommandSource commandSource = commandContext.getSource();

			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				if (!powersComponent.grantPower(reference, source)) {
					continue;
				}

				processedTargets.add(target);
				PowersComponent.Synchronizer.GRANT.sync(target, grantedPowers);

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.grant.fail.single", targets.getFirst().getName(), powerName, source.toString()));
				}

				else {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.grant.fail.multiple", targets.size(), powerName, source.toString()));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.grant.success.single", processedTargets.getFirst().getName(), powerName, source.toString()), true);
				}

				else {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.grant.success.multiple", processedTargets.size(), powerName, source.toString()), true);
				}

			}

			return processedTargets.size();

		}

	}

	static final class RevokeSubCommand {

		static final CommandNode<ServerCommandSource> NODE = literal("revoke")
			.then(argument("targets", EntityArgumentType.entities())
				.then(literal("all")
					.executes(RevokeSubCommand::allFromDefaultSource)
					.then(argument("source", IdentifierArgumentType.identifier())
						.executes(RevokeSubCommand::allFromSpecificSource)))
				.then(argument("power", PowerReferenceArgumentType.powerReference())
					.executes(RevokeSubCommand::oneFromDefaultSource)
					.then(argument("source", IdentifierArgumentType.identifier())
						.executes(RevokeSubCommand::oneFromSpecificSource))))
			.build();

		static int allFromDefaultSource(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgumentType.getEntities(commandContext, "targets")),
				null,
				DEFAULT_SOURCE
			);
		}

		static int allFromSpecificSource(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgumentType.getEntities(commandContext, "targets")),
				null,
				IdentifierArgumentType.getIdentifier(commandContext, "source")
			);
		}

		static int oneFromDefaultSource(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgumentType.getEntities(commandContext, "targets")),
				PowerReferenceArgumentType.getExistingPowerReference(commandContext, "power"),
				DEFAULT_SOURCE
			);
		}

		static int oneFromSpecificSource(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgumentType.getEntities(commandContext, "targets")),
				PowerReferenceArgumentType.getExistingPowerReference(commandContext, "power"),
				IdentifierArgumentType.getIdentifier(commandContext, "source")
			);
		}

		static int execute(CommandContext<ServerCommandSource> commandContext, List<Entity> targets, @Nullable PowerReference powerReference, Identifier source) {

			ServerCommandSource commandSource = commandContext.getSource();
			List<Entity> processedTargets = new ObjectArrayList<>();

			AtomicInteger revokedPowersCount = new AtomicInteger();

			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				Map<Identifier, Collection<PowerReference>> revokedPowers = new Object2ObjectOpenHashMap<>();

				if (powerReference != null) {

					if (powersComponent.revokePower(powerReference, source)) {
						revokedPowers.put(source, List.of(powerReference));
					}

				}

				else {

					Set<PowerReference> matchingReferences = powersComponent.getReferences(source);
					for (var matchingReference : matchingReferences) {
						powersComponent.revokePower(matchingReference, source);
					}

					if (!matchingReferences.isEmpty()) {
						revokedPowers.put(source, matchingReferences);
					}

				}

				if (!revokedPowers.isEmpty()) {

					processedTargets.add(target);
					PowersComponent.Synchronizer.REVOKE.sync(target, revokedPowers);

					revokedPowersCount.addAndGet(revokedPowers.size());

				}

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {

					if (powerReference != null) {

						Power power = PowerManager.get(powerReference);
						Text powerName = power.getName().copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(powerReference.toString()))));

						commandSource.sendError(Text.translatable("commands.neo-apoli.power.revoke.fail.single", targets.getFirst().getName(), powerName, source));

					}

					else {
						commandSource.sendError(Text.translatable("commands.neo-apoli.power.revoke.all.fail.single", targets.getFirst().getName(), source.toString()));
					}

				}

				else {

					if (powerReference != null) {

						Power power = PowerManager.get(powerReference);
						Text powerName = power.getName().copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(powerReference.toString()))));

						commandSource.sendError(Text.stringifiedTranslatable("commands.neo-apoli.power.revoke.fail.multiple", targets.size(), powerName, source.toString()));

					}

					else {
						commandSource.sendError(Text.stringifiedTranslatable("commands.neo-apoli.power.revoke.all.fail.multiple", targets.size(), source));
					}

				}

			}

			else {

				if (processedTargets.size() == 1) {

					if (powerReference != null) {

						Power power = PowerManager.get(powerReference);
						Text powerName = power.getName().copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(powerReference.toString()))));

						commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.revoke.success.single", processedTargets.getFirst().getName(), powerName, source.toString()), true);

					}

					else {
						commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.revoke.all.success.single", processedTargets.getFirst().getName(), revokedPowersCount.get(), source.toString()), true);
					}

				}

				else {

					if (powerReference != null) {

						Power power = PowerManager.get(powerReference);
						Text powerName = power.getName().copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(powerReference.toString()))));

						commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.revoke.success.multiple", processedTargets.size(), powerName, source.toString()), true);

					}

					else {
						commandSource.sendFeedback(() -> Text.stringifiedTranslatable("commands.neo-apoli.power.revoke.all.success.multiple", processedTargets.size(), revokedPowersCount.get(), source), true);
					}

				}

			}

			return processedTargets.size();

		}

	}

	static final class RemoveSubCommand {

		static final CommandNode<ServerCommandSource> NODE = literal("remove")
			.then(argument("targets", EntityArgumentType.entities())
				.then(argument("power", PowerReferenceArgumentType.powerReference())
					.executes(RemoveSubCommand::execute)))
			.build();

		static int execute(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			List<Entity> targets = new ObjectArrayList<>(EntityArgumentType.getEntities(commandContext, "targets"));
			List<Entity> processedTargets = new ObjectArrayList<>();

			PowerReference reference = PowerReferenceArgumentType.getExistingPowerReference(commandContext, "power");
			List<PowerReference> references = List.of(reference);

			ServerCommandSource commandSource = commandContext.getSource();
			Text powerName = PowerManager.get(reference).getName().copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(reference.toString()))));

			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				Map<Identifier, Collection<PowerReference>> removedPowers = new Object2ObjectOpenHashMap<>();

				for (Identifier source : powersComponent.getSources(reference)) {

					if (powersComponent.revokePower(reference, source)) {
						removedPowers.put(source, references);
					}

				}

				if (!removedPowers.isEmpty()) {
					processedTargets.add(target);
					PowersComponent.Synchronizer.REVOKE.sync(target, removedPowers);
				}

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.remove.fail.single", targets.getFirst().getName(), powerName));
				}

				else {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.remove.fail.multiple", targets.size(), powerName));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.remove.success.single", processedTargets.getFirst().getName(), powerName), true);
				}

				else {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.remove.success.multiple", processedTargets.size(), powerName), true);
				}

			}

			return processedTargets.size();

		}

	}

	static final class ClearSubCommand {

		static final CommandNode<ServerCommandSource> NODE = literal("clear")
			.executes(ClearSubCommand::fromSelf)
			.then(argument("targets", EntityArgumentType.entities())
				.executes(ClearSubCommand::fromSpecified))
			.build();

		static int fromSelf(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, ObjectArrayList.of(commandContext.getSource().getEntityOrThrow()));
		}

		static int fromSpecified(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, new ObjectArrayList<>(EntityArgumentType.getEntities(commandContext, "targets")));
		}

		static int execute(CommandContext<ServerCommandSource> commandContext, List<Entity> targets) {

			List<Entity> processedTargets = new ObjectArrayList<>();
			ServerCommandSource commandSource = commandContext.getSource();

			AtomicInteger totalClearedPowers = new AtomicInteger(0);
			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				Map<Identifier, Collection<PowerReference>> clearedPowers = new Object2ObjectOpenHashMap<>();

				powersComponent.forEach((reference, impl, sources) -> {

					for (var source : sources) {

						if (powersComponent.revokePower(reference, source)) {
							clearedPowers.computeIfAbsent(source, k -> new ObjectOpenHashSet<>()).add(reference);
						}

					}

				});

				if (!clearedPowers.isEmpty()) {
					processedTargets.add(target);
					PowersComponent.Synchronizer.REVOKE.sync(target, clearedPowers);
				}

				totalClearedPowers.addAndGet(clearedPowers.size());

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.clear.fail.single", targets.getFirst().getName()));
				}

				else {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.clear.fail.multiple", targets.size()));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.clear.success.single", processedTargets.getFirst().getName(), totalClearedPowers.get()), true);
				}

				else {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.clear.success.multiple", processedTargets.size(), totalClearedPowers.get()), true);
				}

			}

			return totalClearedPowers.get();

		}

	}

	static final class ListSubCommand {

		static final CommandNode<ServerCommandSource> NODE = literal("list")
			.executes(ListSubCommand::fromSelf)
			.then(argument("target", EntityArgumentType.entity())
				.executes(ListSubCommand::fromSpecified)
				.then(argument("includeSubPowers", BoolArgumentType.bool())
					.executes(ListSubCommand::fromSpecifiedWithSubPowerOption)))
			.build();

		static int fromSelf(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, commandContext.getSource().getEntityOrThrow(), false);
		}

		static int fromSpecified(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, EntityArgumentType.getEntity(commandContext, "target"), false);
		}

		static int fromSpecifiedWithSubPowerOption(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, EntityArgumentType.getEntity(commandContext, "target"), BoolArgumentType.getBool(commandContext, "includeSubPowers"));
		}

		static int execute(CommandContext<ServerCommandSource> commandContext, Entity target, boolean includeSubPowers) {

			PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
			ServerCommandSource commandSource = commandContext.getSource();

			List<Text> powerTooltips = new ObjectArrayList<>();
			powersComponent.forEach((reference, impl, sources) -> {

				if (!includeSubPowers && reference.isSubPower()) {
					return;
				}

				Power power = impl.getPower();
				PowerType<?> type = power.getType();

				List<Text> sourceTooltips = new ObjectArrayList<>();
				sources.forEach(id -> sourceTooltips.add(Text.literal(id.toString()).formatted()));

				Text idTooltip = Text.stringifiedTranslatable("commands.neo-apoli.power.list.info.id", Text.literal("\"" + reference.toString() + "\"").formatted(Formatting.GREEN));
				Text joinedSourcesTooltip = Text.translatable("commands.neo-apoli.power.list.info.sources", Texts.join(sourceTooltips, Text.of(", ")));
				Text typeTooltip = Text.stringifiedTranslatable("commands.neo-apoli.power.list.info.type", Text.literal("\"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, type) + "\"").formatted(Formatting.GOLD));

				Text hoverTooltip = Text.translatable("commands.neo-apoli.power.list.info", idTooltip, typeTooltip, joinedSourcesTooltip);
				HoverEvent hoverEvent = new HoverEvent.ShowText(hoverTooltip);

				powerTooltips.add(power.getName().copy().styled(style -> style.withHoverEvent(hoverEvent)));

			});

			if (powerTooltips.isEmpty()) {
				commandSource.sendError(Text.translatable("commands.neo-apoli.power.list.fail", target.getName()));
			}

			else {
				commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.list.success", target.getName(), powerTooltips.size(), Texts.join(powerTooltips, Text.of(", "))), false);
			}

			return powerTooltips.size();

		}

	}

	static final class DumpSubCommand {

		static final CommandNode<ServerCommandSource> NODE = literal("dump")
			.then(argument("power", PowerReferenceArgumentType.powerReference())
				.executes(DumpSubCommand::withDefaultIndent)
				.then(argument("indent", IntegerArgumentType.integer(0))
					.executes(DumpSubCommand::withSpecificIndent)))
			.build();

		static int withDefaultIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		static int withSpecificIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		static int execute(CommandContext<ServerCommandSource> context, int indent) throws CommandSyntaxException {

			PowerReference reference = PowerReferenceArgumentType.getExistingPowerReference(context, "power");
			Power power = PowerManager.get(reference);

			ServerCommandSource commandSource = context.getSource();
			RegistryOps<JsonElement> jsonOps = commandSource.getRegistryManager().getOps(JsonOps.INSTANCE);

			return switch (Power.BASE_CODEC.encodeStart(jsonOps, power)) {
				case DataResult.Success<JsonElement> success -> {

					JsonElement copy;
					if (power instanceof MultiplePower && success.value() instanceof JsonObject jsonObject) {

						JsonObject newJsonObject = new JsonObject();
						jsonObject.asMap().forEach((key, value) -> newJsonObject.add(key.substring(key.indexOf(PowerReference.SubPower.SEPARATOR) + 1), value));

						copy = newJsonObject;

					}

					else {
						copy = success.value();
					}

					commandSource.sendFeedback(() -> JsonTextFormatter.format(copy, indent), false);
					yield 1;

				}
				case DataResult.Error<JsonElement> error -> {
					commandSource.sendError(Text.literal(error.message()));
					yield 0;
				}
			};

		}

	}

}
