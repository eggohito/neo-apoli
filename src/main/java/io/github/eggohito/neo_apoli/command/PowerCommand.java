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
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.entity.MutablePowers;
import io.github.eggohito.neo_apoli.power.entity.Powers;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;

import java.util.List;
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

		baseNode.addChild(GrantArgument.node());
		baseNode.addChild(RevokeArgument.node());
		baseNode.addChild(RemoveArgument.node());
		baseNode.addChild(ClearArgument.node());
		baseNode.addChild(ListArgument.node());
		baseNode.addChild(DumpArgument.node());

		rootNode.addChild(baseNode);

	}

	static final class GrantArgument {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("grant")
				.then(argument("targets", EntityArgument.entities())
					.then(argument("power", PowerArgument.powerOrTag())
						.executes(GrantArgument::withDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(GrantArgument::withSpecificSource))));

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
			List<Entity> targets = new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets"));
			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();

			PowerArgument.Result result = PowerArgument.getResult(commandContext, "power");

			List<PowerHolder<?>> holders = result.get();
			long totalGrantedPowers = 0;

			for (var target : targets) {

				try (MutablePowers mutable = MutablePowers.create(target)) {

					long grantedPowers = 0;

					for (var holder : holders) {

						if (mutable.grant(holder.id(), source)) {
							grantedPowers++;
						}

					}

					if (grantedPowers > 0) {
						processedTargets.put(target, grantedPowers);
					}

					totalGrantedPowers += grantedPowers;

				}

			}

			switch (result) {
				case PowerArgument.Result.Singleton singleton -> {

					HoverEvent hoverEvent = new HoverEvent.ShowText(Component.nullToEmpty(singleton.id().toString()));
					Component powerName = holders.getFirst().name().copy().withStyle(style -> style.withHoverEvent(hoverEvent));

					if (processedTargets.isEmpty()) {

						if (targets.size() == 1) {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.grant.single_power.fail.single_entity", targets.getFirst().getName(), powerName, source));
						}

						else {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.grant.single_power.fail.multiple_entities", targets.size(), powerName, source));
						}

					}

					else {

						if (processedTargets.size() == 1) {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.grant.single_power.success.single_entity", processedTargets.keySet().iterator().next().getName(), powerName, source), true);
						}

						else {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.grant.single_power.success.multiple_entities", processedTargets.size(), powerName, source), true);
						}

					}

				}
				case PowerArgument.Result.Collection collection -> {

					if (processedTargets.isEmpty()) {

						if (targets.size() == 1) {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.grant.multiple_powers.fail.single_entity", targets.getFirst().getName(), collection.id(), source));
						}

						else {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.grant.multiple_powers.fail.multiple_entities", targets.size(), collection.id(), source));
						}

					}

					else {

						var processedTarget = processedTargets.object2LongEntrySet().iterator().next();
						long finalTotalGrantedPowers = totalGrantedPowers;

						if (processedTargets.size() == 1) {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.grant.multiple_powers.success.single_entity", processedTarget.getKey().getName(), processedTarget.getLongValue(), collection.id(), source), true);
						}

						else {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.grant.multiple_powers.success.multiple_entities", processedTargets.size(), finalTotalGrantedPowers, collection.id(), source), true);
						}

					}

				}
			}

			return processedTargets.size();

		}

	}

	static final class RevokeArgument {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("revoke")
				.then(argument("targets", EntityArgument.entities())
					.then(literal("all")
						.executes(RevokeArgument::allFromDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(RevokeArgument::allFromSpecificSource)))
					.then(argument("power", PowerArgument.powerOrTag())
						.executes(RevokeArgument::oneFromDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(RevokeArgument::oneFromSpecificSource))));

			return node.build();

		}

		static int allFromDefaultSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return executeAll(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				DEFAULT_SOURCE
			);
		}

		static int allFromSpecificSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return executeAll(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				ResourceLocationArgument.getId(commandContext, "source")
			);
		}

		static int oneFromDefaultSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				PowerArgument.getResult(commandContext, "power"),
				DEFAULT_SOURCE
			);
		}

		static int oneFromSpecificSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				PowerArgument.getResult(commandContext, "power"),
				ResourceLocationArgument.getId(commandContext, "source")
			);
		}

		static int executeAll(CommandContext<CommandSourceStack> commandContext, List<Entity> targets, ResourceLocation source) {

			CommandSourceStack commandSource = commandContext.getSource();
			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();

			for (var target : targets) {

				if (!Powers.has(target)) {
					continue;
				}

				try (MutablePowers mutable = MutablePowers.create(target)) {

					var holders = mutable.getAllFromSource(source);
					long revokedPowers = 0;

					for (var holder : holders) {

						if (mutable.revoke(holder.id(), source)) {
							revokedPowers++;
						}

					}

					if (revokedPowers > 0) {
						processedTargets.put(target, revokedPowers);
					}

				}

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.all.fail.single_entity", targets.getFirst().getName(), source));
				}

				else {
					commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.all.fail.multiple_entities", targets.size(), source));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					var processedEntry = processedTargets.object2LongEntrySet().iterator().next();
					commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.revoke.all.success.single_entity", processedEntry.getKey(), processedEntry.getLongValue(), source), true);
				}

				else {

					long totalRevokedPowers = processedTargets.values()
						.longStream()
						.sum();

					commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.revoke.all.success.multiple_entities", processedTargets.size(), totalRevokedPowers, source), true);

				}

			}

			return processedTargets.size();

		}

		static int execute(CommandContext<CommandSourceStack> commandContext, List<Entity> targets, PowerArgument.Result result, ResourceLocation source) throws CommandSyntaxException {

			CommandSourceStack commandSource = commandContext.getSource();
			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();

			List<PowerHolder<?>> holders = result.get();
			long totalRevokedPowers = 0;

			for (var target : targets) {

				if (!Powers.has(target)) {
					continue;
				}

				try (MutablePowers mutable = MutablePowers.create(target)) {

					long revokedPowers = 0;

					for (var holder : holders) {

						if (mutable.revoke(holder.id(), source)) {
							revokedPowers++;
						}

					}

					if (revokedPowers > 0) {
						processedTargets.put(target, revokedPowers);
					}

					totalRevokedPowers += revokedPowers;

				}

			}

			switch (result) {
				case PowerArgument.Result.Singleton(PowerIdentifier id) -> {

					HoverEvent hoverEvent = new HoverEvent.ShowText(Component.nullToEmpty(id.toString()));
					Component powerName = holders.getFirst().name().copy().withStyle(style -> style.withHoverEvent(hoverEvent));

					if (processedTargets.isEmpty()) {

						if (targets.size() == 1) {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.single_power.fail.single_entity", targets.getFirst().getName(), powerName, source));
						}

						else {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.single_power.fail.multiple_entities", targets.size(), powerName, source));
						}

					}

					else {

						if (processedTargets.size() == 1) {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.revoke.single_power.success.single_entity", processedTargets.keySet().iterator().next().getName(), powerName, source), true);
						}

						else {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.revoke.single_power.success.multiple_entities", processedTargets.size(), powerName, source), true);
						}

					}

				}
				case PowerArgument.Result.Collection(TagKey<PowerHolder<?>> tag) -> {

					Component tagName = Component.literal("#" + tag.location());

					if (processedTargets.isEmpty()) {

						if (targets.size() == 1) {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.multiple_powers.fail.single_entity", targets.getFirst().getName(), tagName, source));
						}

						else {
							commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.revoke.multiple_powers.fail.multiple_entities", targets.size(), tagName, source));
						}

					}

					else {

						var processedTarget = processedTargets.object2LongEntrySet().iterator().next();
						long finalTotalRevokedPowers = totalRevokedPowers;

						if (processedTargets.size() == 1) {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.revoke.multiple_powers.success.single_entity", processedTarget.getKey().getName(), processedTarget.getLongValue(), tagName, source), true);
						}

						else {
							commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.revoke.multiple_powers.success.multiple_entities", processedTargets.size(), finalTotalRevokedPowers, tagName, source), true);
						}

					}

				}
			}

			return processedTargets.size();

		}

	}

	static final class RemoveArgument {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("remove")
				.then(argument("targets", EntityArgument.entities())
					.then(argument("power", PowerArgument.power())
						.executes(RemoveArgument::execute)));

			return node.build();

		}

		static int execute(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			List<Entity> targets = new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets"));
			List<Entity> processedTargets = new ObjectArrayList<>();

			PowerHolder<?> holder = PowerArgument.getPower(commandContext, "power");
			CommandSourceStack commandSource = commandContext.getSource();

			Component powerName = holder.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(holder.toString()))));

			for (Entity target : targets) {

				if (!Powers.has(target)) {
					continue;
				}

				try (MutablePowers mutable = MutablePowers.create(target)) {

					Set<ResourceLocation> sources = mutable.getSources(holder.id());
					long removedPowers = 0;

					for (var source : sources) {

						if (mutable.revoke(holder.id(), source)) {
							removedPowers++;
						}

					}

					if (removedPowers > 0) {
						processedTargets.add(target);
					}

				}

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.remove.fail.single_entity", targets.getFirst().getName(), powerName));
				}

				else {
					commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.remove.fail.multiple_entities", targets.size(), powerName));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.remove.success.single_entity", processedTargets.getFirst().getName(), powerName), true);
				}

				else {
					commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.remove.success.multiple_entities", processedTargets.size(), powerName), true);
				}

			}

			return processedTargets.size();

		}

	}

	static final class ClearArgument {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("clear")
				.executes(ClearArgument::fromSelf)
				.then(argument("targets", EntityArgument.entities())
					.executes(ClearArgument::fromSpecified));

			return node.build();

		}

		static int fromSelf(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, ObjectArrayList.of(commandContext.getSource().getEntityOrException()));
		}

		static int fromSpecified(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")));
		}

		static int execute(CommandContext<CommandSourceStack> commandContext, List<Entity> targets) {

			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();
			CommandSourceStack commandSource = commandContext.getSource();

			for (Entity target : targets) {

				if (!Powers.has(target)) {
					continue;
				}

				try (MutablePowers mutable = MutablePowers.create(target)) {

					long clearedPowers = 0;

					for (var holder : mutable.getAll()) {

						for (var source : mutable.getSources(holder.id())) {

							if (mutable.revoke(holder.id(), source)) {
								clearedPowers++;
							}

						}

					}

					if (clearedPowers > 0) {
						processedTargets.put(target, clearedPowers);
					}

				}

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.clear.fail.single", targets.getFirst().getName()));
				}

				else {
					commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.clear.fail.multiple", targets.size()));
				}

			}

			else {

				if (processedTargets.size() == 1) {

					var processedTarget = processedTargets.object2LongEntrySet().iterator().next();

					commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.clear.success.single", processedTarget.getKey().getName(), processedTarget.getLongValue()), true);

				}

				else {

					long totalClearedPowers = processedTargets.values()
						.longStream()
						.count();

					commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.clear.success.multiple", processedTargets.size(), totalClearedPowers), true);

				}

			}

			return processedTargets.size();

		}

	}

	static final class ListArgument {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("list")
				.executes(ListArgument::fromSelf)
				.then(argument("target", EntityArgument.entity())
					.executes(ListArgument::fromSpecified)
					.then(argument("includeSubPowers", BoolArgumentType.bool())
						.executes(ListArgument::fromSpecifiedWithSubPowerOption)));

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

			List<PowerHolder<?>> powerHolders = Optional.ofNullable(powers).map(self -> self.getAll(includeSubPowers)).orElseGet(ObjectArrayList::new);
			List<Component> powerTooltips = new ObjectArrayList<>();

			for (var powerHolder : powerHolders) {

				if (powers == null) {
					break;
				}

				Power power = powerHolder.value();
				Power.Type<?> type = power.getType();

				List<Component> sourceTooltips = powers.getSources(powerHolder.id())
					.stream()
					.map(Objects::toString)
					.map(source -> Component.literal(source).withStyle())
					.collect(Collectors.toCollection(ObjectArrayList::new));

				Component idTooltip = Component.translatableEscape("commands.neo-apoli.power.list.info.id", Component.literal("\"" + powerHolder.id().toString() + "\"").withStyle(ChatFormatting.GREEN));
				Component typeTooltip = Component.translatableEscape("commands.neo-apoli.power.list.info.type", Component.literal("\"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, type) + "\"").withStyle(ChatFormatting.GOLD));
				Component joinedSourcesTooltip = Component.translatableEscape("commands.neo-apoli.power.list.info.sources", ComponentUtils.formatList(sourceTooltips, Component.nullToEmpty(", ")));

				Component hoverTooltip = Component.translatable("commands.neo-apoli.power.list.info", idTooltip, typeTooltip, joinedSourcesTooltip);
				HoverEvent hoverEvent = new HoverEvent.ShowText(hoverTooltip);

				powerTooltips.add(powerHolder.name().copy().withStyle(style -> style.withHoverEvent(hoverEvent)));

			}

			if (powerTooltips.isEmpty()) {
				commandSource.sendFailure(Component.translatableEscape("commands.neo-apoli.power.list.fail", target.getName()));
			}

			else {
				commandSource.sendSuccess(() -> Component.translatableEscape("commands.neo-apoli.power.list.success", target.getName(), powerTooltips.size(), ComponentUtils.formatList(powerTooltips, Component.nullToEmpty(", "))), false);
			}

			return powerTooltips.size();

		}

	}

	static final class DumpArgument {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("dump")
				.then(argument("power", PowerArgument.power())
					.executes(DumpArgument::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(DumpArgument::withSpecificIndent)));

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
