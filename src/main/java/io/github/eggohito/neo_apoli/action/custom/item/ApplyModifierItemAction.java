package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ApplyModifierItemAction extends ItemAction {

	public static final MapCodec<ApplyModifierItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegistryKey.createCodec(RegistryKeys.ITEM_MODIFIER).fieldOf("modifier").forGetter(ApplyModifierItemAction::modifier)
	).apply(instance, ApplyModifierItemAction::new));

	public static final PacketCodec<RegistryByteBuf, ApplyModifierItemAction> PACKET_CODEC = PacketCodec.tuple(
		RegistryKey.createPacketCodec(RegistryKeys.ITEM_MODIFIER), ApplyModifierItemAction::modifier,
		ApplyModifierItemAction::new
	);

	private final RegistryKey<LootFunction> modifier;

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.APPLY_MODIFIER;
	}

	@Override
	protected void impl(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		StackReference stackReference = context.required(ContextParameters.STACK_REFERENCE);
		ItemStack oldStack = stackReference.get();

		//	Since the ID is already validated, there should be no need to validate here...
		LootFunction modifier = serverWorld.getServer().getReloadableRegistries().createRegistryLookup()
			.getEntryOrThrow(this.modifier())
			.value();

		LootWorldContext lootWorldContext = new LootWorldContext.Builder(serverWorld)
			.add(LootContextParameters.ORIGIN, context.optional(ContextParameters.POSITION).orElse(Vec3d.ZERO))
			.addOptional(LootContextParameters.THIS_ENTITY, context.nullable(ContextParameters.THIS_ENTITY))
			.build(LootContextTypes.COMMAND);

		LootContext lootContext = new LootContext.Builder(lootWorldContext).build(Optional.empty());
		lootContext.markActive(LootContext.itemModifier(modifier));

		ItemStack newStack = modifier.apply(oldStack.copy(), lootContext);
		newStack.capCount(newStack.getMaxCount());

		stackReference.set(newStack);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		Optional<RegistryEntryLookup<LootFunction>> optLookup = reporter.getWrapperLookup().flatMap(wrapperLookup -> wrapperLookup.getOptional(this.modifier().getRegistryRef()));
		super.validate(reporter);

		optLookup.ifPresentOrElse(
			lookup -> lookup.getOptional(this.modifier()).ifPresentOrElse(
				reference -> {},
				() -> reporter.report("Item modifier \"" + this.modifier().getValue() + "\" does not exist!")
			),
			() -> reporter.report("Couldn't properly validate whether item modifier \"" + this.modifier().getValue() + "\" exists!")
		);

	}
}
