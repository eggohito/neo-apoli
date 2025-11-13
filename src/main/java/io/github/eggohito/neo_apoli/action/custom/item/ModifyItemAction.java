package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public record ModifyItemAction(EntityTarget entity, RegistryKey<LootFunction> modifier) implements ItemAction {

	public static final MapCodec<ModifyItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.optionalFieldOf("entity", EntityTarget.THIS).forGetter(ModifyItemAction::entity),
		RegistryKey.createCodec(RegistryKeys.ITEM_MODIFIER).fieldOf("modifier").forGetter(ModifyItemAction::modifier)
	).apply(instance, ModifyItemAction::new));

	public static final PacketCodec<RegistryByteBuf, ModifyItemAction> PACKET_CODEC = PacketCodec.tuple(
		EntityTarget.PACKET_CODEC, ModifyItemAction::entity,
		RegistryKey.createPacketCodec(RegistryKeys.ITEM_MODIFIER), ModifyItemAction::modifier,
		ModifyItemAction::new
	);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.MODIFY;
	}

	@Override
	public void serverExecute(ServerContext context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		StackReference stackReference = context.required(ContextParameters.STACK_REFERENCE);
		ItemStack stack = stackReference.get();

		LootFunction modifier = context.getServer().getReloadableRegistries().createRegistryLookup()
			.getEntryOrThrow(this.modifier())
			.value();

		LootWorldContext lootWorldContext = new LootWorldContext.Builder(context.getWorld())
			.add(LootContextParameters.ORIGIN, context.optional(ContextParameters.ENTITY_POS).orElse(Vec3d.ZERO))
			.addOptional(LootContextParameters.THIS_ENTITY, context.nullable(entity().getParameter()))
			.build(LootContextTypes.COMMAND);

		LootContext lootContext = new LootContext.Builder(lootWorldContext).build(Optional.empty());
		lootContext.markActive(LootContext.itemModifier(modifier));

		ItemStack newStack = modifier.apply(stack.copy(), lootContext);
		newStack.capCount(newStack.getMaxCount());

		stackReference.set(newStack);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		ItemAction.super.validate(reporter);
		Optional<RegistryEntryLookup<LootFunction>> modifierRegistry = reporter
			.getWrapperLookup()
			.flatMap(wrapperLookup -> wrapperLookup.getOptional(this.modifier().getRegistryRef()));

		modifierRegistry.ifPresentOrElse(
			lookup -> lookup.getOptional(this.modifier()).ifPresentOrElse(
				reference -> {},
				() -> reporter.report("Item modifier \"" + this.modifier().getValue() + "\" does not exist!")
			),
			() -> reporter.report("Couldn't properly validate whether item modifier \"" + this.modifier().getValue() + "\" exists!")
		);

	}

}
