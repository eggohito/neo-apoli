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
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PowerCommand {

	public static final ResourceLocation DEFAULT_SOURCE = NeoApoli.id("command");

	public static void register(CommandNode<CommandSourceStack> rootNode) {

		CommandNode<CommandSourceStack> baseNode = literal("power")
			.requires(source -> source.hasPermission(2))
			.build();

		baseNode.addChild(Grant.node());
		baseNode.addChild(Revoke.node());
		baseNode.addChild(Remove.node());
		baseNode.addChild(Clear.node());
		baseNode.addChild(List.node());
		baseNode.addChild(Dump.node());

		rootNode.addChild(baseNode);

	}

	static final class Grant {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("grant")
				.then(argument("targets", EntityArgument.entities())
					.then(argument("power", PowerArgument.powerOrTag())
						.executes(Grant::withDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(Grant::withSpecificSource))));

			return node.build();

		}

		static int withDefaultSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, DEFAULT_SOURCE);
		}

		static int withSpecificSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, ResourceLocationArgument.getId(commandContext, "source"));
		}

		static int execute(CommandContext<CommandSourceStack> commandContext, ResourceLocation source) throws CommandSyntaxException {

			CommandSourceStack commandSource = commandContext.getSource();

			java.util.List<Entity> targets = new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets"));
			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();

			PowerArgument.Result result = PowerArgument.getArgument(commandContext, "power");
			java.util.List<PowerHolder<?>> powerHolders = result.get();

			for (var target : targets) {

				Powers powers = Powers.getOrCreate(target);
				long grantedPowers = powerHolders
					.stream()
					.filter(entry -> powers.grantWithCallback(entry.id(), source))
					.count();

				if (grantedPowers <= 0) {
					continue;
				}

				processedTargets.put(target, grantedPowers);
				powers.update();

			}

			switch (result) {
				case PowerArgument.Result.Singleton singleton -> {

					HoverEvent hoverEvent = new HoverEvent.ShowText(Component.nullToEmpty(singleton.id().toString()));
					Component powerName = powerHolders.getFirst().name().copy().withStyle(style -> style.withHoverEvent(hoverEvent));

					if (processedTargets.isEmpty()) {

						if (targets.size() == 1) {
							commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.grant.single_power.fail.single_entity", targets.getFirst().getName(), powerName, source.toString()));
						}

						else {
							commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.grant.single_power.fail.multiple_entities", targets.size(), powerName, source.toString()));
						}

					}

					else {

						if (processedTargets.size() == 1) {
							commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.grant.single_power.success.single_entity", processedTargets.keySet().iterator().next().getName(), powerName, source.toString()), true);
						}

						else {
							commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.grant.single_power.success.multiple_entities", processedTargets.size(), powerName, source.toString()), true);
						}

					}

				}
				case PowerArgument.Result.Collection collection -> {

					if (processedTargets.isEmpty()) {

						if (targets.size() == 1) {
							commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.grant.multiple_powers.fail.single_entity", targets.getFirst().getName(), collection.id().toString(), source.toString()));
						}

						else {
							commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.grant.multiple_powers.fail.multiple_entities", targets.size(), collection.id().toString(), source.toString()));
						}

					}

					else {

						if (processedTargets.size() == 1) {
							var processedTarget = processedTargets.object2LongEntrySet().iterator().next();
							commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.grant.multiple_powers.success.single_entity", processedTarget.getKey().getName(), processedTarget.getLongValue(), collection.id().toString(), source.toString()), true);
						}

						else {

							long totalGrantedPowers = processedTargets.values()
								.longStream()
								.sum();

							commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.grant.multiple_powers.success.multiple_entities", processedTargets.size(), collection.id().toString(), totalGrantedPowers, source.toString()), true);

						}

					}

				}
			}

			return processedTargets.size();

		}

	}

	static final class Revoke {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("revoke")
				.then(argument("targets", EntityArgument.entities())
					.then(literal("all")
						.executes(Revoke::allFromDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(Revoke::allFromSpecificSource)))
					.then(argument("power", PowerArgument.power())
						.executes(Revoke::oneFromDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(Revoke::oneFromSpecificSource))));

			return node.build();

		}

		static int allFromDefaultSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				null,
				DEFAULT_SOURCE
			);
		}

		static int allFromSpecificSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				null,
				ResourceLocationArgument.getId(commandContext, "source")
			);
		}

		static int oneFromDefaultSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				PowerArgument.getPower(commandContext, "power"),
				DEFAULT_SOURCE
			);
		}

		static int oneFromSpecificSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				PowerArgument.getPower(commandContext, "power"),
				ResourceLocationArgument.getId(commandContext, "source")
			);
		}

		static int execute(CommandContext<CommandSourceStack> commandContext, java.util.List<Entity> targets, @Nullable PowerHolder<?> powerHolder, ResourceLocation source) {

			CommandSourceStack commandSource = commandContext.getSource();
			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();

			for (Entity target : targets) {

				if (!Powers.has(target)) {
					continue;
				}

				Powers powers = Powers.getOrCreate(target);
				long revokedPowers;

				if (powerHolder != null) {

					if (powers.revokeWithCallback(powerHolder.id(), source)) {
						revokedPowers = 1;
					}

					else {
						revokedPowers = 0;
					}

				}

				else {
					revokedPowers = powers.getAllFromSource(source)
						.stream()
						.filter(inner -> powers.revokeWithCallback(inner.id(), source))
						.count();
				}

				if (revokedPowers <= 0) {
					continue;
				}

				processedTargets.put(target, revokedPowers);
				powers.update();

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {

					if (powerHolder != null) {

						PowerIdentifier powerId = powerHolder.id();
						Component powerName = powerHolder.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(powerId.toString()))));

						commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.revoke.fail.single", targets.getFirst().getName(), powerName, source));

					}

					else {
						commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.revoke.all.fail.single", targets.getFirst().getName(), source.toString()));
					}

				}

				else {

					if (powerHolder != null) {

						PowerIdentifier powerId = powerHolder.id();
						Component powerName = powerHolder.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(powerId.toString()))));

						commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.fail.multiple", targets.size(), powerName, source.toString()));

					}

					else {
						commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.all.fail.multiple", targets.size(), source));
					}

				}

			}

			else {

				if (processedTargets.size() == 1) {

					var processedTarget = processedTargets.object2LongEntrySet().iterator().next();
					if (powerHolder != null) {

						PowerIdentifier powerId = powerHolder.id();
						Component powerName = powerHolder.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(powerId.toString()))));

						commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.revoke.success.single", processedTarget.getKey().getName(), powerName, source.toString()), true);

					}

					else {
						commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.revoke.all.success.single", processedTarget.getKey().getName(), processedTarget.getLongValue(), source.toString()), true);
					}

				}

				else {

					if (powerHolder != null) {

						PowerIdentifier powerId = powerHolder.id();
						Component powerName = powerHolder.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(powerId.toString()))));

						commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.revoke.success.multiple", processedTargets.size(), powerName, source.toString()), true);

					}

					else {

						long totalRevokedPowers = processedTargets.values()
							.longStream()
							.sum();

						commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.revoke.all.success.multiple", processedTargets.size(), totalRevokedPowers, source), true);

					}

				}

			}

			return processedTargets.size();

		}

	}

	static final class Remove {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("remove")
				.then(argument("targets", EntityArgument.entities())
					.then(argument("power", PowerArgument.power())
						.executes(Remove::execute)));

			return node.build();

		}

		static int execute(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			java.util.List<Entity> targets = new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets"));
			java.util.List<Entity> processedTargets = new ObjectArrayList<>();

			PowerHolder<?> powerHolder = PowerArgument.getPower(commandContext, "power");
			CommandSourceStack commandSource = commandContext.getSource();

			Component powerName = powerHolder.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(powerHolder.toString()))));

			for (Entity target : targets) {

				if (!Powers.has(target)) {
					continue;
				}

				Powers powers = Powers.getOrCreate(target);
				Set<ResourceLocation> sources = powers.getSources(powerHolder.id());

				long removedPowers = sources
					.stream()
					.filter(source -> powers.revokeWithCallback(powerHolder.id(), source))
					.count();

				if (removedPowers <= 0) {
					continue;
				}

				processedTargets.add(target);
				powers.update();

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.remove.fail.single", targets.getFirst().getName(), powerName));
				}

				else {
					commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.remove.fail.multiple", targets.size(), powerName));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.remove.success.single", processedTargets.getFirst().getName(), powerName), true);
				}

				else {
					commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.remove.success.multiple", processedTargets.size(), powerName), true);
				}

			}

			return processedTargets.size();

		}

	}

	static final class Clear {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("clear")
				.executes(Clear::fromSelf)
				.then(argument("targets", EntityArgument.entities())
					.executes(Clear::fromSpecified));

			return node.build();

		}

		static int fromSelf(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, ObjectArrayList.of(commandContext.getSource().getEntityOrException()));
		}

		static int fromSpecified(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")));
		}

		static int execute(CommandContext<CommandSourceStack> commandContext, java.util.List<Entity> targets) {

			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();
			CommandSourceStack commandSource = commandContext.getSource();

			for (Entity target : targets) {

				Powers powers = Powers.getNullable(target);
				long clearedPowers = 0;

				if (powers == null) {
					continue;
				}

				for (var entry : powers.getAll()) {

					for (var source : powers.getSources(entry.id())) {

						if (powers.revokeWithCallback(entry.id(), source)) {
							clearedPowers++;
						}

					}

				}

				if (clearedPowers <= 0) {
					continue;
				}

				processedTargets.put(target, clearedPowers);
				powers.update();

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.clear.fail.single", targets.getFirst().getName()));
				}

				else {
					commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.clear.fail.multiple", targets.size()));
				}

			}

			else {

				if (processedTargets.size() == 1) {

					var processedTarget = processedTargets.object2LongEntrySet().iterator().next();

					commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.clear.success.single", processedTarget.getKey().getName(), processedTarget.getLongValue()), true);

				}

				else {

					long totalClearedPowers = processedTargets.values()
						.longStream()
						.count();

					commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.clear.success.multiple", processedTargets.size(), totalClearedPowers), true);

				}

			}

			return processedTargets.size();

		}

	}

	static final class List {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("list")
				.executes(List::fromSelf)
				.then(argument("target", EntityArgument.entity())
					.executes(List::fromSpecified)
					.then(argument("includeSubPowers", BoolArgumentType.bool())
						.executes(List::fromSpecifiedWithSubPowerOption)));

			return node.build();

		}

		static int fromSelf(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, commandContext.getSource().getEntityOrException(), false);
		}

		static int fromSpecified(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, EntityArgument.getEntity(commandContext, "target"), false);
		}

		static int fromSpecifiedWithSubPowerOption(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, EntityArgument.getEntity(commandContext, "target"), BoolArgumentType.getBool(commandContext, "includeSubPowers"));
		}

		static int execute(CommandContext<CommandSourceStack> commandContext, Entity target, boolean includeSubPowers) {

			Powers powers = Powers.getNullable(target);
			CommandSourceStack commandSource = commandContext.getSource();

			java.util.List<PowerHolder<?>> powerHolders = Optional.ofNullable(powers).map(self -> self.getAll(includeSubPowers)).orElseGet(ObjectArrayList::new);
			java.util.List<Component> powerTooltips = new ObjectArrayList<>();

			for (var powerHolder : powerHolders) {

				if (powers == null) {
					break;
				}

				Power power = powerHolder.value();
				Power.Type<?> type = power.getType();

				java.util.List<Component> sourceTooltips = powers.getSources(powerHolder.id())
					.stream()
					.map(Objects::toString)
					.map(source -> Component.literal(source).withStyle())
					.collect(Collectors.toCollection(ObjectArrayList::new));

				Component idTooltip = Component.translatableEscape("commands.neo-apoli.power.list.info.id", Component.literal("\"" + powerHolder.id().toString() + "\"").withStyle(ChatFormatting.GREEN));
				Component typeTooltip = Component.translatableEscape("commands.neo-apoli.power.list.info.type", Component.literal("\"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, type) + "\"").withStyle(ChatFormatting.GOLD));
				Component joinedSourcesTooltip = Component.translatable("commands.neo-apoli.power.list.info.sources", ComponentUtils.formatList(sourceTooltips, Component.nullToEmpty(", ")));

				Component hoverTooltip = Component.translatable("commands.neo-apoli.power.list.info", idTooltip, typeTooltip, joinedSourcesTooltip);
				HoverEvent hoverEvent = new HoverEvent.ShowText(hoverTooltip);

				powerTooltips.add(powerHolder.name().copy().withStyle(style -> style.withHoverEvent(hoverEvent)));

			}

			if (powerTooltips.isEmpty()) {
				commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.list.fail", target.getName()));
			}

			else {
				commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.list.success", target.getName(), powerTooltips.size(), ComponentUtils.formatList(powerTooltips, Component.nullToEmpty(", "))), false);
			}

			return powerTooltips.size();

		}

	}

	static final class Dump {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("dump")
				.then(argument("power", PowerArgument.power())
					.executes(Dump::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(Dump::withSpecificIndent)));

			return node.build();

		}

		static int withDefaultIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		static int withSpecificIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		static int execute(CommandContext<CommandSourceStack> context, int indent) throws CommandSyntaxException {

			PowerHolder<?> powerHolder = PowerArgument.getPower(context, "power");
			Power power = powerHolder.value();

			CommandSourceStack commandSource = context.getSource();
			RegistryOps<JsonElement> jsonOps = commandSource.registryAccess().createSerializationContext(JsonOps.INSTANCE);

			return switch (Power.CODEC.encodeStart(jsonOps, power)) {
				case DataResult.Success<JsonElement> success -> {

					JsonElement copy;
					if (power instanceof MultiplePower && success.value() instanceof JsonObject jsonObject) {

						JsonObject newJsonObject = new JsonObject();
						jsonObject.asMap().forEach((key, value) -> {

							String newKey = key.substring(key.indexOf(PowerIdentifier.SEPARATOR) + 1);

							if (value instanceof JsonObject valueObject) {
								valueObject.remove(PowerHolder.ID_KEY);
							}

							newJsonObject.add(newKey, value);

						});

						copy = newJsonObject;

					}

					else {
						copy = success.value();
					}

					commandSource.sendSuccess(() -> JsonTextFormatter.format(copy, indent), false);
					yield 1;

				}
				case DataResult.Error<JsonElement> error -> {
					commandSource.sendFailure(Component.literal(error.message()));
					yield 0;
				}
			};

		}

	}

}
