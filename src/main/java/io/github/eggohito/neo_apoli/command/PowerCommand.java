package io.github.eggohito.neo_apoli.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.command.argument.PowerArgumentType;
import io.github.eggohito.neo_apoli.power.Power;
import joptsimple.internal.Strings;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PowerCommand {

	public static void register(CommandNode<ServerCommandSource> rootNode) {

		CommandNode<ServerCommandSource> baseNode = literal("power")
			.requires(source -> source.hasPermissionLevel(2))
			.build();

		baseNode.addChild(new DumpNode().build());
		rootNode.addChild(baseNode);

	}

	public static final class DumpNode extends LiteralArgumentBuilder<ServerCommandSource> {

		DumpNode() {
			super("dump");
			this.then(argument("power", PowerArgumentType.power())
				.executes(context -> execute(context, false))
				.then(argument("indent", IntegerArgumentType.integer(0))
					.executes(context -> execute(context, true))));
		}

		private int execute(CommandContext<ServerCommandSource> context, boolean specifiedIndent) throws CommandSyntaxException {

			Power power = PowerArgumentType.getPower(context, "power");
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
