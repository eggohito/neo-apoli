package io.github.eggohito.neo_apoli.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.command.argument.PowerIdentifierArgumentType;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import joptsimple.internal.Strings;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PowerCommand {

	public static void register(CommandNode<ServerCommandSource> rootNode) {

		CommandNode<ServerCommandSource> baseNode = literal("power")
			.requires(source -> source.hasPermissionLevel(2))
			.build();

		baseNode.addChild(new GrantNode().build());
		baseNode.addChild(new RevokeNode().build());
		baseNode.addChild(new DumpNode().build());

		rootNode.addChild(baseNode);

	}

	public static final class GrantNode extends LiteralArgumentBuilder<ServerCommandSource> {

		GrantNode() {
			super("grant");
			this.then(
				argument("targets", EntityArgumentType.entities())
					.then(argument("power", PowerIdentifierArgumentType.powerId())
						.then(argument("source", IdentifierArgumentType.identifier())
							.executes(this::execute)))
			);
		}

		private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

			List<Entity> targets = new ObjectArrayList<>(EntityArgumentType.getEntities(context, "targets"));
			List<Entity> processedTargets = new ObjectArrayList<>();

			PowerReference id = PowerIdentifierArgumentType.getExistingPowerId(context, "power");
			Identifier source = IdentifierArgumentType.getIdentifier(context, "source");

			ServerCommandSource commandSource = context.getSource();
			Map<Identifier, Collection<PowerReference>> grantedPowers = Map.of(source, List.of(id));

			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				if (!powersComponent.grantPower(id, source)) {
					continue;
				}

				PowersComponent.Synchronizer.GRANT.sync(target, grantedPowers);
				processedTargets.add(target);

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.grant.fail.single", targets.getFirst().getName(), id.toString(), source.toString()));
				}

				else {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.grant.fail.multiple", targets.size(), id.toString(), source.toString()));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.grant.success.single", processedTargets.getFirst().getName(), id.toString(), source.toString()), true);
				}

				else {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.grant.success.multiple", processedTargets.size(), id.toString(), source.toString()), true);
				}

			}

			return processedTargets.size();

		}

	}

	public static final class RevokeNode extends LiteralArgumentBuilder<ServerCommandSource> {

		RevokeNode() {
			super("revoke");
			this.then(
				argument("targets", EntityArgumentType.entities())
					.then(argument("power", PowerIdentifierArgumentType.powerId())
						.then(argument("source", IdentifierArgumentType.identifier())
							.executes(this::execute)))
			);
		}

		private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

			List<Entity> targets = new ObjectArrayList<>(EntityArgumentType.getEntities(context, "targets"));
			List<Entity> processedTargets = new ObjectArrayList<>();

			PowerReference id = PowerIdentifierArgumentType.getExistingPowerId(context, "power");
			Identifier source = IdentifierArgumentType.getIdentifier(context, "source");

			ServerCommandSource commandSource = context.getSource();
			Map<Identifier, Collection<PowerReference>> revokedPowers = Map.of(source, List.of(id));

			for (Entity target : targets) {

				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(target);
				if (!powersComponent.revokePower(id, source)) {
					continue;
				}

				PowersComponent.Synchronizer.REVOKE.sync(target, revokedPowers);
				processedTargets.add(target);

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.revoke.fail.single", targets.getFirst().getName(), id.toString(), source.toString()));
				}

				else {
					commandSource.sendError(Text.translatable("commands.neo-apoli.power.revoke.fail.multiple", targets.size(), id.toString(), source.toString()));
				}

			}

			else {

				if (processedTargets.size() == 1) {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.revoke.success.single", processedTargets.getFirst().getName(), id.toString(), source.toString()), true);
				}

				else {
					commandSource.sendFeedback(() -> Text.translatable("commands.neo-apoli.power.revoke.success.multiple", processedTargets.size(), id.toString(), source.toString()), true);
				}

			}

			return processedTargets.size();

		}

	}

	public static final class DumpNode extends LiteralArgumentBuilder<ServerCommandSource> {

		DumpNode() {
			super("dump");
			this.then(
				argument("power", PowerIdentifierArgumentType.powerId())
					.executes(context -> execute(context, false))
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(context -> execute(context, true)))
			);
		}

		private int execute(CommandContext<ServerCommandSource> context, boolean specifiedIndent) throws CommandSyntaxException {

			PowerReference id = PowerIdentifierArgumentType.getExistingPowerId(context, "power");
			Power power = PowerManager.get(id);

			int indent = specifiedIndent
				? IntegerArgumentType.getInteger(context, "indent")
				: 4;

			ServerCommandSource source = context.getSource();
			RegistryOps<NbtElement> nbtOps = source.getRegistryManager().getOps(NbtOps.INSTANCE);

			return Power.BASE_CODEC.encodeStart(nbtOps, power)
				.ifSuccess(nbtElement -> source.sendFeedback(() -> new NbtTextFormatter(Strings.repeat(' ', indent)).apply(nbtElement), false))
				.ifError(error -> source.sendError(Text.literal(error.message())))
				.mapOrElse(nbtElement -> 1, error -> 0);

		}

	}

}
