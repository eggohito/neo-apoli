package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;

public class EnumContextParameter<E extends Enum<E>> extends Context.Parameter<E> {

	private final Class<E> enumClass;
	private final Codec<E> codec;

	public EnumContextParameter(ResourceLocation name, Class<E> enumClass) {
		super(name);
		this.enumClass = enumClass;
		this.codec = CodecUtil.enumType(enumClass);
	}

	@Override
	public @NotNull Class<E> getTypeClass() {
		return enumClass;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		var enumNode = argument("name", StringArgumentType.string())
			.redirect(baseNode, this::addToSource)
			.build();

		parameterNode.addChild(enumNode);

	}

	CommandSourceStack addToSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		String name = StringArgumentType.getString(context, "name");

		E value = codec.parse(JavaOps.INSTANCE, name).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
		source.neo_apoli$getContextBuilder().withRequired(this, value);

		return source;

	}

}
