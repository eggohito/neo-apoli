package io.github.eggohito.neo_apoli.command.data.accessor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public record PowerDataAccessor(Entity holder, Power.Instance<?> instance, PowerReference reference, RegistryOps<Tag> ops) implements DataAccessor {

	public static final Dynamic2CommandExceptionType UNGRANTED_ERROR = new Dynamic2CommandExceptionType((a, b) -> Component.translatableEscape("commands.neo-apoli.data.power.ungranted", a, b));
	public static final DynamicCommandExceptionType UNSUPPORTED_ERROR = new DynamicCommandExceptionType(o -> Component.translatableEscape("commands.neo-apoli.data.power.unsupported", o));

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
