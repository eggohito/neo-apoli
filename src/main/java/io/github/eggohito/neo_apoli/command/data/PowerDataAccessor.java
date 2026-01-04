package io.github.eggohito.neo_apoli.command.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.command.argument.PowerArgumentType;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public record PowerDataAccessor(Entity holder, Power.Instance<?> instance, PowerReference reference, RegistryOps<Tag> ops) implements DataAccessor {

	private static final Dynamic2CommandExceptionType UNGRANTED_ERROR = new Dynamic2CommandExceptionType((a, b) -> Component.translatableEscape("commands.neo-apoli.data.power.ungranted", a, b));
	private static final DynamicCommandExceptionType UNSUPPORTED_ERROR = new DynamicCommandExceptionType(o -> Component.translatableEscape("commands.neo-apoli.data.power.unsupported", o));

	public static final Function<String, DataCommands.DataProvider> PROVIDER = target -> new DataCommands.DataProvider() {

		@Override
		public @NotNull DataAccessor access(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

			Entity holder = EntityArgument.getEntity(context, "entity");
			PowerEntry<?> entry = PowerArgumentType.getPower(context, target);

			Power.Instance<?> instance = NeoApoliEntityComponents.POWERS.get(holder).getNullableInstance(entry);
			RegistryOps<Tag> ops = context.getSource().registryAccess().createSerializationContext(NbtOps.INSTANCE);

			if (instance != null) {
				return new PowerDataAccessor(holder, instance, entry.reference(), ops);
			}

			else {
				throw UNGRANTED_ERROR.create(holder.getName(), entry.reference());
			}

		}

		@Override
		public @NotNull ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> builder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> action) {
			return builder
				.then(literal(NeoApoli.MOD_NAMESPACE)
					.then(literal("power")
						.then(argument("entity", EntityArgument.entity())
							.then(action.apply(argument(target, PowerArgumentType.power()))))));
		}

	};

	@Override
	public void setData(CompoundTag other) throws CommandSyntaxException {

		if (instance().isImmutable()) {
			throw UNSUPPORTED_ERROR.create(reference());
		}

		instance().decodeData(this.ops(), other).getOrThrow(err -> MiscUtil.createCommandException(() -> err));
		instance().syncData();

	}

	@Override
	public @NotNull CompoundTag getData() throws CommandSyntaxException {
		return this.instance().encodeData(this.ops())
			.flatMap(MiscUtil::asCompoundTag)	//	This should already return a compound
			.getOrThrow(err -> MiscUtil.createCommandException(() -> err));
	}

	@Override
	public @NotNull Component getModifiedSuccess() {
		return Component.translatableEscape("commands.neo-apoli.data.power.modified", holder().getName(), reference());
	}

	@Override
	public @NotNull Component getPrintSuccess(Tag nbt) {
		return Component.translatableEscape("commands.neo-apoli.data.power.query", reference(), holder().getName(), NbtUtils.toPrettyComponent(nbt));
	}

	@Override
	public @NotNull Component getPrintSuccess(NbtPathArgument.NbtPath path, double scale, int value) {
		return Component.translatableEscape("commands.neo-apoli.data.power.query.scaled", path.asString(), reference(), holder().getName(), String.format(Locale.ROOT, "%.2f", scale), value);
	}

}
