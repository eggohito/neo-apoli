package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public interface ItemCondition extends Condition {

	Codec<ItemCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(ItemCondition::getType, Type::mapCodec), ConstantItemCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, ItemCondition> STREAM_CODEC = Type.STREAM_CODEC.dispatch(ItemCondition::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.ITEM_STACK);
	}

	enum Kind implements Condition.Kind<ItemCondition> {

		INSTANCE;

		@Override
		public @NotNull Function<String, CommandBuilder> commandBuilder() {
			return conditionKey -> new CommandBuilder() {

				@Override
				public ArgumentBuilder<CommandSourceStack, ?> addArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {
					return builder
						.then(literal("block")
							.then(argument("pos", BlockPosArgument.blockPos())
								.then(this.optionallyAddForkedConditionedLogic(rootNode, argument("slot", SlotArgument.slot()), positive, this::testBlock))))
						.then(literal("entity")
							.then(argument("entity", EntityArgument.entity())
								.then(this.optionallyAddForkedConditionedLogic(rootNode, argument("slot", SlotArgument.slot()), positive, this::testEntity))));
				}

				boolean testBlock(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

					CommandSourceStack source = commandContext.getSource();
					ServerLevel serverLevel = source.getLevel();

					BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(commandContext, "pos");
					int slot = SlotArgument.getSlot(commandContext, "slot");

					if (serverLevel.getBlockEntity(blockPos) instanceof Container container) {

						if (slot >= 0 && slot < container.getContainerSize()) {
							return this.test(commandContext, container.getItem(slot));
						}

						else {
							throw MiscUtil.createCommandException(Component.translatable("commands.item.target.no_such_slot"));
						}

					}

					else {
						throw MiscUtil.createCommandException(Component.translatable("commands.item.target.not_a_container", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
					}

				}

				boolean testEntity(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

					Entity target = EntityArgument.getEntity(commandContext, "target");
					int slot = SlotArgument.getSlot(commandContext, "slot");

					SlotAccess slotAccess = target.getSlot(slot);

					if (slotAccess == SlotAccess.NULL) {
						throw MiscUtil.createCommandException(Component.translatable("commands.item.target.no_such_slot", slot));
					}

					else {
						return this.test(commandContext, slotAccess.get());
					}

				}

				boolean test(CommandContext<CommandSourceStack> commandContext, ItemStack stack) throws CommandSyntaxException {
					return Kind.this.test(
						commandContext,
						conditionKey,
						condition -> Util.getRegisteredName(NeoApoliRegistries.ITEM_CONDITION_TYPE, condition.getType()),
						builder -> builder.withRequired(NeoApoliContextParams.ITEM_STACK, stack)
					);
				}

			};
		}

		@Override
		public ResourceKey<? extends Registry<ItemCondition>> registryKey() {
			return NeoApoliRegistryKeys.ITEM_CONDITION;
		}

		@Override
		public Codec<ItemCondition> codec() {
			return ItemCondition.CODEC;
		}

		@Override
		public String asDisplayString() {
			return "Item condition";
		}

	}

	record Type<C extends ItemCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.ITEM_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ITEM_CONDITION_TYPE);

		@Override
		public ItemCondition.Kind kind() {
			return ItemCondition.Kind.INSTANCE;
		}

	}

}
