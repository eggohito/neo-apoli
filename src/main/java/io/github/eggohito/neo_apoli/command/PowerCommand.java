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
import io.github.eggohito.neo_apoli.command.argument.PowerArgumentType;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.PowerReference;
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

import java.util.List;
import java.util.Objects;
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

		baseNode.addChild(GrantSubCommand.node());
		baseNode.addChild(RevokeSubCommand.node());
		baseNode.addChild(RemoveSubCommand.node());
		baseNode.addChild(ClearSubCommand.node());
		baseNode.addChild(ListSubCommand.node());
		baseNode.addChild(DumpSubCommand.node());

		rootNode.addChild(baseNode);

	}

	static final class GrantSubCommand {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("grant")
				.then(argument("targets", EntityArgument.entities())
					.then(argument("power", PowerArgumentType.powerOrTag())
						.executes(GrantSubCommand::withDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(GrantSubCommand::withSpecificSource))));

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

			PowerArgumentType.PowerArgument powerArgument = PowerArgumentType.getArgument(commandContext, "power");
			List<PowerEntry<?>> entries = powerArgument.get(commandContext);

			for (var target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				long grantedPowers = entries
					.stream()
					.filter(entry -> powersComponent.grantPower(entry, source))
					.count();

				if (grantedPowers <= 0) {
					continue;
				}

				processedTargets.put(target, grantedPowers);
				powersComponent.updateGrantedPowers();

			}

			switch (powerArgument) {
				case PowerArgumentType.Power power -> {

					HoverEvent hoverEvent = new HoverEvent.ShowText(Component.nullToEmpty(power.reference().toString()));
					Component powerName = entries.getFirst().name().copy().withStyle(style -> style.withHoverEvent(hoverEvent));

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
				case PowerArgumentType.Tag tag -> {

					if (processedTargets.isEmpty()) {

						if (targets.size() == 1) {
							commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.grant.multiple_powers.fail.single_entity", targets.getFirst().getName(), tag.id().toString(), source.toString()));
						}

						else {
							commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.grant.multiple_powers.fail.multiple_entities", targets.size(), tag.id().toString(), source.toString()));
						}

					}

					else {

						if (processedTargets.size() == 1) {
							var processedTarget = processedTargets.object2LongEntrySet().iterator().next();
							commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.grant.multiple_powers.success.single_entity", processedTarget.getKey().getName(), processedTarget.getLongValue(), tag.id().toString(), source.toString()), true);
						}

						else {

							long totalGrantedPowers = processedTargets.values()
								.longStream()
								.sum();

							commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.grant.multiple_powers.success.multiple_entities", processedTargets.size(), tag.id().toString(), totalGrantedPowers, source.toString()), true);

						}

					}

				}
			}

			return processedTargets.size();

		}

	}

	static final class RevokeSubCommand {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("revoke")
				.then(argument("targets", EntityArgument.entities())
					.then(literal("all")
						.executes(RevokeSubCommand::allFromDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(RevokeSubCommand::allFromSpecificSource)))
					.then(argument("power", PowerArgumentType.power())
						.executes(RevokeSubCommand::oneFromDefaultSource)
						.then(argument("source", ResourceLocationArgument.id())
							.executes(RevokeSubCommand::oneFromSpecificSource))));

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
				PowerArgumentType.getPower(commandContext, "power"),
				DEFAULT_SOURCE
			);
		}

		static int oneFromSpecificSource(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(
				commandContext,
				new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets")),
				PowerArgumentType.getPower(commandContext, "power"),
				ResourceLocationArgument.getId(commandContext, "source")
			);
		}

		static int execute(CommandContext<CommandSourceStack> commandContext, List<Entity> targets, @Nullable PowerEntry<?> entry, ResourceLocation source) {

			CommandSourceStack commandSource = commandContext.getSource();
			Object2LongMap<Entity> processedTargets = new Object2LongOpenHashMap<>();

			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				long revokedPowers;

				if (entry != null) {

					if (powersComponent.revokePower(entry, source)) {
						revokedPowers = 1;
					}

					else {
						revokedPowers = 0;
					}

				}

				else {
					revokedPowers = powersComponent.getAllFromSource(source)
						.stream()
						.filter(inner -> powersComponent.revokePower(inner, source))
						.count();
				}

				if (revokedPowers <= 0) {
					continue;
				}

				processedTargets.put(target, revokedPowers);
				powersComponent.updateRevokedPowers();

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {

					if (entry != null) {

						PowerReference reference = entry.reference();
						Component powerName = entry.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(reference.toString()))));

						commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.revoke.fail.single", targets.getFirst().getName(), powerName, source));

					}

					else {
						commandSource.sendFailure(Component.translatable("commands.neo-apoli.power.revoke.all.fail.single", targets.getFirst().getName(), source.toString()));
					}

				}

				else {

					if (entry != null) {

						PowerReference reference = entry.reference();
						Component powerName = entry.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(reference.toString()))));

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
					if (entry != null) {

						PowerReference reference = entry.reference();
						Component powerName = entry.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(reference.toString()))));

						commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.revoke.success.single", processedTarget.getKey().getName(), powerName, source.toString()), true);

					}

					else {
						commandSource.sendSuccess(() -> Component.translatable("commands.neo-apoli.power.revoke.all.success.single", processedTarget.getKey().getName(), processedTarget.getLongValue(), source.toString()), true);
					}

				}

				else {

					if (entry != null) {

						PowerReference reference = entry.reference();
						Component powerName = entry.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(reference.toString()))));

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

	static final class RemoveSubCommand {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("remove")
				.then(argument("targets", EntityArgument.entities())
					.then(argument("power", PowerArgumentType.power())
						.executes(RemoveSubCommand::execute)));

			return node.build();

		}

		static int execute(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			List<Entity> targets = new ObjectArrayList<>(EntityArgument.getEntities(commandContext, "targets"));
			List<Entity> processedTargets = new ObjectArrayList<>();

			PowerEntry<?> entry = PowerArgumentType.getPower(commandContext, "power");
			CommandSourceStack commandSource = commandContext.getSource();

			Component powerName = entry.name().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(entry.toString()))));

			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				Set<ResourceLocation> sources = powersComponent.getSources(entry);

				long removedPowers = sources
					.stream()
					.filter(source -> powersComponent.revokePower(entry, source))
					.count();

				if (removedPowers <= 0) {
					continue;
				}

				processedTargets.add(target);
				powersComponent.updateRevokedPowers();

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

	static final class ClearSubCommand {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("clear")
				.executes(ClearSubCommand::fromSelf)
				.then(argument("targets", EntityArgument.entities())
					.executes(ClearSubCommand::fromSpecified));

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

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				long clearedPowers = 0;

				for (var entry : powersComponent.getAll()) {

					for (var source : powersComponent.getSources(entry)) {

						if (powersComponent.revokePower(entry, source)) {
							clearedPowers++;
						}

					}

				}

				if (clearedPowers <= 0) {
					continue;
				}

				processedTargets.put(target, clearedPowers);
				powersComponent.updateRevokedPowers();

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

	static final class ListSubCommand {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("list")
				.executes(ListSubCommand::fromSelf)
				.then(argument("target", EntityArgument.entity())
					.executes(ListSubCommand::fromSpecified)
					.then(argument("includeSubPowers", BoolArgumentType.bool())
						.executes(ListSubCommand::fromSpecifiedWithSubPowerOption)));

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

			PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
			CommandSourceStack commandSource = commandContext.getSource();

			List<Component> powerTooltips = new ObjectArrayList<>();

			for (var entry : powersComponent.getAll(includeSubPowers)) {

				Power power = entry.power();
				PowerType<?> type = power.getType();

				List<Component> sourceTooltips = powersComponent.getSources(entry)
					.stream()
					.map(Objects::toString)
					.map(source -> Component.literal(source).withStyle())
					.collect(Collectors.toCollection(ObjectArrayList::new));

				Component idTooltip = Component.translatableEscape("commands.neo-apoli.power.list.info.id", Component.literal("\"" + entry.reference().toString() + "\"").withStyle(ChatFormatting.GREEN));
				Component typeTooltip = Component.translatableEscape("commands.neo-apoli.power.list.info.type", Component.literal("\"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, type) + "\"").withStyle(ChatFormatting.GOLD));
				Component joinedSourcesTooltip = Component.translatable("commands.neo-apoli.power.list.info.sources", ComponentUtils.formatList(sourceTooltips, Component.nullToEmpty(", ")));

				Component hoverTooltip = Component.translatable("commands.neo-apoli.power.list.info", idTooltip, typeTooltip, joinedSourcesTooltip);
				HoverEvent hoverEvent = new HoverEvent.ShowText(hoverTooltip);

				powerTooltips.add(entry.name().copy().withStyle(style -> style.withHoverEvent(hoverEvent)));

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

	static final class DumpSubCommand {

		static CommandNode<CommandSourceStack> node() {

			var node = literal("dump")
				.then(argument("power", PowerArgumentType.power())
					.executes(DumpSubCommand::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(DumpSubCommand::withSpecificIndent)));

			return node.build();

		}

		static int withDefaultIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		static int withSpecificIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		static int execute(CommandContext<CommandSourceStack> context, int indent) throws CommandSyntaxException {

			PowerEntry<?> entry = PowerArgumentType.getPower(context, "power");
			Power power = entry.power();

			CommandSourceStack commandSource = context.getSource();
			RegistryOps<JsonElement> jsonOps = commandSource.registryAccess().createSerializationContext(JsonOps.INSTANCE);

			return switch (Power.CODEC.encodeStart(jsonOps, power)) {
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
