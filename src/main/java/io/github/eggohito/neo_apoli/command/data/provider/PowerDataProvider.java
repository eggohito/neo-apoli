package io.github.eggohito.neo_apoli.command.data.provider;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.command.data.accessor.PowerDataAccessor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public record PowerDataProvider(String target) implements DataCommands.DataProvider {

    @Override
    public @NotNull PowerDataAccessor access(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        Entity holder = EntityArgument.getEntity(context, "entity");
        PowerHolder<?> powerHolder = PowerArgument.getPower(context, target());

        RegistryOps<Tag> ops = context.getSource().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Power.Instance<?> instance = Powers.getOptional(holder)
            .flatMap(powers -> powers.getOptionalInstance(powerHolder.id()))
            .orElse(null);

        if (instance != null) {
            return new PowerDataAccessor(holder, instance, powerHolder.id(), ops);
        }

        else {
            throw PowerDataAccessor.UNGRANTED_ERROR.create(holder.getName(), powerHolder.id());
        }

    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> builder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> action) {
        return builder
            .then(literal(NeoApoli.id("power").toString())
                .then(argument("entity", EntityArgument.entity())
                    .then(action.apply(argument(target(), PowerArgument.power())))));
    }

}
