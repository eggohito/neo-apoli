package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import io.github.eggohito.neo_apoli.context.ContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;

public class EnumContextParameter<E extends Enum<E>> extends ContextParameter<E> {

	private final Class<E> typeClass;
	private final Codec<E> codec;

	public EnumContextParameter(ResourceLocation name, Class<E> typeClass, Codec<E> codec) {
		super(name);
		this.typeClass = typeClass;
		this.codec = codec;
	}

	@Override
	public @NotNull Class<E> typeClass() {
		return typeClass;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		var valueNode = argument("name", StringArgumentType.word())
			.redirect(baseNode, this::addEnumToSource)
			.build();

		parameterNode.addChild(valueNode);

	}

	private CommandSourceStack addEnumToSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		String name = StringArgumentType.getString(context, "name");

		E value = codec.parse(JavaOps.INSTANCE, name).getOrThrow(err -> MiscUtil.createCommandException(() -> err));
		((ContextBuilderHolder) source).neo_apoli$getContextBuilder().withRequired(this, value);

		return source;

	}

}
